package core.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import core.game.entities.Entity;
import core.game.entities.PlayerPawn;
import core.game.logic.GameLogic;
import core.server.Network.InputData;
import core.server.Network.RenderData;
import core.wad.funcs.WadFuncs;
import net.mtrop.doom.WadFile;


import java.io.File;
import java.io.IOException;
import java.util.HashSet;

public class SpaceServer extends Listener {

    //Server Object
    Server server;
    HashSet<Connection> connected = new HashSet();

    //Game loop

    Thread gameLoop = new Thread() {
        @Override
        public void run() {
            GameLogic.start(server);
        }

        @Override
        public void interrupt() {
            GameLogic.stop();
        }
    };

    public SpaceServer(int playerCount) throws IOException {
        server = new Server() {
            protected Connection newConnection() {
                // By providing our own connection implementation, we can store per
                // connection state without a connection ID to state look up.
                return new PlayerConnection();
            }
        };

        GameLogic.isSinglePlayer = false;

        //Loading the wad files
        try {

            //Read the default .WAD. "wads" will eventually be used to store any loaded mods as well as the base .WAD.
            //We only read the .WAD once and take all the information that we need.
            System.out.println(System.getProperty("user.dir"));
            WadFile file = new WadFile(Gdx.files.internal("assets/resource.wad").file());
            Array<WadFile> wads = new Array<>();
            wads.add(file);

            //Load all of the level data and the graphics before closing the .WAD
            GameLogic.loadLevels(file, wads);

            //When we add add-on support we will also close other files inside of 'wads"
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        //Load prepare all Entity logic, open game screen and initiate game loop.
        WadFuncs.loadStates();
        WadFuncs.setEntityTypes();
        GameLogic.loadEntities(GameLogic.currentLevel);
        Network.register(server);

        server.addListener(new Listener(){

            //When the client connects to the server add a player entity to the game
            public void connected(Connection c){
                System.out.println("Client connected: " + c.getRemoteAddressTCP().getHostString());
                PlayerConnection connection = (PlayerConnection) c;
                connected.add(c);
                //Wait for everyone to connect
                if(connected.size() == playerCount && !gameLoop.isAlive()){
                    GameLogic.currentLevel = GameLogic.levels.get(1);
                    gameLoop.start();
                }

            }
            //When the client sends a packet to the server handle it
            public void received(Connection c, Object packetData) {
                PlayerConnection connection = (PlayerConnection) c;

                //update player movement based on the input
                if(packetData instanceof InputData){
                    InputData input = (InputData) packetData;
                    connection.playerInput = input;
                    GameLogic.controls = input.controls;
                    System.out.println("Angle received: " + input.angle);
                    GameLogic.getPlayer(0).getPos().angle = input.angle;
                }
            }
            //This method will run when a client disconnects from the server, remove the character from the game
            public void disconnected(Connection c){
                PlayerConnection connection = (PlayerConnection) c;
                System.out.println("Client disconnected! " + c.getID());
            }
        });
        server.bind(Network.tcpPort, Network.udpPort);
        server.start();
        System.out.println("Server is running");
    }

    static class PlayerConnection extends Connection{
        public InputData playerInput;
    }
}
