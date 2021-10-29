package core.gdx.wad;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import core.game.logic.GameLogic;

import java.util.Objects;

public class ChatWindow extends com.badlogic.gdx.scenes.scene2d.ui.Window {
    MyGDxTest myGDxTest;
    GameScreen gameScreen;
    int messageFieldCounter;
    ScrollPane scrollPane;//TODO implemnt scroll pane
    public ChatWindow(String title, Skin skin, GameScreen gameScreen, Stage stage, MyGDxTest myGDxTest) {
        super(title, skin);
        setModal(true);
        setMovable(false);
        this.myGDxTest = myGDxTest;
        this.gameScreen = gameScreen;
        messageFieldCounter=0;
        //scrollPane = new ScrollPane(this);
        //ArrayList<String> chatHistory = new ArrayList<>();
        //Table chatLogHolder = new Table(skin);
        TextField chatLog = new TextField("", skin);
        add(chatLog).height(chatLog.getHeight()*3);
        pack();
        row();

        TextArea chatField = new TextArea("", skin);
        add(chatField);

        chatField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(final TextField textField, final char key) {
                if (key == '\n' || key == '\r') {
                    if (messageFieldCounter <= 50) {
                        chatLog.appendText(Objects.requireNonNull(GameLogic.getPlayer(0)).getTag()
                                +": "+chatField.getText());
                        chatLog.appendText("\n");
                        chatLog.setHeight(chatLog.getHeight()+chatField.getHeight());
                        chatField.setText("");
                    }
                }
            }
        });

    }
}