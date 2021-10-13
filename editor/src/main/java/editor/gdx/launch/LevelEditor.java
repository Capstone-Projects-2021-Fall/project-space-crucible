package editor.gdx.launch;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import net.mtrop.doom.WadFile;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.*;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class LevelEditor extends Game {
    public EditorScreen editorScreen;
    private WadFile file;

    public LevelEditor(WadFile file) {
        super();
        this.file = file;
    }

    @Override
    public void create() {
        editorScreen = new EditorScreen(this, file);
        setScreen(editorScreen);
    }
}