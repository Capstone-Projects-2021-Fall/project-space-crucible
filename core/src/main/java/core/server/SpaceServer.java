package core.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import core.game.logic.GameLogic;
import core.level.info.LevelData;
import core.server.Network.InputData;
import core.server.Network.StartGame;
import core.wad.funcs.WadFuncs;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class SpaceServer implements Listener {

    //Server Object
    Server server;
    public static Network.ClientData clientData;
    public static HashSet<Integer> connected = new HashSet<>();
    public static HashSet<Integer> rconConnected = new HashSet<>();
    public static HashSet<Integer> disconnected = new HashSet<>();
    public static List<Integer> idToPlayerNum = new LinkedList<>();
    public static HashMap<Integer, String> ips = new HashMap<>();

    //Game loop
    Thread gameLoop = new Thread() {
        @Override
        public void run() {
            GameLogic.start();
        }

        @Override
        public void interrupt() {
            GameLogic.stop();
        }
    };

    public SpaceServer(int tcpPort) throws IOException {
        idToPlayerNum.add(-1);
        server = new Server(8192,8192) {
            protected Connection newConnection() {
                /* By providing our own connection implementation, we can store per
                // connection state without a connection ID to state look up. */
                return new PlayerConnection();
            }
        };
        clientData = new Network.ClientData();
        GameLogic.isSinglePlayer = false;

        //Load prepare all Entity logic, open game screen and initiate game loop.
        WadFuncs.loadStates();
        WadFuncs.setEntityTypes();
        WadFuncs.loadLevelEffects();
        Network.register(server);

        server.addListener(new Listener(){

            //When the client connects to the server add a player entity to the game
            public void connected(Connection c){
                ips.put(c.getID(), c.getRemoteAddressTCP().getAddress().toString());
                server.sendToTCP(c.getID(), new Network.PromptConnectionType());

            }
            //When the client sends a packet to the server handle it
            public void received(Connection c, Object packetData) {
                PlayerConnection connection = (PlayerConnection) c;

                //update player movement based on the input
                if(packetData instanceof InputData){
                    InputData input = (InputData) packetData;
                    connection.playerInput = input;

                    if(GameLogic.getPlayer(SpaceServer.idToPlayerNum.indexOf(c.getID())) != null) {
                        Objects.requireNonNull(GameLogic.getPlayer(SpaceServer.idToPlayerNum.indexOf(c.getID()))).controls = input.controls;
                        Objects.requireNonNull(GameLogic.getPlayer(SpaceServer.idToPlayerNum.indexOf(c.getID()))).getPos().angle = input.angle;
                    }
                }
                else if(packetData instanceof StartGame){
                    if(((StartGame) packetData).startGame && !gameLoop.isAlive()){
                        System.out.println("Initializing server and starting game loop");
                        StartGame start = new StartGame();
                        start.startGame = true;
                        server.sendToAllTCP(start);
                        GameLogic.server = server;
                        GameLogic.currentLevel = GameLogic.levels.get(1);
                        GameLogic.loadEntities(GameLogic.currentLevel, false);
                        gameLoop.start();
                    }
                }

                else if(packetData instanceof Network.CameraData) {
                    connection.cameraData = (Network.CameraData) packetData;
                }

                else if(packetData instanceof Network.ChatMessage) {
                    server.sendToAllTCP(packetData);
                    sendToRCON(
                            ((Network.ChatMessage) packetData).sender + ": "
                            + ((Network.ChatMessage) packetData).message
                    );
                }


                //Send level data upon creation
                else if (packetData instanceof Network.LevelInfo) {
                    if (!GameLogic.levels.containsKey(((Network.LevelInfo) packetData).levelNumber)) {
                        GameLogic.levels.put(((Network.LevelInfo) packetData).levelNumber, new LevelData());
                        GameLogic.levels.get(((Network.LevelInfo) packetData).levelNumber).levelnumber = ((Network.LevelInfo) packetData).levelNumber;
                    }

                    GameLogic.levels.get(((Network.LevelInfo) packetData).levelNumber)
                            .name = (((Network.LevelInfo) packetData).levelName);

                    GameLogic.levels.get(((Network.LevelInfo) packetData).levelNumber)
                            .midi = (((Network.LevelInfo) packetData).levelMIDI);
                }

                else if (packetData instanceof Network.AddTile) {
                    if (!GameLogic.levels.containsKey(((Network.AddTile) packetData).levelNumber)) {
                        GameLogic.levels.put(((Network.AddTile) packetData).levelNumber, new LevelData());
                        GameLogic.levels.get(((Network.AddTile) packetData).levelNumber).levelnumber = ((Network.LevelInfo) packetData).levelNumber;
                    }

                    GameLogic.levels.get(((Network.AddTile) packetData).levelNumber).getTiles()
                            .add(((Network.AddTile) packetData).levelTile);
                }

                else if (packetData instanceof Network.AddObject) {
                    if (!GameLogic.levels.containsKey(((Network.AddObject) packetData).levelNumber)) {
                        GameLogic.levels.put(((Network.AddObject) packetData).levelNumber, new LevelData());
                        GameLogic.levels.get(((Network.AddObject) packetData).levelNumber).levelnumber = ((Network.LevelInfo) packetData).levelNumber;
                    }

                    GameLogic.levels.get(((Network.AddObject) packetData).levelNumber).getObjects()
                            .add(((Network.AddObject) packetData).levelObject);
                }

                else if (packetData instanceof Network.CheckConnection) {
                    if (((Network.CheckConnection) packetData).type == Network.ConnectionType.PLAYER) {
                        System.out.println("Client connected to game server: " + c.getID());
                        connected.add(c.getID());
                        idToPlayerNum.add(c.getID());
                        clientData.connected = connected;
                        clientData.idToPlayerNum = idToPlayerNum;
                        System.out.println("Player connected " + idToPlayerNum.indexOf(c.getID()));
                        server.sendToAllTCP(clientData);
                    } else if (((Network.CheckConnection) packetData).type == Network.ConnectionType.RCON) {
                        rconConnected.add(c.getID());
                    }
                }

                //RCON Command
                else if (packetData instanceof Network.RCONMessage) {
                    handleRCON(((Network.RCONMessage) packetData).message);
                }
            }
            //This method will run when a client disconnects from the server, remove the character from the game
            public void disconnected(Connection c){

                if (connected.contains(c.getID())) {
                    disconnected.add(c.getID());
                    connected.remove(c.getID());
                    clientData.connected = connected;
                    //clientData.idToPlayerNum = idToPlayerNum;
                    server.sendToAllTCP(clientData);
                    System.out.println("Client disconnected from game server! " + c.getID());
                } else if (rconConnected.contains(c.getID())) {
                    rconConnected.remove(c.getID());
                }
            }
        });
        server.bind(tcpPort);
        server.start();
        System.out.println("Server is running");
    }

    public static class PlayerConnection extends Connection{
        public InputData playerInput;
        public Network.CameraData cameraData;
    }

    private void handleRCON(String message) {

        String command = message.contains(" ") ? message.substring(0, message.indexOf(' ')) : message;

        switch(command) {

            case "ping":
                sendToRCON("ping");
                break;

            case "say":
                if (!message.contains(" ")) {return;}
                String chat = message.substring(message.indexOf("say ") + 4);
                sendToRCON("Server: " + chat);
                Network.ChatMessage cm = new Network.ChatMessage();
                cm.sender = "Server";
                cm.message = chat;
                server.sendToAllTCP(cm);
                break;

            case "level":
                try {
                    int level  = Integer.parseInt(message.substring(message.indexOf(' ')+1));

                    if (!GameLogic.levels.containsKey(level)) {
                        sendToRCON("Server has no level " + level);
                        return;
                    }
                    GameLogic.readyChangeLevel(GameLogic.levels.get(level));
                    sendToRCON("Switching to level " + level);

                } catch(NumberFormatException n) {
                    sendToRCON("Invalid level number, try again.");
                }
                break;

            case "skill":
                try {
                    int skill = Integer.parseInt(message.substring(message.indexOf(' ')+1));
                    if (skill > GameLogic.NIGHTMARE || skill < GameLogic.BABY) {throw new NumberFormatException();}
                    else {
                        sendToRCON("Changing skill to skill " + skill);
                        GameLogic.difficulty = skill;
                    }

                } catch(NumberFormatException n) {
                    sendToRCON("Unknown skill number, please try again.");
                }

            case "statuses":
                sendToRCON("Connected Players:");
                for (Integer i : connected) {
                    sendToRCON("Connection " + i + ": " + ips.get(i));
                }

                sendToRCON("Connected RCON:");
                for (Integer i : rconConnected) {
                    sendToRCON("Connection " + i + ": " + ips.get(i));
                }

                sendToRCON("Disconnected:");
                for (Integer i : disconnected) {
                    sendToRCON("Connection " + i + ": " + ips.get(i));
                }
        }
    }

    private void sendToRCON(String send) {

        Network.RCONMessage m = new Network.RCONMessage();
        m.message = send;

        for (Integer i : rconConnected) {
            server.sendToTCP(i, m);
        }
    }
}
