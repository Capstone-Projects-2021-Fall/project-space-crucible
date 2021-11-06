package core.gdx.wad.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import core.rcon.RCON;

public class RCONLauncher {

    public static void main(String[] args) {
        createApplication();
        System.exit(0);
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new RCON(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Space Crucible RCON");
        configuration.setWindowedMode(640, 480);
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        return configuration;
    }
}