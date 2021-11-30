package core.gdx.wad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import core.server.Network;

public class ChatWindow extends com.badlogic.gdx.scenes.scene2d.ui.Window {
    GameScreen gameScreen;
    Table chatLog;
    ScrollPane scrollPane;
    Window chatWindow = this;
    public ChatWindow(String title, Skin skin, GameScreen gameScreen, Stage stage) {
        super(title, skin);
        setModal(true);
        setMovable(true);
        setResizable(false);
        setColor(0,0,0,0.5f);
        this.gameScreen = gameScreen;

        chatLog = new Table();
        scrollPane = new ScrollPane(chatLog);
        add(scrollPane).height(100).width(320);
        scrollPane.setFadeScrollBars(false);
        row();
        TextField chatField = new TextField("", skin);
        chatField.setColor(1,1,1,1);
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

        chatWindow.addCaptureListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if (!(event.getTarget() instanceof TextField)) stage.setKeyboardFocus(null);
                return false;
            }
        });
    }

    public void addToChatLog(String log) {
        chatLog.add(new Label(log, getSkin())).width(320);
        chatLog.row();
    }

    public void resize() {
        chatLog.setPosition(((Gdx.graphics.getWidth() - chatLog.getWidth())/ 2f), ((Gdx.graphics.getHeight() - chatLog.getHeight()) / 2f));
        scrollPane.setPosition(((Gdx.graphics.getWidth() - scrollPane.getWidth())/ 2f), ((Gdx.graphics.getHeight() - scrollPane.getHeight()) / 2f));
    }
}