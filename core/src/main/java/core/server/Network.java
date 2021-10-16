package core.server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import core.game.entities.PlayerPawn;

//This class will store things common to both client and server
public class Network {

    //Ports for clients to listen on
    public static int udpPort = 27960, tcpPort = 27970;

    public static void register(EndPoint endPoint){
        Kryo kryo = endPoint.getKryo();
        kryo.register(UpdatePlayer.class);
        kryo.register(AddPlayer.class);
        kryo.register(MovePlayer.class);
        kryo.register(RemovePlayer.class);
    }

    public static class AddPlayer{
        public PlayerPawn player;
    }

    public static class UpdatePlayer{
        public int id;
        public float x, y;
    }

    public static class MovePlayer{
        public float x, y;
    }

    public static class RemovePlayer{
        public int id;
    }
}
