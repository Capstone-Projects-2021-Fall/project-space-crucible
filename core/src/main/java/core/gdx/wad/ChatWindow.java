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
        chatLog = new Table(skin);
        scrollPane = new ScrollPane(chatLog,skin);
        scrollPane.setColor(0,0,0,.7f);
        add(scrollPane).width(Value.percentWidth(.9f, chatWindow)).height(Value.percentHeight(.5f, chatWindow));
        scrollPane.setFadeScrollBars(false);
        row();
        TextField chatField = new TextField("", skin);
        add(chatField).width(Value.percentWidth(.7f, chatWindow)).height(Value.percentHeight(.14f, chatWindow));
        pack();


        chatField.setTextFieldListener((textField, key) -> {
            if (key == '\n' || key == '\r') {

                Network.ChatMessage chat = new Network.ChatMessage();
                chat.sender = TitleScreen.playerName;
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
        chatLog.add(new Label(log, getSkin())).width(Value.percentWidth(.85f, chatWindow));
        chatLog.row();
    }
}