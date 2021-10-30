package core.gdx.wad;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import core.game.logic.GameLogic;

public class ChooseDifficultyWindow extends Window {

    private SelectBox<String> levelList;
    private TextButton button;

    public ChooseDifficultyWindow(String title, Skin skin, TitleScreen screen) {
        super(title, skin);
        setModal(true);
        levelList = new SelectBox<>(skin);
        levelList.setItems("Very easy", "Easy", "Medium", "Hard", "Nightmare!");
        add(levelList);
        row();
        button = new TextButton("Go!", skin);
        add(button);
        pack();

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                GameLogic.difficulty = levelList.getSelectedIndex();
                remove();
                screen.remove = true;
            }
        });
    }
}
