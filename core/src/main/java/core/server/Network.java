package core.server;

import com.badlogic.gdx.Input;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import core.game.entities.Entity;
import core.game.entities.PlayerPawn;
import core.level.info.LevelTile;

import java.util.ArrayList;

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
        kryo.register(RenderData.class);
        kryo.register(InputData.class);
    }

    //Send this to the CLIENT
    public static class AddPlayer{
        public PlayerPawn player;
    }

    //Send this to the CLIENT
    public static class UpdatePlayer{
        public int id;
        public float x, y;
    }

    //Send this to the Server
    public static class MovePlayer{
        public float x, y;
    }

    //Send this to the CLIENT
    public static class RemovePlayer{
        public int id;
    }

    //Send this to the CLIENT
    public static class RenderData {
        public ArrayList<Entity> entityList;
        public ArrayList<LevelTile> tiles;
    }

    //Send this to the SERVER
    public static class InputData {
        public Input playerInput; //This has the .isKeyPressed, etc methods.
    }
}
