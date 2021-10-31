package core.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class MasterServer implements Listener {

    Server server;
    public HashMap<String, Integer> servers = new HashMap<>();
    public HashSet<Connection> playersConnected = new HashSet<>();
    public ArrayList<Integer> availablePorts = new ArrayList<>();

    public MasterServer(int minPort, int maxPort){
        server = new Server();
        Network.register(server);
        for(int i = minPort; i <= maxPort; i++){
            availablePorts.add(i);
        }
        server.addListener(new Listener(){
            //When the client connects to the server add a player entity to the game
            public void connected(Connection c){
                playersConnected.add(c);

            }
            public void received (Connection connection, Object object) {
                //If the server sends RenderData object update the client's gamescreen
                if(object instanceof Network.CreateLobby){
                    int tcpPort = 0;
                    for(int port : availablePorts ){
                        if(!servers.containsValue(port) && !isPortAvailable(port)) {
                            tcpPort = port;
                            break;
                        }
                    }
                    if(tcpPort == 0) {
                        System.out.println("All the lobbies are taken");
                        System.exit(0);
                    }
                    String lobbyCode = createRandomLobbyCode();
                    servers.put(lobbyCode, tcpPort);
                    //send port info
                    Network.ServerDetails serverDetails = new Network.ServerDetails();
                    serverDetails.tcpPort = tcpPort;
                    serverDetails.lobbyCode = lobbyCode;
                    connection.sendTCP(serverDetails);
                }
                if(object instanceof Network.JoinLobby){
                    String lobbyCode = ((Network.JoinLobby) object).lobbyCode;
                    System.out.println(lobbyCode);
                    //If user lobby code was correct find the tcpPort and send it to the user
                    if(servers.containsKey(lobbyCode)){
                        int tcpPort = servers.get(lobbyCode);
                        Network.ValidLobby validLobby = new Network.ValidLobby();
                        validLobby.valid = tcpPort != 0;
                        connection.sendTCP(validLobby);
                        if(!validLobby.valid) return;
                        Network.ServerDetails serverDetails = new Network.ServerDetails();
                        serverDetails.tcpPort = tcpPort;
                        serverDetails.lobbyCode = lobbyCode;
                        connection.sendTCP(serverDetails);
                    } else {
                        Network.ValidLobby validLobby = new Network.ValidLobby();
                        validLobby.valid = false;
                        connection.sendTCP(validLobby);
                    }
                }
            }
            //This method will run when a client disconnects from the server, remove the character from the game
            public void disconnected(Connection c){
                playersConnected.remove(c);
            }
        });
        try {
            server.bind(Network.tcpPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.start();
        System.out.println("Master Server is running");
    }

    public static String createRandomLobbyCode(){
        String lobbyCode = "";
        Random rand = new Random();
        for(int i = 4; i > 0; i--){
            int num = rand.nextInt(36);
            if(num < 26){
                lobbyCode += (char) (num + 65);
            }else {
                lobbyCode += num - 26;
            }
        }
        return lobbyCode;
    }

    public static boolean isPortAvailable(int port) {
        boolean freePort;
        try (var ignored = new ServerSocket(port)) {
            freePort = true;
        } catch (IOException e) {
            freePort = false;
        }
        return freePort;
    }
}

