package core.gdx.wad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class ChatWindow extends com.badlogic.gdx.scenes.scene2d.ui.Window {
    MyGDxTest myGDxTest;
    GameScreen gameScreen;
    public ChatWindow(String title, Skin skin, GameScreen gameScreen, Stage stage, MyGDxTest myGDxTest) {
        super(title, skin);
        setModal(true);
        setMovable(false);
        this.myGDxTest = myGDxTest;
        this.gameScreen = gameScreen;
        //ArrayList<String> chatHistory = new ArrayList<>();
        TextField chatLog = new TextField("", skin);
        add(chatLog).height(chatLog.getHeight()*3/2);
        row();
        pack();
        TextArea chatField = new TextArea("", skin);
        add(chatField);

//        chatField.setTextFieldListener((textField, c) -> {
//
//        });

    }
}
