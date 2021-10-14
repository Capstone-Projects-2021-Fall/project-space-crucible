package core.server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.HashSet;

public class SpaceServer extends Listener {

    //Server Object
    Server server;
    HashSet<Player> connected = new HashSet();

    public SpaceServer() throws IOException {
        server = new Server() {
            protected Connection newConnection() {
                return new PlayerConnection();
            }
        };
        Network.register(server);

        server.addListener(new Listener(){

            public void connected(Connection c){
                System.out.println("Client connected: " + c.getRemoteAddressTCP().getHostString());
                PlayerConnection connection = (PlayerConnection) c;
                Player player = connection.player;
                player = loadCharacter(c.getID());
                Network.AddPlayer addPlayer = new Network.AddPlayer();
                addPlayer.player = player;
                server.sendToAllTCP(addPlayer);
            }

            public void received(Connection c, Object p) {
                PlayerConnection connection = (PlayerConnection) c;
                Player player = connection.player;

                if(p instanceof Network.MovePlayer){
                    Network.MovePlayer msg = (Network.MovePlayer)p;
                    player.x += msg.x;
                    player.y += msg.y;

                    Network.UpdatePlayer update = new Network.UpdatePlayer();
                    update.id = player.id;
                    update.x = player.x;
                    update.y = player.y;
                    server.sendToAllTCP(update);
                    return;
                }

            }
            //This method will run when a client disconnects from the server
            public void disconnected(Connection c){
                PlayerConnection connection = (PlayerConnection) c;
                if(connection.player != null){
                    Network.RemovePlayer removeCharacter = new Network.RemovePlayer();
                    removeCharacter.id = connection.player.id;
                    server.sendToAllTCP(removeCharacter);
                }
                System.out.println("Client disconnected! " + c.getID());

            }
        });
        server.bind(Network.tcpPort, Network.udpPort);
        server.start();
    }

    Player loadCharacter(int id){
        Player player = new Player();
        player.id = id;
        player.x = 0;
        player.y = 0;
        return player;
    }

    static class PlayerConnection extends Connection{
        public Player player;
    }

    public static void main (String[] args) throws IOException {
        new SpaceServer();
    }

}
