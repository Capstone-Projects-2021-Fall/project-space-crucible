package core.server;

import core.game.entities.PlayerPawn;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Lobby {
    private final Map<Integer, PlayerPawn> players = new ConcurrentHashMap<>();
    private final String lobbyCode;
    public int lobbySize = 4;

    public Lobby(String lobbyCode){
        this.lobbyCode = lobbyCode;
//        this.lobbySize = lobbySize;
    }

    public static Lobby createLobby(String lobbyCode){
        return new Lobby(lobbyCode);
    }

    public String getLobbyCode() {
        return lobbyCode;
    }

    public void addPlayerToLobby(){

    }

    public void removePlayerFromLobby(){

    }

    public int getLobbySize() {
        return lobbySize;
    }

    public void setLobbySize(int lobbySize) {
        this.lobbySize = lobbySize;
    }


}
