import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class server extends Listener {

    //Server Object
    static Server server;
    //Ports for clients to listen on
    static int udpPort = 27960, tcpPort = 27960;

    public static void main(String[] args) throws Exception {
        //Create the server
        server = new Server();
        //Register a packet class
        server.getKryo().register(PacketMessage.class);
        //Bind server to a port
        server.bind(tcpPort, udpPort);

        //start the server
        server.start();

        //add the listener
        server.addListener(new server());

        System.out.println("Server is running!");
    }

    //This method will run when a client connects to the server
    public void connected(Connection c){
        System.out.println("Client connected: " + c.getRemoteAddressTCP().getHostString());

        //Create a message packet
        PacketMessage packetMessage = new PacketMessage();
        packetMessage.message = "Hello from server";

        c.sendTCP(packetMessage);
        //c.sendUDP(packetMessage);
    }

    //This method will run when a packet is received
    public void received(Connection c, Object p){
        System.out.println("Received a packet from " + c.getRemoteAddressTCP().getHostString());
    }

    //This method will run when a client disconnects from the server
    public void disconnected(Connection c){
        System.out.println("Client disconnected!");
    }


}
