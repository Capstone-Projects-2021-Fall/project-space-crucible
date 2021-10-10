package editor.gdx.launch;

import com.badlogic.gdx.Game;
import core.level.info.LevelData;
import editor.gdx.prompts.EditorFrame;
import editor.gdx.prompts.FilePrompt;
import net.mtrop.doom.WadFile;

import javax.swing.*;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class LevelEditor extends Game {
    public EditorScreen editorScreen;

    @Override
    public void create() {
        EditorFrame prompt = new EditorFrame(this);
        WadFile file = null;

        prompt.setContentPane(new FilePrompt(file, prompt, this));
        prompt.setSize(430, 360);
        prompt.setResizable(false);
        prompt.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        prompt.setVisible(true);

        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        editorScreen = new EditorScreen(this);
        setScreen(editorScreen);
    }
}