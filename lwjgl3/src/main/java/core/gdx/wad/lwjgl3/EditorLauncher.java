package core.gdx.wad.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import editor.gdx.launch.LevelEditor;
import editor.gdx.launch.WadChooser;
import net.mtrop.doom.WadFile;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class EditorLauncher {

    public static void main(String[] args) {

        try {
            createApplication();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static Lwjgl3Application createApplication() throws IOException {

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

        return new Lwjgl3Application(new LevelEditor(file), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Space Crucible Level Editor");
        configuration.setWindowedMode(640, 480);
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        return configuration;
    }
}