package core.gdx.wad;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import core.game.logic.GameLogic;
import core.server.Network;

public class NameChangeWindow extends Window {
    MyGDxTest myGDxTest;
    public NameChangeWindow(String title, Skin skin, MyGDxTest myGDxTest) {
        super(title, skin);
        TextField newName = new TextField("",skin);
        add(newName);
        Button confirmNameButton = new Button();
        add(confirmNameButton);
        pack();

        newName.setTextFieldListener((textField, key) -> {
            if (key == '\n' || key == '\r') {
                newName.setText("");
                newName.setDisabled(true);
                /*
                StringBuilder sb = new StringBuilder();
                sb.append("Player ")
                        .append(gameScreen.playerNumber)
                        .append(": ").append(chatField.getText());
                qA.add(sb.toString());
                chatLog.setItems(qA);
//                    chatLog.appendText("Player " +(Objects.requireNonNull(GameLogic.getPlayer(0)).getTag())
//                            + ": "+ sb + "\n");
                chatField.setText("");
                chatField.setDisabled(true);
                scrollPane.setScrollPercentY(100f);
                */
            }
        });
    }
}
