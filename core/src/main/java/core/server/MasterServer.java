package core.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import core.server.Network.JoinLobby;
import core.server.Network.Ping;
import core.server.Network.CreateLobby;
import core.server.Network.ServerDetails;
import core.server.Network.ValidLobby;
import core.server.Network.RCONLogin;
import core.server.Network.RCONMessage;
import core.server.Network.OpenLobby;
import core.server.Network.SendServerInfo;

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
    public HashMap<Integer, Integer> serversConnected = new HashMap<>();
    public ArrayList<Integer> availablePorts = new ArrayList<>();
    public HashSet<Integer> rconConnections = new HashSet<>();
    final private String CODE = "MASTER";

    public MasterServer(int minPort, int maxPort, String password){
        server = new Server();
        Network.register(server);
        for(int i = minPort; i <= maxPort; i++){
            availablePorts.add(i);
        }
        server.addListener(new Listener(){
            //When the client connects to the server add a player entity to the game
            public void connected(Connection c){
            }
            public void received (Connection connection, Object object) {
                //If the server sends RenderData object update the client's gamescreen
                if(object instanceof CreateLobby){
                    int tcpPort = 0;
                    for(int port : availablePorts ){
                        if(!ports.containsValue(port) && !isPortAvailable(port)) {
                            tcpPort = port;
                            break;
                        }
                    }
                    if(tcpPort == 0) {
                        System.out.println("All the lobbies are taken");
                        return;
                    }
                    String lobbyCode = createRandomLobbyCode();

                    String rconPass = createRandomLobbyCode();

                    ServerEntry entry = new ServerEntry(tcpPort,
                            ((CreateLobby) object).names, ((CreateLobby) object).hashes, rconPass, connection.getID());

                    servers.put(lobbyCode, entry);
                    ports.put(lobbyCode, tcpPort);
                    //send port info
                    ServerDetails serverDetails = new ServerDetails();
                    serverDetails.tcpPort = tcpPort;
                    serverDetails.lobbyCode = lobbyCode;
                    serverDetails.rconPass = rconPass;
                    connection.sendTCP(serverDetails);
                }
                if(object instanceof JoinLobby){
                    String lobbyCode = ((JoinLobby) object).lobbyCode;
                    System.out.println(lobbyCode);

                    //If user lobby code was correct find the tcpPort and send it to the user
                    if(servers.containsKey(lobbyCode)){

                        //Check if lobby code exists and is not 0
                        ServerEntry serverEntry = servers.get(lobbyCode);
                        int tcpPort = serverEntry.port;
                        ValidLobby validLobby = new ValidLobby();
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
                        //If the client does not have the level files lobby host added
                        if (serverEntry.getFiles().size() != ((JoinLobby) object).names.size()) {
                            //Ping  the host and tell the host to send the file
                            Ping pingLobbyHost = new Ping();
                            pingLobbyHost.getAddonFiles = true;
                            pingLobbyHost.masterClientThatNeedsTheFiles = connection.getID();
                            ServerEntry hostEntry = servers.get(lobbyCode);
                            server.sendToTCP(hostEntry.masterID, pingLobbyHost);

                            validLobby.valid = false;
                            validLobby.reason = "Lobby requires these WADS:\n" + serverEntry.getFiles().toString() + " \n Server is downloading them in your assets folder\n Wait a few seconds and try to join again!";
                            connection.sendTCP(validLobby);
                            System.out.println("No bueno. 2");
                            return;
                        }

                        for (int i = 0; i < serverEntry.getFiles().size(); i++) {

                            String serverFile = serverEntry.getFiles().get(i);
                            String serverHash = serverEntry.getHashes().get(i);
                            String clientFile = ((JoinLobby) object).names.get(i);
                            String clientHash = ((JoinLobby) object).hashes.get(i);

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
                        ServerDetails serverDetails = new ServerDetails();
                        serverDetails.tcpPort = tcpPort;
                        serverDetails.lobbyCode = lobbyCode;
                        connection.sendTCP(serverDetails);
                    } else {
                        ValidLobby validLobby = new ValidLobby();
                        validLobby.valid = false;
                        validLobby.reason = "No lobby with that key exists";
                        connection.sendTCP(validLobby);
                        System.out.println("No bueno. 5");
                    }
                }

                //RCON Login
                else if (object instanceof RCONLogin) {

                    String code = ((RCONLogin) object).code;
                    String pass = ((RCONLogin) object).pass;

                    RCONMessage m = new RCONMessage();

                    if (code.equals(CODE) && pass.equals(password)) {
                        rconConnections.add(connection.getID());
                        m.message = "OK";
                    } else {

                        for (String s : servers.keySet()) {
                            if (s.equals(code) && servers.get(s).rconPass.equals(pass)) {
                                m.message = String.valueOf(servers.get(s).port);
                                connection.sendTCP(m);
                                return;
                            }
                        }

                        m.message = "Bad login";
                    }

                    connection.sendTCP(m);
                }

                //RCON Command
                else if (object instanceof RCONMessage) {
                   handleRCON(((RCONMessage) object).message);
                }
                else if(object instanceof OpenLobby){
                    ports.entrySet().removeIf(entry -> ((OpenLobby) object).tcpPort == entry.getValue());
                }
                else if (object instanceof SendServerInfo) {
                    serversConnected.put(((SendServerInfo) object).tcpPort, connection.getID());
                }
                else if(object instanceof Network.CreateWadFile){
                    //Redirect the files to the player that needs it
                    System.out.println("master received create file");
                    server.sendToTCP(((Network.CreateWadFile) object).sendFileTo, object);
                }
                else if(object instanceof Network.WadFile){
                    //Redirect the files to the player that needs it
                    System.out.println("master received a chunk of file " + ((Network.WadFile) object).levelFileName);
                    server.sendToTCP(((Network.WadFile) object).sendFileTo, object);
                }
            }

            //This method will run when a client disconnects from the server, remove the character from the game
            public void disconnected(Connection c){
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
        private final int port;
        private final ArrayList<String> files;
        private final ArrayList<String> hashes;
        private final String rconPass;
        private final int masterID;

        public ServerEntry(int port, ArrayList<String> files, ArrayList<String> hashes, String rconPass, int masterID) {
            this.port = port;
            this.files = files;
            this.hashes = hashes;
            this.rconPass = rconPass;
            this.masterID = masterID;
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

    //Handle RCON commands
    private void handleRCON(String message) {

        String command = message.contains(" ") ? message.substring(0, message.indexOf(' ')) : message;

        switch(command) {

            case "ping":
                sendToRCON("ping");
                break;

            case "list":
                switch(message.substring(message.indexOf(' ') + 1)) {

                    case "codes":
                        for (String code : servers.keySet()) {
                            sendToRCON(code);
                        }
                        break;

                    case "freeports":
                        for (Integer port : availablePorts) {
                            if (!ports.containsValue(port)) {
                                sendToRCON(String.valueOf(port));
                            }
                        }
                        break;
                }
                break;

            case "info":
                String code = message.substring(message.indexOf(' ') + 1);

                if (servers.containsKey(code)) {
                    sendToRCON("Port: " + servers.get(code).port);
                    sendToRCON("Files: " + servers.get(code).files.toString());
                    sendToRCON("Hashes: " + servers.get(code).hashes.toString());
                    sendToRCON("RCON Pass: " + servers.get(code).rconPass);
                }
                break;
        }
    }

    private void sendToRCON(String send) {

        RCONMessage m = new RCONMessage();

        for (Integer i : rconConnections) {
            m.message = send;
            server.sendToTCP(i, m);
        }
    }

}

