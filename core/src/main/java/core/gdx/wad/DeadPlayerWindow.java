package core.gdx.wad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import core.game.logic.GameLogic;



public class DeadPlayerWindow extends Window {
    MyGDxTest myGDxTest;
    public DeadPlayerWindow(String title, Skin skin, MyGDxTest myGDxTest) {
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
                remove(); //this remove not working for some reason
                GameLogic.readyChangeLevel(GameLogic.currentLevel);
            }
        });
        titleScreenButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                try {
                    myGDxTest.setScreen(myGDxTest.titleScreen); //null for some reason
//                    TitleScreen titleScreen = new TitleScreen(myGDxTest, myGDxTest.gameLoop); //also null
//                    myGDxTest.setScreen(titleScreen);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
