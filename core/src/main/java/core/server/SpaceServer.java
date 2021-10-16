package core.server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import core.game.entities.Entity;
import core.game.entities.PlayerPawn;
import core.server.Network.UpdatePlayer;
import core.server.Network.MovePlayer;
import core.server.Network.AddPlayer;
import core.server.Network.RemovePlayer;
import core.server.Network.InputData;
import core.server.Network.RenderData;



import java.io.IOException;
import java.util.HashSet;

public class SpaceServer extends Listener {

    //Server Object
    Server server;
    HashSet<PlayerPawn> connected = new HashSet();
    //Player count

    public SpaceServer() throws IOException {
        server = new Server() {
            protected Connection newConnection() {
                // By providing our own connection implementation, we can store per
                // connection state without a connection ID to state look up.
                return new PlayerConnection();
            }
        };
        Network.register(server);

        server.addListener(new Listener(){

            //When the client connects to the server add a player entity to the game
            public void connected(Connection c){
                System.out.println("Client connected: " + c.getRemoteAddressTCP().getHostString());
                PlayerConnection connection = (PlayerConnection) c;
                PlayerPawn player = connection.player;

                //Wait for everyone to connect
                //Once everyone is connected create a thread for the game loop
                //run the game logic
                //once the game is over set screen state to main menu
//                if(connected.size() != )


                //If the player with the same tag already exists reject the connection
//                for(PlayerPawn otherPlayer: connected) {
//                    if(otherPlayer.getTag() == player.getTag()) {
//                        c.close();
//                        return;
//                    }
//                }
                //Load the player in the game server side
//                player = loadPlayer(c.getID());

                //Add existing client's player entity to newly connected client
//                for(PlayerPawn otherPlayer: connected){
//                    AddPlayer addPlayer = new AddPlayer();
//                    addPlayer.player = player;
//                    connection.sendTCP(addPlayer);
//                }
                connected.add(player);

                //Add newly connected client's player entity to all existing client
//                AddPlayer addPlayer = new Network.AddPlayer();
//                addPlayer.player = player;
//                server.sendToAllTCP(addPlayer);
            }
            //When the client sends a packet to the server handle it
            public void received(Connection c, Object packetData) {
                PlayerConnection connection = (PlayerConnection) c;
                PlayerPawn player = connection.player;

                if(packetData instanceof MovePlayer){
                    //Move the player in the server
                    MovePlayer msg = (MovePlayer) packetData;
//                    player.getPos().x += msg.x;
//                    player.getPos().y += msg.y;

                    System.out.println("x:" + msg.x + " y: " + msg.y);

                    //send the player movement back to the clients
                    UpdatePlayer update = new UpdatePlayer();
//                    update.id = player.getTag();
//                    update.x = player.getPos().x;
//                    update.y = player.getPos().y;
                    update.x = msg.x;
                    update.y = msg.y;
                    update.id = 0;
                    server.sendToAllTCP(update);
                    return;
                }

//                if(packetData instanceof InputData){
//                    InputData input = (InputData) packetData;
//                    //update player movement based on the input
//
//                    RenderData renderData = new RenderData();
//                    //Update render data to send back to client
//                    c.sendTCP(renderData);
//                }
            }
            //This method will run when a client disconnects from the server, remove the character from the game
            public void disconnected(Connection c){
                PlayerConnection connection = (PlayerConnection) c;
//                if(connection.player != null){
//                    RemovePlayer removePlayer = new RemovePlayer();
//                    removePlayer.id = connection.player.getTag();
//                    server.sendToAllTCP(removePlayer);
//                }
                System.out.println("Client disconnected! " + c.getID());
            }
        });
        server.bind(Network.tcpPort, Network.udpPort);
        server.start();
        System.out.println("Server is running");
    }

    PlayerPawn loadPlayer(int id){
        //Load player in the game
        Entity.Position pos = new Entity.Position(0,0,0);
        PlayerPawn player = new PlayerPawn(pos,0);
        return player;
    }

    static class PlayerConnection extends Connection{
        public PlayerPawn player;
    }

    public static void main (String[] args) throws IOException {
        new SpaceServer();
    }

}
