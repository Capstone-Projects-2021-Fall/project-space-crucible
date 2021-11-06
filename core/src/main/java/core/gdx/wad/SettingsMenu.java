package core.gdx.wad;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class SettingsMenu extends Window {
    MyGDxTest myGDxTest;
    SettingsScreen settingsScreen;
    public SettingsMenu(String title, Skin skin, SettingsScreen settingsScreen, Stage stage, MyGDxTest myGDxTest) {
        super(title, skin);
        setModal(false);
        this.myGDxTest=myGDxTest;
        this.settingsScreen=new SettingsScreen(myGDxTest);

        Button addonsButton = new TextButton("Add-ons", skin);
        add(addonsButton);
        row();
        Button sfxButton = new TextButton("Volume", skin);
        add(sfxButton);
        row();
        Button backButton = new TextButton("Back", skin);
        add(backButton);
        row();
        pack();

        addonsButton.addListener(new ClickListener() {

                 @Override
                 public void clicked(InputEvent event, float x, float y) {
                     super.clicked(event, x, y);
                     stage.addActor(new AddonWindow("Add .WADs", skin));
                 }
             }
        );

        sfxButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Actor volumeActor = new SoundSettings("Volume", skin, settingsScreen, stage, myGDxTest);
                stage.addActor(volumeActor);
            }
        });
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                //((MyGDxTest) Gdx.app.getApplicationListener()).setScreen(new TitleScreen(game,gameLoop));
                myGDxTest.setScreen(myGDxTest.titleScreen);
            }
        });
    }
}
