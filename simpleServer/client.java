import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class client extends Listener {

    static Client client;
    static String ip = "localhost";
    //Ports for clients to listen on
    static int udpPort = 27960, tcpPort = 27960;

    static boolean messageRec = false;

    public static void main(String[] args) throws Exception{
        System.out.println("Connecting to the server!");
        //Create a client object
        client = new Client();

        //register the packet object
        client.getKryo().register(PacketMessage.class);

        //Start the client
        client.start();
        //The client must be started before a connection can be made

        //Connect to the server
        client.connect(5000, ip, tcpPort, udpPort);

        //Add a listener
        client.addListener(new client());

        System.out.println("Connected! The client program is now waiting for a packet");

        //Stop the program closing before we receive the message.
        while(!messageRec){
            Thread.sleep(1000);
        }
        System.out.println("The client will now exit.");
        System.exit(0);
    }

    public void received(Connection c, Object p){
        if(p instanceof PacketMessage){
            PacketMessage packet = (PacketMessage) p;
            System.out.println("Received the message from the server: " + packet.message);
            messageRec = true;
        }
    }
}
