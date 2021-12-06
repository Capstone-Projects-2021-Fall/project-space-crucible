package core.gdx.wad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import core.config.Config;

public class ResolutionWindow extends Window {
    Config config = new Config();


    public ResolutionWindow(String title, Skin skin, SettingsMenu settingsMenu) {
        super(title, skin);
        setModal(true);
        setMovable(false);
        setResizable(false);
        setColor(0f,0f,0f,.8f);
        Label fullscreenLabel = new Label("FullScreen:  ", skin);
        add(fullscreenLabel);
        CheckBox fullscreenBox = new CheckBox(null, skin);
        fullscreenBox.setChecked(true);
        add(fullscreenBox);
        row();
        Button confirmButton = new TextButton("Confirm", skin);
        add(confirmButton);
        row();

        fullscreenBox.setChecked(TitleScreen.fullscreen);


        fullscreenBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(fullscreenBox.isChecked()){
                    handleFullScreen(true);
                }else{
                    handleFullScreen(false);
                }
                //settingsScreen.remove = true;
            }
        });
        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                config.saveText("fullscreen", String.valueOf(fullscreenBox.isChecked()));
                remove();
                settingsMenu.setVisible(true);

                //settingsScreen.remove = true;
            }
        });

    }
    public static void handleFullScreen(boolean check){
        if(check){
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }else{
            Gdx.graphics.setWindowedMode(640,480);

        }
    }
}
