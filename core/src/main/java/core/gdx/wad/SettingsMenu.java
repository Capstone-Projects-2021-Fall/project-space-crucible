package core.gdx.wad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class SettingsMenu extends Window {
    MyGDxTest myGDxTest;
    SettingsScreen settingsScreen;
    public SettingsMenu(String title, Skin skin, SettingsScreen settingsScreen, Stage stage, MyGDxTest myGDxTest) {
        super(title, skin);
        setModal(false);
        this.myGDxTest=myGDxTest;
        this.settingsScreen=new SettingsScreen(myGDxTest);

        Button masterVolumeButton = new TextButton("Volume", skin);
        add(masterVolumeButton);
        row();
        Button backButton = new TextButton("Back", skin);
        add(backButton);
        row();
        pack();



        masterVolumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                System.out.println("Master Volume\n");
                Actor volumeActor = new SettingsSlider("Adjust Volume", skin, settingsScreen, stage, myGDxTest);
                stage.addActor(volumeActor);
            }
        });
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                System.out.println("Back Button\n");
                //((MyGDxTest) Gdx.app.getApplicationListener()).setScreen(new TitleScreen(game,gameLoop));
                myGDxTest.setScreen(myGDxTest.titleScreen);
            }
        });
//        if(Gdx.input.isTouched()){
//            ((MyGDxTest) Gdx.app.getApplicationListener()).setScreen(new TitleScreen(game,gameLoop));
//        }
    }
}
