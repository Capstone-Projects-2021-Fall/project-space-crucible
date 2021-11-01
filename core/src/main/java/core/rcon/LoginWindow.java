package core.rcon;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.esotericsoftware.kryonet.Client;
import core.server.Network;

public class LoginWindow extends Window {

    private Label codeLabel;
    private TextField codeField;
    private Label passLabel;
    private TextField passField;
    private TextButton loginButton;
    private Client client;

    public LoginWindow(String title, Skin skin, Client client) {
        super(title, skin);

        setModal(true);
        this.client = client;

        codeLabel = new Label("Code: ", skin);
        codeField = new TextField("", skin);
        passLabel = new Label("Password: ", skin);
        passField = new TextField("", skin);
        passField.setPasswordMode(true);
        passField.setPasswordCharacter('*');
        loginButton = new TextButton("Login", skin);

        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                login();
            }
        });

        add(codeLabel);
        add(codeField);
        row();

        add(passLabel);
        add(passField);
        row();

        add(loginButton);
        pack();
    }

    private void login() {
        Network.RCONLogin login = new Network.RCONLogin();
        login.code = codeField.getText();
        login.pass = passField.getText();
        client.sendTCP(login);
        remove();
    }
}
