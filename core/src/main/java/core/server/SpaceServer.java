package core.server;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import core.game.entities.Entity;
import core.game.logic.GameLogic;
import core.level.info.LevelData;
import core.level.info.LevelObject;
import core.server.Network.InputData;
import core.server.Network.StartGame;
import core.wad.funcs.WadFuncs;
import core.server.Network.SendPing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class SpaceServer implements Listener {

    //Server Object
    Server server;
    Client serverClient;
    public static String ip = "100.19.127.86";
    public static Network.ClientData clientData;
    public static HashSet<Integer> connected = new HashSet<>();
    public static HashMap<Integer, String> playerNames = new HashMap<>();
    public static HashSet<Integer> rconConnected = new HashSet<>();
    public static HashSet<Integer> disconnected = new HashSet<>();
    public static List<Integer> idToPlayerNum = new LinkedList<>();
    public static HashMap<Integer, String> ips = new HashMap<>();
    boolean gameStartedByHost = false;
    final public Date startTime;
    final private SpaceServer spaceServer = this;
    public long packetsReceived = 0;
    public long packetsSent = 0;
    File serverReport;
    final long startTimer;
    Writer fileWriter;
    //Game loop
    Thread gameLoop;

    public SpaceServer(int tcpPort) throws IOException {
        startTimer = System.nanoTime();
        serverReport = Gdx.files.internal("server-reports/gameServer-" + tcpPort + ".txt").file();
        if(serverReport.createNewFile()){
            System.out.println("Created new server report file called gameServer-" + tcpPort);
            fileWriter = new FileWriter(serverReport, true);
        }else{
            System.out.println("gameServer-"+tcpPort+" file already exists overwriting the file.");
            fileWriter = new FileWriter(serverReport, false);
        }
        createGameLoopThread();
        startTime = new Date();
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
        WadFuncs.loadLevelEffects();
        Network.register(server);

        server.addListener(new Listener(){

            //When the client connects to the server add a player entity to the game
            public void connected(Connection c){
                long duration = (System.nanoTime()-startTimer)/1000000000;
                ips.put(c.getID(), c.getRemoteAddressTCP().getAddress().toString());
                server.sendToTCP(c.getID(), new Network.PromptConnectionType());
                packetsSent++;
                try {
                    fileWriter.write(duration + ": A new player has joined the server: Player " + c.getID() + "\n");
                    fileWriter.flush();
                } catch (IOException e) {
                    System.out.println("Could not write to file " + serverReport.getName());
                    e.printStackTrace();
                }
            }
            //When the client sends a packet to the server handle it
            public void received(Connection c, Object packetData) {
                long duration = (System.nanoTime()-startTimer)/1000000000;
                packetsReceived++;
                PlayerConnection connection = (PlayerConnection) c;

                //update player movement based on the input
                if(packetData instanceof InputData){
                    InputData input = (InputData) packetData;
                    connection.playerInput = input;

                    if(GameLogic.getPlayer(SpaceServer.idToPlayerNum.indexOf(c.getID())) != null) {
                        try {
                            fileWriter.write(duration + ": Received input data from Player " + c.getID() + ": " + Arrays.toString(input.controls) + ", angle: " + input.angle + "\n");
                            fileWriter.flush();
                        } catch (IOException e) {
                            System.out.println("Could not write to file " + serverReport.getName());
                            e.printStackTrace();
                        }
                        Objects.requireNonNull(GameLogic.getPlayer(SpaceServer.idToPlayerNum.indexOf(c.getID()))).controls = input.controls;
                        Objects.requireNonNull(GameLogic.getPlayer(SpaceServer.idToPlayerNum.indexOf(c.getID()))).getPos().angle = input.angle;
                    }
                }
                else if(packetData instanceof Network.SendPlayerName){
                    int i = 1;
                    String name = ((Network.SendPlayerName) packetData).name;
                    if(playerNames.containsValue(name) ){
                        while (playerNames.containsValue(name)) {
                            name = name.concat(String.valueOf(i));
                            i++;
                        }
                    }
                    playerNames.put(c.getID(), name);
                }
                else if(packetData instanceof StartGame){
                    if(((StartGame) packetData).startGame && !gameLoop.isAlive()){
                        System.out.println("Initializing server and starting game loop");
                        StartGame start = new StartGame();
                        start.startGame = true;
                        start.levelnum = 1;
                        gameStartedByHost = true;
                        server.sendToAllTCP(start);
                        packetsSent += server.getConnections().size();
                        GameLogic.spaceServer = spaceServer;
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
                    packetsSent += server.getConnections().size();
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
                        GameLogic.levels.get(((Network.AddTile) packetData).levelNumber).levelnumber = ((Network.AddTile) packetData).levelNumber;
                    }
                    GameLogic.levels.get(((Network.AddTile) packetData).levelNumber).getTiles()
                            .add(((Network.AddTile) packetData).levelTile);
                }
                else if (packetData instanceof Network.AddObject) {
                    if (!GameLogic.levels.containsKey(((Network.AddObject) packetData).levelNumber)) {
                        GameLogic.levels.put(((Network.AddObject) packetData).levelNumber, new LevelData());
                        GameLogic.levels.get(((Network.AddObject) packetData).levelNumber).levelnumber = ((Network.AddObject) packetData).levelNumber;
                    }
                    GameLogic.levels.get(((Network.AddObject) packetData).levelNumber).getObjects()
                            .add(((Network.AddObject) packetData).levelObject);
                }
                else if (packetData instanceof Network.GameEntity) {
                    GameLogic.entityTable.put(((Network.GameEntity) packetData).name, ((Network.GameEntity) packetData).spawner);
                    if (((Network.GameEntity) packetData).mapID > -1) {
                        GameLogic.mapIDTable.put(((Network.GameEntity) packetData).mapID, ((Network.GameEntity) packetData).spawner);
                        System.out.println(((Network.GameEntity) packetData).mapID);
                    }
                }
                else if (packetData instanceof Network.State) {
                    System.out.println("adding state");
                    GameLogic.stateList.add(((Network.State) packetData).state);
                    System.out.println("StateList: " + GameLogic.stateList.size());
                }
                else if (packetData instanceof Network.CheckConnection) {
                    if (((Network.CheckConnection) packetData).type == Network.ConnectionType.PLAYER) {
                        System.out.println("Client connected to game server: " + c.getID() + "Username: " + c.toString());
                        connected.add(c.getID());
                        idToPlayerNum.add(c.getID());
                        clientData.connected = connected;
                        clientData.idToPlayerNum = idToPlayerNum;
                        clientData.playerNames = playerNames;
                        System.out.println("Player connected " + idToPlayerNum.indexOf(c.getID()));

                        if (gameStartedByHost) {
                            for (LevelObject lo : GameLogic.currentLevel.getObjects()) {
                                if (lo.type == 0 && lo.tag == idToPlayerNum.indexOf(c.getID())) {
                                    GameLogic.newEntityQueue.add(GameLogic.mapIDTable.get(0)
                                            .spawnEntity(new Entity.Position(lo.xpos, lo.ypos, lo.angle), lo.tag, lo.layer, lo.ambush));
                                    break;
                                }
                            }
                        }
                        server.sendToAllTCP(clientData);
                        packetsSent += server.getConnections().size();
                        idToPlayerNum.forEach(integer -> System.out.println("Player " + idToPlayerNum.indexOf(integer) + "'s username is " + playerNames.get(integer)));
                        if(gameStartedByHost){
                            System.out.println("Game started by host is true sending tcp");
                            StartGame start = new StartGame();
                            start.startGame = true;
                            start.levelnum = GameLogic.currentLevel.levelnumber;
                            server.sendToTCP(c.getID(), start);
                            packetsSent += server.getConnections().size();
                        }
                    }
                    else if (((Network.CheckConnection) packetData).type == Network.ConnectionType.RCON) {
                        rconConnected.add(c.getID());
                    }
                }
                //RCON Command
                else if (packetData instanceof Network.RCONMessage) {
                    handleRCON(((Network.RCONMessage) packetData).message);
                }
                else if (packetData instanceof SendPing){
//                    System.out.println(((SendPing) packetData).ping);
                }
            }
            //This method will run when a client disconnects from the server, remove the character from the game
            public void disconnected(Connection c){
                long duration = (System.nanoTime()-startTimer)/1000000000;
                int connectionID = c.getID();
                if (connected.contains(connectionID)) {
                    try {
                        fileWriter.write(duration + ": A player has left the server: Player " + connectionID + "\n");
                        fileWriter.flush();
                    } catch (IOException e) {
                        System.out.println("Could not write to file " + serverReport.getName());
                        e.printStackTrace();
                    }
                    disconnected.add(connectionID);
                    connected.remove(connectionID);
                    clientData.connected = connected;
//                    idToPlayerNum.remove((Object) connectionID);
                    playerNames.remove(connectionID);
                    if(connected.size() == 0){
                        idToPlayerNum.clear();
                        idToPlayerNum.add(-1);
                        playerNames.clear();
                        try {
                            fileWriter.write(duration +": All players left, the server is now empty and ready to be reused\n");
                            fileWriter.flush();
                        } catch (IOException e) {
                            System.out.println("Could not write to file " + serverReport.getName());
                            e.printStackTrace();
                        }
                        //Ping the master server that this server is empty
                        Network.OpenLobby openLobby = new Network.OpenLobby();
                        openLobby.tcpPort = tcpPort;
                        gameStartedByHost = false;
                        serverClient.sendTCP(openLobby);
                        //Stop the GameLogic and restart the thread so when a new lobby starts everything gets reset
                        try {GameLogic.stop();} catch (NullPointerException ignored){}
                        createGameLoopThread();
                    } else {
                        if (gameStartedByHost) {
                            GameLogic.getPlayer(idToPlayerNum.indexOf(c.getID()))
                                    .setSpeed(GameLogic.getPlayer(idToPlayerNum.indexOf(c.getID())).getSpeed() / 40);
                        } else {
                            SpaceServer.idToPlayerNum.remove((Integer)c.getID());
                        }
                        clientData.idToPlayerNum = idToPlayerNum;
                        clientData.playerNames = playerNames;
                        server.sendToAllTCP(clientData);
                        packetsSent += server.getConnections().size();
                        System.out.println("Client disconnected from game server! " + connectionID);
                    }
                } else if (rconConnected.contains(connectionID)) {
                    rconConnected.remove(connectionID);
                }
                idToPlayerNum.forEach(integer -> System.out.println("Player " + idToPlayerNum.indexOf(integer) + "'s username is " + playerNames.get(integer)));
            }
        });
        server.bind(tcpPort);
        server.start();

        //Start a connection to the master server as a client
        serverClient = new Client(8192, 8192);
        serverClient.start();
        //register the packets
        Network.register(serverClient);

        serverClient.addListener(new ThreadedListener(new Listener() {
            public void connected(Connection connection) {
                Network.SendServerInfo serverInfo = new Network.SendServerInfo();
                serverInfo.tcpPort = tcpPort;
                serverClient.sendTCP(serverInfo);
            }
            public void received (Connection connection, Object packetData) {
            }
        }));
        try {
            serverClient.connect(5000, ip, Network.tcpPort);
        } catch (IOException e) {
            System.out.println("Master Server is not running!");
            serverClient = null;
        }
    }

    private void createGameLoopThread() {
        gameLoop = new Thread() {
            @Override
            public void run() {
                GameLogic.start();
            }

            @Override
            public void interrupt() {
                GameLogic.stop();
            }
        };
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
                packetsSent += server.getConnections().size();
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
                break;

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
                break;

            case "packetinfo":
                Date now = new Date();
                long elapsedTime = now.getTime() - startTime.getTime();
                double elapsedSeconds = elapsedTime / 1000f;
                sendToRCON("Packets sent: " + packetsSent);
                sendToRCON("Packets received: " + packetsReceived);
                sendToRCON("Sent per second (avg): " + (packetsSent / elapsedSeconds));
                sendToRCON("Received per second (avg): " + (packetsReceived / elapsedSeconds));
                break;
        }
    }

    private void sendToRCON(String send) {

        Network.RCONMessage m = new Network.RCONMessage();
        m.message = send;

        for (Integer i : rconConnected) {
            server.sendToTCP(i, m);
            packetsSent++;
        }
    }
}
