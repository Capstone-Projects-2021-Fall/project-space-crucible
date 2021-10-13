package core.gdx.wad.lwjgl3;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import editor.gdx.launch.LevelEditor;
import editor.gdx.launch.WadChooser;
import net.mtrop.doom.WadFile;

import javax.swing.*;
import java.io.IOException;

/** Launches the desktop (LWJGL) application. */
public class LegacyEditorLauncher {
	public static void main(String[] args) throws IOException {
		createApplication();
	}

	private static LwjglApplication createApplication() throws IOException {

		JDialog dialog = new JDialog();
		JFileChooser wc = new WadChooser();
		dialog.setContentPane(wc);
		dialog.setSize(640, 480);
		dialog.setResizable(false);
		if (wc.showOpenDialog(dialog) != JFileChooser.APPROVE_OPTION) {
			System.exit(0);
		}
		dialog.dispose();

		WadFile file = new WadFile(wc.getSelectedFile());

		return new LwjglApplication(new LevelEditor(file), getDefaultConfiguration());
	}

	private static LwjglApplicationConfiguration getDefaultConfiguration() {
		LwjglApplicationConfiguration configuration = new LwjglApplicationConfiguration();
		configuration.title = "Space Crucible";
		configuration.width = 640;
		configuration.height = 480;
		return configuration;
	}
}