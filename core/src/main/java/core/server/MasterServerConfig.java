package core.server;

import com.badlogic.gdx.Game;

import java.io.IOException;

public class MasterServerConfig extends Game{
    int minPort;
    int maxPort;
    public MasterServerConfig(int minPort, int maxPort) {
        super();
        this.minPort = minPort;
        this.maxPort = maxPort;
    }

    @Override
    public void create() {
        new MasterServer(minPort, maxPort);
    }
}
