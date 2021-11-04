package core.gdx.wad;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class PausedGameWindow extends Window {
    MyGDxTest myGDxTest;
    public PausedGameWindow(String title, Skin skin, GameScreen gameScreen, MyGDxTest myGDxTest) {
        super(title, skin);
        setModal(false);
        setMovable(true);
        setResizable(false);
        this.myGDxTest = myGDxTest;
        //this.settingsScreen=new SettingsScreen(myGDxTest); //makes the game super slow

        Button restartButton = new TextButton("Restart Level", skin);
        add(restartButton);
        row();
        Button titleScreenButton = new TextButton("Return to Title screen", skin);
        add(titleScreenButton);
        row();
        pack();

        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                //myGDxTest.gameLoop.start();
            }
        });

        titleScreenButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                myGDxTest.setScreen(myGDxTest.titleScreen);
            }
        });
    }
}
