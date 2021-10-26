package core.gdx.wad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class SettingsMenu extends Window {

    public SettingsMenu(String title, Skin skin, SettingsScreen settingsScreen, Stage stage, MyGDxTest myGDxTest) {
        super(title, skin);
        setModal(false);

        //TODO replace this with a slider
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
