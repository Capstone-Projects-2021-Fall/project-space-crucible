package core.gdx.wad;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import core.game.logic.GameLogic;
import core.server.Network;

public class NameChangeWindow extends Window {
    MyGDxTest myGDxTest;
    public int playerNumber = 1;
    public static String playerName="Player";

    public NameChangeWindow(String title, Skin skin, SettingsMenu settingsMenu) {
        super(title, skin);
        setModal(false);
        this.myGDxTest=myGDxTest;

        TextField enterNewName = new TextField("Enter new name",skin);
        add(enterNewName);
        row();
        TextField newName = new TextField("Current Name",skin);
        add(newName);
        row();
        Button confirmNameButton = new TextButton("Confirm", skin);
        add(confirmNameButton);
        pack();

        newName.setDisabled(true);
        newName.setText(playerName);


        enterNewName.setTextFieldListener((textField, key) -> {
            if (key == '\n' || key == '\r') {
                playerName = enterNewName.getText();
                newName.setText(playerName);
            }
        });

        confirmNameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                playerName = enterNewName.getText();
                newName.setText(playerName);
                remove();
                settingsMenu.setVisible(true);
                //settingsScreen.remove = true;
            }
        });
        enterNewName.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                enterNewName.setText("");
            }
        });
    }
}
