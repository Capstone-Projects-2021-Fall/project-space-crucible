package core.gdx.wad;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class SettingsSlider extends Window {
    MyGDxTest myGDxTest;
    SettingsScreen settingsScreen;
    public SettingsSlider(String title, Skin skin, SettingsScreen settingsScreen, Stage stage, MyGDxTest myGDxTest) {
        super(title, skin);
        setModal(false);
        this.myGDxTest=myGDxTest;
        this.settingsScreen=new SettingsScreen(myGDxTest);

        Button confirmButton = new TextButton("Confirm", skin);
        add(confirmButton);
        Slider masterVolumeSlider = new Slider(0.00f, 10.00f, 1.00f, false, skin);
        add(masterVolumeSlider);
        row();
        pack();


        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                System.out.println("Back Button\n");
                //((MyGDxTest) Gdx.app.getApplicationListener()).setScreen(new TitleScreen(game,gameLoop));
                myGDxTest.setScreen(myGDxTest.titleScreen);
            }
        });
        masterVolumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("Volume Changed\n");
            }
        });
    }
}
