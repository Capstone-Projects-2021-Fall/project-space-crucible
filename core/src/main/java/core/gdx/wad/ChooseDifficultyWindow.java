package core.gdx.wad;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import core.game.logic.GameLogic;

public class ChooseDifficultyWindow extends Window {

    private SelectBox<String> levelList;
    private TextButton button;
    private TextButton back;

    public ChooseDifficultyWindow(String title, Skin skin, TitleScreen titleScreen, Button buttons[], boolean isSingleplayer) {
        super(title, skin);
        setModal(true);
        levelList = new SelectBox<>(skin);
        levelList.setItems("Very easy", "Easy", "Medium", "Hard", "Nightmare!");
        add(levelList);
        row();
        button = new TextButton("Go!", skin);
        back = new TextButton("Back", skin);
        add(button);
        row();
        add(back);
        pack();

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(isSingleplayer) {
                    GameLogic.difficulty = levelList.getSelectedIndex();
                    remove();
                    titleScreen.myGDxTest.setScreen(new GameScreen(titleScreen.myGDxTest.gameLoop, true, titleScreen.myGDxTest));
                }else{
                    GameScreen.difficultyLevelSelected = levelList.getSelectedIndex();
                    System.out.println("Game difficulty level: " + GameScreen.difficultyLevelSelected);
                    remove();

                }
            }
        });
        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                remove();
                if(isSingleplayer) {
                    for (Button button : buttons)
                        button.setVisible(true);
                }
            }
        });
    }
}
