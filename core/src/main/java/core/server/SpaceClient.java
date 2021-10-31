package core.server;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import core.game.logic.GameLogic;
import core.gdx.wad.GameScreen;
import core.gdx.wad.StartMenu;
import core.server.Network.*;
import core.wad.funcs.SoundFuncs;

import java.io.IOException;
import java.util.HashMap;

public class SpaceClient implements Listener {

    Client client;
    static String ip = "localhost";
    GameScreen screen;
    public ValidLobby validLobby;
    StartMenu startMenu;


    public SpaceClient(GameScreen screen, StartMenu startMenu){
        this.screen = screen;
        this.startMenu = startMenu;

        client = new Client(8192, 8192);
        client.start();
        //register the packets
        Network.register(client);

        client.addListener(new ThreadedListener(new Listener() {
            public void connected (Connection connection) {
            }

            public void received (Connection connection, Object object) {
                if(object instanceof ServerDetails) {
                    screen.setServerDetails((ServerDetails) object);
                    client.close(); //Close connection to Master Server
                    try {
                        client.connect(5000, ip, ((ServerDetails) object).tcpPort); //Join the server
                        startMenu.myGDxTest.setScreen(screen);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                //If the server sends RenderData object update the client's gamescreen
                else if(object instanceof RenderData){
                    screen.setRenderData((RenderData) object);
                }
                //If server sends MIDIData, change client's music
                else if (object instanceof MIDIData) {
                    if (((MIDIData) object).midi != null && !((MIDIData) object).midi.equals("") ) {
                        SoundFuncs.playMIDI(((MIDIData) object).midi);
                    } else {
                        SoundFuncs.stopMIDI();
                    }
                }
                //If server sends ClientData set the client data
                else if(object instanceof ClientData){
                    screen.setClientData((ClientData) object);
                }
                //If server sends SoundData, play sound matching the given name
                else if (object instanceof SoundData) {
                    SoundFuncs.playSound(((SoundData) object).sound);
                }
                //If server sends ValidLobby unblock the startMenu
                else if( object instanceof ValidLobby){
                    validLobby = (ValidLobby) object;
                    synchronized (startMenu){
                        startMenu.notifyAll();
                    }
                }
                //If server sends StartGame set the startGame value to it
                else if(object instanceof StartGame){
                    screen.startGame = ((StartGame) object).startGame;
                } else if (object instanceof LevelChange) {
                    GameLogic.currentLevel = GameLogic.levels.get(((LevelChange) object).number);
                }

                else if (object instanceof ChatMessage) {
                    screen.addChatToWindow((ChatMessage) object);
                }
            }
            public void disconnected (Connection connection) {
//                System.out.println("Client Disconnected: Closing the application!");
//                System.exit(0);
            }
        }));


        //Connect the client to the server
        try {
            client.connect(5000, ip, Network.tcpPort);
        } catch (IOException e) {
            System.out.println("Master Server is not running!");
            client = null;
        }
    }

    public void makeLobby(){
        CreateLobby createLobby = new CreateLobby();
        client.sendTCP(createLobby);
    }
    public void sendLobbyCode(String lCode){
        Network.JoinLobby joinLobby = new Network.JoinLobby();
        joinLobby.lobbyCode = lCode;
        client.sendTCP(joinLobby);
    }

    public void getInput(boolean[] controls){
        InputData inputData = new InputData();
        inputData.controls = controls;
        inputData.angle = screen.getAngle();
        client.sendTCP(inputData);
    }

    public void getCameraData(CameraData cameradata) {
        client.sendTCP(cameradata);
    }

    public Client getClient(){
        return client;
    }

}
