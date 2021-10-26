package core.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.HashSet;

public class MasterServer extends Listener {

    Server server;
    public static HashSet<Connection> playersConnected = new HashSet();

    public MasterServer(){
        server = new Server();
        server.addListener(new Listener(){
            //When the client connects to the server add a player entity to the game
            public void connected(Connection c){
                System.out.println("Client connected: " + c.getRemoteAddressTCP().getHostString());
                playersConnected.add(c);

            }

            //This method will run when a client disconnects from the server, remove the character from the game
            public void disconnected(Connection c){
                playersConnected.remove(c);
                System.out.println("Client disconnected! " + c.getID());
            }
        });
        try {
            server.bind(Network.tcpPort, Network.udpPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.start();
        System.out.println("Master Server is running");
    }
}

