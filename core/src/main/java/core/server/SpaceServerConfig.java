package core.server;

import com.badlogic.gdx.Game;

import java.io.IOException;

public class SpaceServerConfig extends Game{

    int tcpPort;

    public SpaceServerConfig(int tcpPort) {
        super();
        this.tcpPort = tcpPort;
    }

    @Override
    public void create() {

        try {
            new SpaceServer(tcpPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
