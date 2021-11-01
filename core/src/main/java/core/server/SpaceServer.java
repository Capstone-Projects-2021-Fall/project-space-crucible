package core.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import core.game.logic.GameLogic;
import core.level.info.LevelData;
import core.server.Network.InputData;
import core.server.Network.StartGame;
import core.wad.funcs.WadFuncs;

import net.mtrop.doom.WadFile;
import org.checkerframework.checker.units.qual.A;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class SpaceServer implements Listener {

    //Server Object
    Server server;
    Network.ClientData clientData;
    public static HashSet<Integer> connected = new HashSet<>();
    public static HashSet<Integer> rconConnected = new HashSet<>();
    public static List<Integer> idToPlayerNum = new LinkedList<>();

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
                        GameLogic.getPlayer(SpaceServer.idToPlayerNum.indexOf(c.getID())).controls = input.controls;
                        GameLogic.getPlayer(SpaceServer.idToPlayerNum.indexOf(c.getID())).getPos().angle = input.angle;
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
            }
            //This method will run when a client disconnects from the server, remove the character from the game
            public void disconnected(Connection c){

                if (connected.contains(c.getID())) {
                    connected.remove(c.getID());
                    clientData.connected = connected;
                    clientData.idToPlayerNum = idToPlayerNum;
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
}
