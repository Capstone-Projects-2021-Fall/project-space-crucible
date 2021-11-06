package editor.scene2d.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class EditorHUDActor extends Table {
    private TextField name;
    private MIDINameField music;

    public EditorHUDActor(TextField name, MIDINameField music) {
        this.name = name;
        add(this.name);
        this.music = music;
        add(this.music);
        pack();
    }

    public TextField getLevelName() {
        return name;
    }

    public MIDINameField getMusic() {
        return music;
    }
}
