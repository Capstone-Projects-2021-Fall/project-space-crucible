package core.gdx.wad;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import core.game.logic.GameLogic;


public class PausedGameWindow extends Window {
    MyGDxTest myGDxTest;
    public PausedGameWindow(String title, Skin skin, GameScreen gameScreen, MyGDxTest myGDxTest) {
        super(title, skin);
        setModal(true);
        setMovable(false);
        setResizable(false);
        //this.myGDxTest = myGDxTest;
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
                remove(); //this remove not working for some reason
                GameLogic.readyChangeLevel(GameLogic.currentLevel);
            }
        });
        titleScreenButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                try {
//                    myGDxTest.setScreen(myGDxTest.titleScreen); //null

//                    TitleScreen titleScreen = new TitleScreen(myGDxTest, myGDxTest.gameLoop); //also null
//                    myGDxTest.setScreen(titleScreen);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
