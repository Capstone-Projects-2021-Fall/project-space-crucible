package core.server;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import core.gdx.wad.GameScreen;
import core.server.Network.*;
import core.wad.funcs.MIDIFuncs;


import java.io.IOException;


public class SpaceClient extends Listener {

    Client client;
    static String ip = "localhost";
    GameScreen screen;

    public SpaceClient(GameScreen screen){
        System.out.println("Connecting to the server!");
        //Create a client object
        client = new Client(20000, 20000);
        client.start();
        //register the packets
        Network.register(client);
        this.screen = screen;

        client.addListener(new ThreadedListener(new Listener() {
            public void connected (Connection connection) {
            }

            public void received (Connection connection, Object object) {
                //If the server sends RenderData object update the client's gamescreen
                if(object instanceof RenderData){
                    screen.setRenderData((RenderData) object);
                } else if (object instanceof MIDIData) {
                    if (((MIDIData) object).midi != null && !((MIDIData) object).midi.equals("") ) {
                        MIDIFuncs.playMIDI(((MIDIData) object).midi);
                    } else {
                        MIDIFuncs.stopMIDI();
                    }
                }
            }
            public void disconnected (Connection connection) {
                System.exit(0);
            }
        }));


        //Connect the client to the server
        try {
            client.connect(5000, ip, Network.tcpPort, Network.udpPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getInput(boolean[] controls){
        InputData inputData = new InputData();
        inputData.controls = controls;
        inputData.angle = screen.getAngle();
        client.sendTCP(inputData);
    }

}