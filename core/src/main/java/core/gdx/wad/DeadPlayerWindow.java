package core.gdx.wad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import core.game.logic.GameLogic;


public class DeadPlayerWindow extends Window {
    MyGDxTest myGDxTest;
    public DeadPlayerWindow(String title, Skin skin, MyGDxTest myGDxTest, Stage stage, GameScreen gameScreen) {
        super(title, skin);
        setMovable(false);
        setResizable(false);
        this.myGDxTest = myGDxTest;

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
                GameLogic.readyChangeLevel(GameLogic.currentLevel);
                remove(); //TODO this remove not working for some reason. workaround on game screen
            }
        });
        titleScreenButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                try {
                    //start menu gone
//                    myGDxTest.setScreen(myGDxTest.titleScreen);
                    //start menu there but crashes when you start game (IllegalThreadStateException)
                    TitleScreen titleScreen = new TitleScreen(myGDxTest, myGDxTest.gameLoop);
                    myGDxTest.setScreen(titleScreen);
                } catch (IllegalThreadStateException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
