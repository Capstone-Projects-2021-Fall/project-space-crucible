package editor.gdx.launch;

import com.badlogic.gdx.Game;

import javax.swing.*;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class LevelEditor extends Game {
    public EditorScreen editorScreen;

    @Override
    public void create() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        editorScreen = new EditorScreen(this);
        setScreen(editorScreen);
    }
}