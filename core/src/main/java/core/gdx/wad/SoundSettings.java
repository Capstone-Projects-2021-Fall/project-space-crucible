package core.gdx.wad;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import core.wad.funcs.SoundFuncs;

public class SoundSettings extends Window {
    MyGDxTest myGDxTest;
    SettingsScreen settingsScreen;
    //public static float masterVolume = 1f;

    public SoundSettings(String title, Skin skin, SettingsScreen settingsScreen, Stage stage, MyGDxTest myGDxTest) {
        super(title, skin);
        setModal(false);
        this.myGDxTest=myGDxTest;
        this.settingsScreen=new SettingsScreen(myGDxTest);

        Slider masterVolumeSlider = new Slider(0, 100, 1, false, skin);
        masterVolumeSlider.setValue(50);
        add(masterVolumeSlider);
        Label volumeValue = new Label("50", skin);
        add(volumeValue);
        Button confirmButton = new TextButton("Confirm", skin);
        add(confirmButton);
        row();
        pack();


        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                System.out.println("Confirm Button\n");
                remove();
                //settingsScreen.remove = true;

            }
        });
        masterVolumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("Volume Changed to " +masterVolumeSlider.getValue() +"\n");
                volumeValue.setText((int) masterVolumeSlider.getValue());
                //Sound.setVolume("sound id", masterVolumeSlider.getValue()); TODO reference BGM (midi?)
                //Midi
            }
        });
    }
}
