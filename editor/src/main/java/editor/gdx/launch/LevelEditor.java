package editor.gdx.launch;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import net.mtrop.doom.WadFile;

import javax.swing.*;
import java.io.IOException;
import java.util.*;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class LevelEditor extends Game {
    public EditorScreen editorScreen;

    @Override
    public void create() {
        try {
            editorScreen = new EditorScreen(this, new WadFile(Gdx.files.internal("assets/resource.wad").file()),
                    1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setScreen(editorScreen);
    }
}