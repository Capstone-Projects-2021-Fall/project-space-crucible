package core.gdx.wad;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import core.game.logic.GameLogic;
import core.server.Network;

import java.util.ArrayList;
import java.util.Objects;

public class ChatWindow extends com.badlogic.gdx.scenes.scene2d.ui.Window {
    MyGDxTest myGDxTest;
    GameScreen gameScreen;
    List<String> chatLog;
    ScrollPane scrollPane;
    Array<String> qA = new Array<>();
    public ChatWindow(String title, Skin skin, GameScreen gameScreen, Stage stage, MyGDxTest myGDxTest) {
        super(title, skin);
        setModal(true);
        setMovable(true);
        setResizable(false);
        this.myGDxTest = myGDxTest;
        this.gameScreen = gameScreen;
        //ArrayList<String> chatHistory = new ArrayList<>();
//        Table chatLogHolder = new Table(skin);
//        add(chatLogHolder);
//        row();

        //TextField chatLog = new TextField("", skin);
        //Label chatLogBody = new Label("",skin);
        chatLog = new List<>(skin);
        scrollPane = new ScrollPane(chatLog, skin);
        //add(chatLog).height(chatLog.getHeight()*3/2);
        add(scrollPane).height(100).width(200);
        scrollPane.setFadeScrollBars(false);

        //chatLog.setDisabled(true);
        row();
        TextField chatField = new TextField("", skin);
        add(chatField);
        //chatLog.setHeight(2*chatField.getHeight());
        pack();


        chatField.setTextFieldListener((textField, key) -> {
            if (key == '\n' || key == '\r') {

                Network.ChatMessage chat = new Network.ChatMessage();
                chat.sender = "Player " + gameScreen.playerNumber;
                chat.message = chatField.getText();
                gameScreen.client.getGameClient().sendTCP(chat);
                chatField.setText("");
                chatField.setDisabled(true);
                scrollPane.setScrollPercentY(100f);

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

        chatField.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                chatField.setDisabled(false);
            }
        });
    }

    public void addToChatLog(String log) {
        qA.add(log);
        chatLog.setItems(qA);
    }
}