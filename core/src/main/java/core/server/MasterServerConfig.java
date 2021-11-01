package core.server;

import com.badlogic.gdx.Game;

public class MasterServerConfig extends Game{
    int minPort;
    int maxPort;
    String password;
    public MasterServerConfig(int minPort, int maxPort, String password) {
        super();
        this.minPort = minPort;
        this.maxPort = maxPort;
        this.password = password;
    }

    @Override
    public void create() {
        new MasterServer(minPort, maxPort, password);
    }
}
