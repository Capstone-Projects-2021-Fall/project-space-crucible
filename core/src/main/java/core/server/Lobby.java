package core.server;

import com.esotericsoftware.kryonet.Connection;
import core.game.entities.PlayerPawn;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Lobby {
    private final Map<Integer, Connection> players = new ConcurrentHashMap<>();
    private final String lobbyCode;
    public int lobbySize = 4;

    public Lobby(String lobbyCode, int lobbySize){
        this.lobbyCode = lobbyCode;
        this.lobbySize = lobbySize;
    }

    public static Lobby createLobby(String lobbyCode, int lobbySize){
        return new Lobby(lobbyCode, lobbySize);
    }

    public String getLobbyCode() {
        return lobbyCode;
    }

    public void addPlayerToLobby(Connection c){
        this.players.put(c.getID(), c);
    }

    public void removePlayerFromLobby(Connection c){
        this.players.remove(c.getID());
    }

    public int getLobbySize() {
        return lobbySize;
    }

    public void setLobbySize(int lobbySize) {
        this.lobbySize = lobbySize;
    }

    public boolean isLobbyFull(){
        if(this.players.size() == this.getLobbySize()){
            return true;
        }
        return false;
    }
}
