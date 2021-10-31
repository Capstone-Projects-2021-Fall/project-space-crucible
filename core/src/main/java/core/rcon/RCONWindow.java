package core.rcon;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import core.gdx.wad.PopupWindow;
import core.server.Network;
import core.server.SpaceClient;
import org.lwjgl.system.CallbackI;

import java.io.IOException;

public class RCONWindow extends Window {

    final private Stage stage;
    final private Label connectedLabel;
    final private ScrollPane scrollPane;
    final private List<String> serverLog;
    final private Array<String> serverLogArray = new Array<>();
    final private TextField commandField;
    final private TextButton sendButton;
    final private Table commandTable;
    private boolean loggedIn = false;

    final private Client client = new Client(8192, 8192);

    public RCONWindow(String title, Skin skin, Stage stage) {
        super(title, skin);
        setModal(true);
        setResizable(true);
        setMovable(true);
        this.stage = stage;

        connectedLabel = new Label("Connected to:", skin);
        add(connectedLabel);
        row();

        serverLog = new List<>(skin);
        serverLog.setItems(serverLogArray);
        scrollPane = new ScrollPane(serverLog);
        add(scrollPane).width(320).height(240);
        row();

        commandField = new TextField("", skin);
        sendButton = new TextButton("Send", skin);

        sendButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                sendCommand();
            }
        });

        commandTable = new Table(skin);

        commandTable.add(commandField);
        commandTable.add(sendButton);
        add(commandTable);
        pack();

        commandField.setDisabled(true);
        sendButton.setDisabled(true);

        //Prepare Client
        Network.register(client);
        addClientListener();
        client.start();

        try {
            client.connect(5000, "localhost", Network.tcpPort);
        } catch(IOException io) {System.exit(1);}
    }

    private void updateLog(String message) {
        serverLogArray.add(message);
        serverLog.setItems(serverLogArray);
        scrollPane.setScrollPercentY(100f);
    }

    private void sendCommand() {
        updateLog("Sent: " + commandField.getText());

        Network.RCONMessage command = new Network.RCONMessage();
        command.message = commandField.getText();
        client.sendTCP(command);
        commandField.setText("");
    }

    private void addClientListener() {

        client.addListener(new Listener.ThreadedListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                Listener.super.connected(connection);
                stage.addActor(new LoginWindow("Login", getSkin(), client));
            }

            @Override
            public void disconnected(Connection connection) {
                Listener.super.disconnected(connection);
            }

            @Override
            public void received(Connection connection, Object object) {
                Listener.super.received(connection, object);

                    if (object instanceof Network.RCONMessage) {

                        if (!loggedIn) {
                            if (((Network.RCONMessage) object).message.equals("Bad login")) {
                                stage.addActor(new LoginWindow("Login", getSkin(), client));
                                stage.addActor(new PopupWindow("Error", getSkin(), ((Network.RCONMessage) object).message));
                            } else if (((Network.RCONMessage) object).message.equals("OK")) {
                                loggedIn = true;
                                commandField.setDisabled(false);
                                sendButton.setDisabled(false);
                            }
                        } else {
                            updateLog(((Network.RCONMessage) object).message);
                        }
                    }
            }
        }));

    }
}