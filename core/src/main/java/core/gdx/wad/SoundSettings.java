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

    public SoundSettings(String title, Skin skin, SettingsScreen settingsScreen, Stage stage, MyGDxTest myGDxTest) {
        super(title, skin);
        setModal(false);
        this.myGDxTest=myGDxTest;

        Label sfxLabel = new Label("SFX: ", skin);
        add(sfxLabel);
        Slider soundEffectSlider = new Slider(0, 100, 1, false, skin);
        soundEffectSlider.setValue(50);
        add(soundEffectSlider);
        Label sfxVolumeValue = new Label("50", skin);
        add(sfxVolumeValue);
        row();
        Label bgmLabel = new Label("BGM: ", skin);
        add(bgmLabel);
        Slider bgmSlider = new Slider(0, 100, 1, false, skin);
        bgmSlider.setValue(90);
        add(bgmSlider);
        Label bgmVolumeValue = new Label("90", skin);
        add(bgmVolumeValue);
        row();
        Button confirmButton = new TextButton("Confirm", skin);
        add(confirmButton);
        row();
        pack();


        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                remove();
                //settingsScreen.remove = true;
            }
        });
        soundEffectSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sfxVolumeValue.setText((int) soundEffectSlider.getValue());
                SoundFuncs.volume=soundEffectSlider.getValue()/100f;
            }
        });
        bgmSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {  //TODO: find why volume messages aren't working (controller 7)
                bgmVolumeValue.setText((int) bgmSlider.getValue());
                SoundFuncs.seqVolume=bgmSlider.getValue()/100d; //supposed to change BGM volume
                System.out.println("seqVolume: " +SoundFuncs.seqVolume);

                //mutes bgm
//                if(bgmSlider.getValue()<49){
//                    for(int i=0; i<1000; i++){
//                        SoundFuncs.sequencer.setTrackMute(i,true);//mutes BGM, but you can still hear drums
//                    }
//                }
//                if(bgmSlider.getValue()>=50){
//                    for(int i=0; i<=1000; i++){
//                        SoundFuncs.sequencer.setTrackMute(i,false);//unmutes BGM
//                    }
//                }
            }
        });
    }
}
