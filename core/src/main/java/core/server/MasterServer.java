package core.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class MasterServer extends Listener {

    Server server;
    public static HashMap<String, SpaceServer> servers = new HashMap<>();
    public static HashMap<Server, Integer[]> ports = new HashMap<>();
    public static HashSet<Connection> playersConnected = new HashSet<>();

    public MasterServer(){
        server = new Server();
        Network.register(server);

        server.addListener(new Listener(){
            //When the client connects to the server add a player entity to the game
            public void connected(Connection c){
                System.out.println("Client connected: " + c.getRemoteAddressTCP().getHostString());
                playersConnected.add(c);

            }
            public void received (Connection connection, Object object) {

                //If the server sends RenderData object update the client's gamescreen
                if(object instanceof Network.CreateLobby){
                    int playerCount = ((Network.CreateLobby) object).playerCount;
                    if(playerCount >= 1){
                        try {
                            int tcpPort = getAvailablePort();
                            int udpPort = getAvailablePort();
                            String lobbyCode = createRandomLobbyCode();
                            servers.put(lobbyCode, new SpaceServer(playerCount, tcpPort, udpPort));
                            ports.put(server, new Integer[]{tcpPort, udpPort});
                            //send port info
                            Network.ServerDetails serverDetails = new Network.ServerDetails();
                            serverDetails.tcpPort = tcpPort;
                            serverDetails.udpPort = udpPort;
                            serverDetails.lobbyCode = lobbyCode;
                            System.out.println("Sending ports");
                            connection.sendTCP(serverDetails);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if(object instanceof Network.JoinLobby){
                    String lobbyCode = ((Network.JoinLobby) object).lobbyCode;
                    if(servers.containsKey(lobbyCode)){
                        //add the player to the server
                        SpaceServer spaceServer= servers.get(lobbyCode);
                        Integer [] port = ports.get(spaceServer.server);
                        Network.ServerDetails serverDetails = new Network.ServerDetails();
                        serverDetails.tcpPort = port[0];
                        serverDetails.udpPort = port[1];
                        serverDetails.lobbyCode = lobbyCode;
                        connection.sendTCP(serverDetails);
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

