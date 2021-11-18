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
                remove();
            }
        });
        titleScreenButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) throws IllegalThreadStateException {
                super.clicked(event, x, y);
//                myGDxTest.setScreen(myGDxTest.titleScreen);
//                StartMenu.setMainMenuButtonsVisible(true);
                myGDxTest.gameLoop.interrupt();
                Thread gameLoop = new Thread() {
                    @Override
                    public void run() {
                        GameLogic.start();
                    }

                    @Override
                    public void interrupt() {
                        GameLogic.stop();
                    }
                };
                    TitleScreen titleScreen = new TitleScreen(myGDxTest, gameLoop);
                    myGDxTest.setScreen(titleScreen);
                    TitleScreen.setMainMenuButtonsVisible(true);

            }
        });
    }
}
