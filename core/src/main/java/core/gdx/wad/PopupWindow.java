package core.gdx.wad;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class PopupWindow extends Window {
    public PopupWindow(String title, Skin skin, String message) {
        super(title, skin);

        setModal(true);

        add(new Label(message, skin));
        row();

        TextButton button = new TextButton("OK", skin);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                close();
            }
        });

        add(button);
        pack();
    }

    private void close() {remove();}
}
