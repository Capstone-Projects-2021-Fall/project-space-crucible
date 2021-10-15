package core.gdx.wad.lwjgl3;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import editor.gdx.launch.LevelEditor;

/** Launches the desktop (LWJGL) application. */
public class LegacyEditorLauncher {
	public static void main(String[] args) {
		createApplication();
	}

	private static LwjglApplication createApplication()  {
		return new LwjglApplication(new LevelEditor(), getDefaultConfiguration());
	}

	private static LwjglApplicationConfiguration getDefaultConfiguration() {
		LwjglApplicationConfiguration configuration = new LwjglApplicationConfiguration();
		configuration.title = "Space Crucible";
		configuration.width = 640;
		configuration.height = 480;
		return configuration;
	}
}