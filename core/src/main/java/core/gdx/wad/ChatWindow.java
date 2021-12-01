package core.gdx.wad;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import core.server.Network;

public class ChatWindow extends com.badlogic.gdx.scenes.scene2d.ui.Window {
    MyGDxTest myGDxTest;
    GameScreen gameScreen;
    Table chatLog;
    ScrollPane scrollPane;
    TextField chatField;
    public ChatWindow(String title, Skin skin, GameScreen gameScreen, Stage stage, MyGDxTest myGDxTest) {
        super(title, skin);
        setModal(true);
        setMovable(true);
        setResizable(false);
        this.myGDxTest = myGDxTest;
        this.gameScreen = gameScreen;

        chatLog = new Table(skin);
        add(chatLog).fill();//.grow().expand().uniform();
        row();
        scrollPane = new ScrollPane(chatLog, skin);
        add(scrollPane).height(100).width(320);
        scrollPane.setFadeScrollBars(false);
        row();
        chatField = new TextField("", skin);
        add(chatField);
        pack();


        chatField.setTextFieldListener((textField, key) -> {
            if (key == '\n' || key == '\r') {

                Network.ChatMessage chat = new Network.ChatMessage();
                chat.sender = NameChangeWindow.playerName;
                chat.message = chatField.getText();
                gameScreen.client.getGameClient().sendTCP(chat);
                chatField.setText("");
                chatField.setDisabled(true);
                scrollPane.setScrollPercentY(100f);
            }
        });

        chatField.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                chatField.setDisabled(false);
            }
        });
    }

    public void addToChatLog(String log) {
        chatLog.add(new Label(log, getSkin())).width(320);
        chatLog.row();
    }
}

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