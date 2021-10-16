package core.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.input.GestureDetector;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import core.game.entities.PlayerPawn;
import core.server.Network.*;


import java.io.IOException;


public class SpaceClient extends Listener {

    Client client;
    static String ip = "localhost";

    public SpaceClient(){
        System.out.println("Connecting to the server!");
        //Create a client object
        client = new Client();
        client.start();
        //register the packets
        Network.register(client);

        client.addListener(new ThreadedListener(new Listener() {
            public void connected (Connection connection) {
            }

            public void received (Connection connection, Object object) {
//                if (object instanceof AddPlayer) {
//                    AddPlayer msg = (AddPlayer) object;
//                    addPlayer(msg.player);
//                    return;
//                }
                if (object instanceof UpdatePlayer) {
                    updatePlayer((UpdatePlayer) object);
                    return;
                }
//                if (object instanceof RemovePlayer) {
//                    //msg contains the player tag
//                    RemovePlayer msg = (RemovePlayer) object;
//                    removePlayer(msg.id);
//                    return;
//                }
                //If the server sends RenderData object update the client's gamescreen
//                if(object instanceof RenderData){
//                    RenderData packet = (RenderData) object;
//                    renderData(packet);
//                    return;
//                }
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
        //This loop will handle all the movement from the player and send it to the server as a MovePlayer instance
        while(true){
            UpdateMovement();
            break;
        }
    }

    private void renderData(RenderData packet) {
        //Render the client screen

    }

    public void addPlayer(PlayerPawn player){
        //Add the player to the game
    }
    public void updatePlayer(UpdatePlayer msg){
        //Update the player
        System.out.println("x:" + msg.x + " y: " + msg.y);
        return;
    }
    public void removePlayer(int playerTag){
        //Remove the player entity from the game
    }

    public void UpdateMovement(){

        MovePlayer msg = new MovePlayer();
        msg.x = 1;
        msg.y = 1;
        int speed = 120;

//        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))
//            msg.x -= speed * Gdx.graphics.getDeltaTime();
//        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))
//            msg.x += speed * Gdx.graphics.getDeltaTime();
//        if(Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W))
//            msg.y += speed * Gdx.graphics.getDeltaTime();
//        if(Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S))
//            msg.y -= speed * Gdx.graphics.getDeltaTime();

        if(msg != null)
            client.sendTCP(msg);
    }


    public static void main(String[] args) throws Exception{
        new SpaceClient();
    }
}
