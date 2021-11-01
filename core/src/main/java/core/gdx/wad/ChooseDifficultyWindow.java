package core.gdx.wad;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import core.game.logic.GameLogic;

public class ChooseDifficultyWindow extends Window {

    private SelectBox<String> levelList;
    private TextButton button;
    private TextButton back;

    public ChooseDifficultyWindow(String title, Skin skin, TitleScreen screen, StartMenu startMenu, Button buttons[]) {
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
                GameLogic.difficulty = levelList.getSelectedIndex();
                remove();
                startMenu.remove();
                screen.game.setScreen(new GameScreen(screen.gameLoop, true));
                screen.dispose();
            }
        });
        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                remove();
                for(Button button : buttons)
                    button.setVisible(true);

            }
        });
    }
}
