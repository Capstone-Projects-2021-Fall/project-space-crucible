package core.gdx.wad.lwjgl3;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import core.server.MasterServer;
import core.server.MasterServerConfig;
import core.server.ServerGame;

public class MasterServerLauncher {
    /** Launches the headless server application. */
    public static void main(String[] args) {
        createApplication(args);
    }

    private static Application createApplication(String [] args) {
        return new HeadlessApplication(new MasterServerConfig(), getDefaultConfiguration());
    }

    private static HeadlessApplicationConfiguration getDefaultConfiguration() {
        return new HeadlessApplicationConfiguration();
    }
}

