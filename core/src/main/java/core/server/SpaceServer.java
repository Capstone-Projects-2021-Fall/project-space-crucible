package core.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import core.game.logic.GameLogic;
import core.server.Network.InputData;
import core.server.Network.StartGame;
import core.wad.funcs.WadFuncs;

import net.mtrop.doom.WadFile;

import java.io.IOException;
import java.util.HashSet;

public class SpaceServer implements Listener {

    //Server Object
    Server server;
    Network.ClientData clientData;
    public static HashSet<Integer> connected = new HashSet<>();

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
        server = new Server(8192,8192) {
            protected Connection newConnection() {
                /* By providing our own connection implementation, we can store per
                // connection state without a connection ID to state look up. */
                return new PlayerConnection();
            }
        };
        clientData = new Network.ClientData();
        GameLogic.isSinglePlayer = false;
        //Loading the wad files
        try {
            //Read the default .WAD. "wads" will eventually be used to store any loaded mods as well as the base .WAD.
            //We only read the .WAD once and take all the information that we need.
            WadFile file = new WadFile(Gdx.files.internal("assets/resource.wad").file());
            Array<WadFile> wads = new Array<>();
            wads.add(file);
            //Load all the level data and the graphics before closing the .WAD
            GameLogic.loadLevels(wads);
            //When we add add-on support we will also close other files inside 'wads"
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        //Load prepare all Entity logic, open game screen and initiate game loop.
        WadFuncs.loadStates();
        WadFuncs.setEntityTypes();
        WadFuncs.loadLevelEffects();
        Network.register(server);

        server.addListener(new Listener(){

            //When the client connects to the server add a player entity to the game
            public void connected(Connection c){
                System.out.println("Client connected to game server: " + c.getID());
                connected.add(c.getID());
                clientData.connected = connected;
                System.out.println("Player connected " + connected.size());
                server.sendToAllTCP(clientData);

            }
            //When the client sends a packet to the server handle it
            public void received(Connection c, Object packetData) {
                PlayerConnection connection = (PlayerConnection) c;

                //update player movement based on the input
                if(packetData instanceof InputData){
                    InputData input = (InputData) packetData;
                    connection.playerInput = input;
                    if(GameLogic.getPlayer(c.getID()) != null) {
                        GameLogic.getPlayer(c.getID()).controls = input.controls;
                        GameLogic.getPlayer(c.getID()).getPos().angle = input.angle;
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
                    Network.CameraData camera = (Network.CameraData) packetData;
                    connection.cameraData = camera;
                }

                else if(packetData instanceof Network.ChatMessage) {
                    server.sendToAllTCP(packetData);
                }
            }
            //This method will run when a client disconnects from the server, remove the character from the game
            public void disconnected(Connection c){
                connected.remove(c.getID());
                clientData.connected = connected;
                server.sendToAllTCP(clientData);
                System.out.println("Client disconnected from game server! " + c.getID());
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
