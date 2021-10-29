package core.gdx.wad.lwjgl3;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import core.server.MasterServer;
import core.server.MasterServerConfig;

public class MasterServerLauncher {
    /** Launches the headless server application. */
    public static void main(String[] args) {
        try{
            Integer.parseInt(args[0]);
            Integer.parseInt(args[1]);
        }catch (Exception e){
            System.out.println("Syntax: java -jar ServerLauncher.jar [tcpPort]");
            System.exit(0);
        }
        createApplication(args);
    }

    private static Application createApplication(String [] args) {
        return new HeadlessApplication(new MasterServerConfig(Integer.parseInt(args[0]), Integer.parseInt(args[1])), getDefaultConfiguration());
    }

    private static HeadlessApplicationConfiguration getDefaultConfiguration() {
        return new HeadlessApplicationConfiguration();
    }
}

