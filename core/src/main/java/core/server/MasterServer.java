package core.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class MasterServer extends Listener {

    Server server;
    public static HashMap<String, SpaceServer> servers = new HashMap<>();
    public static HashSet<Connection> playersConnected = new HashSet();

    public MasterServer(){
        server = new Server();

        server.addListener(new Listener(){
            //When the client connects to the server add a player entity to the game
            public void connected(Connection c){
                System.out.println("Client connected: " + c.getRemoteAddressTCP().getHostString());
                playersConnected.add(c);

            }
            public void received (Connection connection, Object object) {

                //If the server sends RenderData object update the client's gamescreen
                if(object instanceof Network.CreateLobby){
                    System.out.println("Received request to create lobby");
                    int playerCount = ((Network.CreateLobby) object).playerCount;
                    if(playerCount >= 1){
                        try {
                            int tcpPort = getAvailablePort();
                            int udpPort = getAvailablePort();
                            servers.put(createRandomLobbyCode(), new SpaceServer(playerCount, tcpPort, udpPort));
                            //send port info
                            Network.ServerPorts serverPorts = new Network.ServerPorts();
                            serverPorts.tcpPort = tcpPort;
                            serverPorts.udpPort = udpPort;
                            System.out.println("Sending new server ports to client");
                            connection.sendTCP(serverPorts);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if(object instanceof Network.JoinLobby){
                    String lobbyCode = ((Network.JoinLobby) object).lobbyCode;
                    if(servers.containsKey(lobbyCode)){
                        //add the player to the server
                        servers.get(lobbyCode);

                    }
                }
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

    public static String createRandomLobbyCode(){
        String lobbyCode = "";
        Random rand = new Random();
        for(int i = 7; i > 0; i--){
            int num = rand.nextInt(36);
            if(num < 26){
                lobbyCode += (char) (num + 65);
            }else {
                lobbyCode += num - 25;
            }
        }
        return lobbyCode;
    }
    public static int getAvailablePort() throws IOException {
        var freePort = new ServerSocket(0);
        freePort.close();
        return freePort.getLocalPort();
    }
}

