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

import java.io.IOException;

public class RCONWindow extends Window {

    //final private Table rconTable;
    final private Stage stage;
    final private RCONScreen parent;
    final private Label connectedLabel;
    final private ScrollPane scrollPane;
    final private Table serverLog;
    final private TextField commandField;
    final private TextButton sendButton;
    final private Table commandTable;
    final public static String ip = "100.19.127.86";
    private boolean loggedIn = false;

    public RCONWindow(String title, Skin skin, Stage stage, RCONScreen parent) {
        super(title, skin);
        setResizable(true);
        setMovable(true);
        this.stage = stage;
        this.parent = parent;

        connectedLabel = new Label("Connected to:", skin);
        add(connectedLabel);
        row();

        serverLog = new Table(skin);
        //serverLog.setLayoutEnabled(false);
        scrollPane = new ScrollPane(serverLog);
        scrollPane.setFadeScrollBars(false);
        add(scrollPane).width(320).height(400);
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
        Network.register(RCON.client);
        addClientListener();
        RCON.client.start();

        try {
            RCON.client.connect(5000, ip, Network.tcpPort);
        } catch(IOException io) {System.exit(1);}
    }

    private void updateLog(String message) {
        serverLog.add(new Label(message, getSkin())).width(320);
        serverLog.row();
        scrollPane.setScrollPercentY(100f);
    }

    private void sendCommand() {

        if (commandField.getText().equals("exit")) {
            if (!loggedIn) {
                System.exit(0);
            } else {
                RCON.client.close();
                loggedIn = false;

                try {
                    RCON.client.connect(5000, ip, Network.tcpPort);
                } catch(IOException io) {System.exit(1);}
            }
        }

        updateLog("Sent: " + commandField.getText());

        Network.RCONMessage command = new Network.RCONMessage();
        command.message = commandField.getText();
        RCON.client.sendTCP(command);
        commandField.setText("");
    }

    private void addClientListener() {

        RCON.client.addListener(new Listener.ThreadedListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                Listener.super.connected(connection);
                if (!loggedIn) {
                    stage.addActor(new LoginWindow("Login", getSkin(), RCON.client));
                }
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
                                stage.addActor(new LoginWindow("Login", getSkin(), RCON.client));
                                stage.addActor(new PopupWindow("Error", getSkin(), ((Network.RCONMessage) object).message));
                            } else if (((Network.RCONMessage) object).message.equals("OK")) {
                                loggedIn = true;
                                commandField.setDisabled(false);
                                sendButton.setDisabled(false);
                                connectedLabel.setText("Connected to: " + RCON.client.getRemoteAddressTCP().getHostString() + ":" +RCON.client.getRemoteAddressTCP().getPort());
                            } else {
                                try {
                                    int port = Integer.parseInt(((Network.RCONMessage) object).message);
                                    RCON.client.close();
                                    RCON.client.connect(ip, port);

                                    loggedIn = true;
                                    commandField.setDisabled(false);
                                    sendButton.setDisabled(false);
                                    connectedLabel.setText("Connected to: " + RCON.client.getRemoteAddressTCP().getHostString() + ":"+ RCON.client.getRemoteAddressTCP().getPort());


                                } catch (NumberFormatException | IOException ignored){}
                            }
                        } else {
                            updateLog(((Network.RCONMessage) object).message);
                        }
                    }

                    else if (object instanceof Network.PromptConnectionType) {
                        Network.CheckConnection cc = new Network.CheckConnection();
                        cc.type = Network.ConnectionType.RCON;
                        RCON.client.sendTCP(cc);
                    }

                    else if (object instanceof Network.RCONPlayerStats) {
                        System.out.println("Got stats");
                        parent.live.updatePlayers((Network.RCONPlayerStats) object);
                    }
            }
        }));

    }
}
