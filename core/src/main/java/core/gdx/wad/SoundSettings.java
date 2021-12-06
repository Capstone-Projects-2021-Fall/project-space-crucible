package core.gdx.wad;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import core.config.Config;
import core.wad.funcs.SoundFuncs;

public class SoundSettings extends Window {
    MyGDxTest myGDxTest;
    Config config = new Config();
    public SoundSettings(String title, Skin skin, SettingsMenu settingsMenu) {
        super(title, skin);
        setModal(false);
        setColor(0,0,0,1f);
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
        CheckBox bgmCheckBox = new CheckBox(null, skin);
        bgmCheckBox.setChecked(true);
        add(bgmCheckBox);

//        Slider bgmSlider = new Slider(0, 100, 1, false, skin);
//        bgmSlider.setValue(50);
//        add(bgmSlider);
//        Label bgmVolumeValue = new Label("50", skin);
//        add(bgmVolumeValue);
        row();
        Button confirmButton = new TextButton("Confirm", skin);
        add(confirmButton);
        row();
        pack();

        sfxVolumeValue.setText((int) TitleScreen.sfx);
        soundEffectSlider.setValue(TitleScreen.sfx);
        bgmCheckBox.setChecked(TitleScreen.bgm);
//        bgmSlider.setValue(TitleScreen.bgm);
//        bgmVolumeValue.setText((int) TitleScreen.bgm);
        SoundFuncs.volume=TitleScreen.sfx/100f;

        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                config.saveText("sfx", String.valueOf(soundEffectSlider.getValue()));
                config.saveText("bgm", String.valueOf(bgmCheckBox.isChecked()));
                remove();
                settingsMenu.setVisible(true);

                //settingsScreen.remove = true;
            }
        });
        soundEffectSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sfxVolumeValue.setText((int) soundEffectSlider.getValue());
                handleSFX(soundEffectSlider.getValue());
            }
        });
//        bgmSlider.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {  //TODO: find why volume messages aren't working (controller 7)
//                bgmVolumeValue.setText((int) bgmSlider.getValue());
////                SoundFuncs.seqVolume=bgmSlider.getValue()/100d; //supposed to change BGM volume
////                System.out.println("seqVolume: " +SoundFuncs.seqVolume);
//                handleBGM(bgmSlider.getValue());
//
//            }
//        });
        bgmCheckBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(bgmCheckBox.isChecked()){
                    handleBGM(true);
                }else{
                    handleBGM(false);
                }
                //settingsScreen.remove = true;
            }
        });
    }

    public static void handleSFX(float volume){
        SoundFuncs.volume=volume/100f;
    }
    public static void handleBGM(boolean volume){
        if(!volume){
            for(int i=0; i<1000; i++){
                SoundFuncs.sequencer.setTrackMute(i,true);//mutes BGM, but you can still hear drums
            }
        }
        if(volume){
            for(int i=0; i<=1000; i++){
                SoundFuncs.sequencer.setTrackMute(i,false);//unmutes BGM
            }
        }
    }

}
