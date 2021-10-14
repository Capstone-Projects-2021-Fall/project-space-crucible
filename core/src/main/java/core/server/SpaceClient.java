package core.server;

import com.badlogic.gdx.Net;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;


public class SpaceClient extends Listener {

    Client client;
    static String ip = "localhost";
    //Ports for clients to listen on

    static boolean messageRec = false;
    static String message = "";

    public SpaceClient(){
        System.out.println("Connecting to the server!");
        //Create a client object
        client = new Client();
        client.start();

        //register the packet object
        Network.register(client);

        client.addListener(new ThreadedListener(new Listener() {
            public void connected (Connection connection) {
            }

            public void received (Connection connection, Object object) {
                if (object instanceof Network.AddPlayer) {
                    Network.AddPlayer msg = (Network.AddPlayer) object;
//                    ui.addCharacter(msg.player);
                    return;
                }
                if (object instanceof Network.UpdatePlayer) {
//                    ui.updateCharacter((Network.UpdatePlayer)object);
                    return;
                }
                if (object instanceof Network.RemovePlayer) {
                    Network.RemovePlayer msg = (Network.RemovePlayer) object;
//                    ui.removeCharacter(msg.id);
                    return;
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

    public static void main(String[] args) throws Exception{
        new SpaceClient();
    }
}
