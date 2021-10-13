package core.gdx.wad.lwjgl3;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import core.gdx.wad.MyGDxTest;

/** Launches the desktop (LWJGL) application. */
public class LegacyLauncher {
	public static void main(String[] args) {
		createApplication();
	}

	private static LwjglApplication createApplication() {
		return new LwjglApplication(new MyGDxTest(), getDefaultConfiguration());
	}

	private static LwjglApplicationConfiguration getDefaultConfiguration() {
		LwjglApplicationConfiguration configuration = new LwjglApplicationConfiguration();
		configuration.title = "Space Crucible";
		configuration.width = 640;
		configuration.height = 480;
		return configuration;
	}
}