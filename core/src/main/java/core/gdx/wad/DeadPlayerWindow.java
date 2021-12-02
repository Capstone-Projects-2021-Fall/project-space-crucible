package core.gdx.wad;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import core.game.logic.GameLogic;
import core.server.Network;

import java.io.IOException;

public class DeadPlayerWindow extends Window {
    MyGDxTest myGDxTest;
    public DeadPlayerWindow(String title, Skin skin, MyGDxTest myGDxTest, Stage stage, GameScreen gameScreen) {
        super(title, skin);
        setMovable(false);
        setResizable(false);
        this.myGDxTest = myGDxTest;
        Button restartButton = new TextButton("Restart Level", skin);
        if(gameScreen.playerNumber == 1) {
            add(restartButton);
            row();
        }
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
                if(gameScreen.isSinglePlayer) {
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
                    myGDxTest.gameLoop = gameLoop;
                }
                else {
                    try {
                        gameScreen.client.getGameClient().update(0);
                        Network.Ping ping = new Network.Ping();
                        ping.disconnect = true;
                        gameScreen.client.getGameClient().sendTCP(ping);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                myGDxTest.setScreen(myGDxTest.titleScreen);
                    TitleScreen.mainMenuTable.setVisible(true);
            }
        });
    }
}
