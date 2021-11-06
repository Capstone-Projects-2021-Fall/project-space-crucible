package editor.scene2d.actors;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class EditorHUDActor extends Table {
    private TextField name;
    private MIDINameField music;
    private CheckBox singleLayer;
    private NumberField layer;

    public EditorHUDActor(TextField name, MIDINameField music, CheckBox singleLayer, NumberField layer) {
        this.name = name;
        add(this.name);
        this.music = music;
        add(this.music);
        this.singleLayer = singleLayer;
        this.singleLayer.setChecked(false);

        this.singleLayer.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                toggleLayer();
            }
        });

        add(this.singleLayer);
        this.layer = layer;
        this.layer.setDisabled(true);
        add(this.layer);
        pack();
    }

    public TextField getLevelName() {
        return name;
    }

    public MIDINameField getMusic() {
        return music;
    }

    public CheckBox getSingleLayer() {
        return singleLayer;
    }

    public NumberField getLayer() {
        return layer;
    }

    private void toggleLayer() {
        layer.setDisabled(!singleLayer.isChecked());
    }
}
