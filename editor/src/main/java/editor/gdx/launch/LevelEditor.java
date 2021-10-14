package editor.gdx.launch;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class LevelEditor extends Game {
    public EditorScreen editorScreen;

    public LevelEditor() {
        super();
    }

    @Override
    public void create() {
        editorScreen = new EditorScreen(this);
        setScreen(editorScreen);
    }
}