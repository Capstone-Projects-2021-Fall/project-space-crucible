package core.server;

import com.badlogic.gdx.Game;

import java.io.IOException;

public class MasterServerConfig extends Game{

    public MasterServerConfig() {
        super();
    }

    @Override
    public void create() {
        new MasterServer();
    }
}
