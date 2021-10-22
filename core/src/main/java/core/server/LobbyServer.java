package core.server;

import com.esotericsoftware.kryonet.Connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class LobbyServer {

    private final Map<String, Lobby> lobbies = new ConcurrentHashMap<>();

    public LobbyServer(){
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

    //This returns random free available port on the server
    public static int getAvailablePort() throws IOException {
        var freePort = new ServerSocket(0);
        freePort.close();
        return freePort.getLocalPort();
    }

    public Lobby createLobby(String hostUsername){
        final String newLobbyCode = createRandomLobbyCode();
        final Lobby lobby = new Lobby(newLobbyCode, 4);
        lobbies.put(lobby.getLobbyCode(), lobby);
        //add the host player to the lobby
        lobby.addPlayerToLobby(hostUsername);
        return lobby;
    }
    public void joinLobby(String lobbyCode){
        final Lobby lobby = lobbies.get(lobbyCode);
        if(lobby == null){
            //tell client lobby does not exist
        }else{
            //Check if lobby is not full then
                //add the player to the lobby
//                lobby.addPlayerToLobby();
        }
    }
}
