package core.server;

import com.badlogic.gdx.Game;

import java.io.IOException;

public class ServerGame extends Game{

    int playerCount;

    public ServerGame(int playerCount) {
        super();
        this.playerCount = playerCount;
        create();
    }

    @Override
    public void create() {
        try {
            new SpaceServer(playerCount);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
