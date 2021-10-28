package core.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class MasterServer implements Listener {

    Server server;
    public HashMap<String, SpaceServer> servers = new HashMap<>();
    public HashMap<SpaceServer, Integer[]> ports = new HashMap<>();
    public HashSet<Connection> playersConnected = new HashSet<>();

    public MasterServer(){
        server = new Server();
        Network.register(server);

        server.addListener(new Listener(){
            //When the client connects to the server add a player entity to the game
            public void connected(Connection c){
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
                            SpaceServer spaceServer = new SpaceServer(playerCount, tcpPort, udpPort);
                            servers.put(lobbyCode, spaceServer);
                            ports.put(spaceServer, new Integer[]{tcpPort, udpPort});
                            //send port info
                            Network.ServerDetails serverDetails = new Network.ServerDetails();
                            serverDetails.tcpPort = tcpPort;
                            serverDetails.udpPort = udpPort;
                            serverDetails.lobbyCode = lobbyCode;
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
                        Network.ValidLobby validLobby = new Network.ValidLobby();
                        validLobby.valid = spaceServer != null;
                        connection.sendTCP(validLobby);
                        if(!validLobby.valid) return;
                        Integer [] port = ports.get(spaceServer);
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
        for(int i = 5; i > 0; i--){
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

