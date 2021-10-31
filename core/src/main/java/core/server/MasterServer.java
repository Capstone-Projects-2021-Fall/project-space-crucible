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
    public HashMap<String, ServerEntry> servers = new HashMap<>();
    public HashMap<String, Integer> ports = new HashMap<>();
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
                        if(!ports.containsValue(port) && !isPortAvailable(port)) {
                            tcpPort = port;
                            break;
                        }
                    }
                    if(tcpPort == 0) {
                        System.out.println("All the lobbies are taken");
                        System.exit(0);
                    }
                    String lobbyCode = createRandomLobbyCode();

                    ServerEntry entry = new ServerEntry(tcpPort,
                            ((Network.CreateLobby) object).names, ((Network.CreateLobby) object).hashes);

                    servers.put(lobbyCode, entry);
                    ports.put(lobbyCode, tcpPort);
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

                        //Check if lobby code exists and is not 0
                        ServerEntry serverEntry = servers.get(lobbyCode);
                        int tcpPort = serverEntry.port;
                        Network.ValidLobby validLobby = new Network.ValidLobby();
                        validLobby.valid = true;
                        validLobby.reason = "";

                        if (tcpPort == 0) {
                            validLobby.valid = false;
                            validLobby.reason = "Invalid TCP Port.";
                        }

                        if(!validLobby.valid)  {
                            connection.sendTCP(validLobby);
                            return;
                        }

                        if (serverEntry.getFiles().size() != ((Network.JoinLobby) object).names.size()) {
                            validLobby.valid = false;
                            validLobby.reason = "Lobby requires these WADS:\n" + serverEntry.getFiles().toString();
                            connection.sendTCP(validLobby);
                            System.out.println("No bueno. 2");
                            return;
                        }

                        for (int i = 0; i < serverEntry.getFiles().size(); i++) {

                            String serverFile = serverEntry.getFiles().get(i);
                            String serverHash = serverEntry.getHashes().get(i);
                            String clientFile = ((Network.JoinLobby) object).names.get(i);
                            String clientHash = ((Network.JoinLobby) object).hashes.get(i);

                            if (!serverFile.equals(clientFile)) {
                                validLobby.valid = false;
                                validLobby.reason = "Missing .WAD " + serverFile;
                                connection.sendTCP(validLobby);
                                System.out.println("No bueno. 3");
                                return;
                            }

                            if (!serverHash.equals(clientHash)) {
                                validLobby.valid = false;
                                validLobby.reason = "Invalid hash for " + serverFile;
                                connection.sendTCP(validLobby);
                                System.out.println("No bueno. 4");
                                return;
                            }
                        }

                        connection.sendTCP(validLobby);
                        System.out.println("Sending details!");
                        Network.ServerDetails serverDetails = new Network.ServerDetails();
                        serverDetails.tcpPort = tcpPort;
                        serverDetails.lobbyCode = lobbyCode;
                        connection.sendTCP(serverDetails);
                    } else {
                        Network.ValidLobby validLobby = new Network.ValidLobby();
                        validLobby.valid = false;
                        validLobby.reason = "No lobby with that key exists";
                        connection.sendTCP(validLobby);
                        System.out.println("No bueno. 5");
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

    public static class ServerEntry {
        private int port;
        private ArrayList<String> files;
        private ArrayList<String> hashes;

        public ServerEntry(int port, ArrayList<String> files, ArrayList<String> hashes) {
            this.port = port;
            this.files = files;
            this.hashes = hashes;
        }

        public int getPort() {
            return port;
        }

        public ArrayList<String> getFiles() {
            return files;
        }

        public ArrayList<String> getHashes() {
            return hashes;
        }
    }
}

