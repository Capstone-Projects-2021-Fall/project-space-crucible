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
        kryo.register(RenderData.class);
        kryo.register(InputData.class);
        kryo.register(java.util.ArrayList.class);
        kryo.register(PlayerPawn.class);
        kryo.register(Input.class);
        kryo.register(core.level.info.LevelTile.class);
        kryo.register(core.game.entities.Entity.class);
        kryo.register(com.badlogic.gdx.graphics.Texture.class);
        kryo.register(com.badlogic.gdx.graphics.glutils.PixmapTextureData.class);
        kryo.register(com.badlogic.gdx.graphics.Pixmap$Format.class);

    }

    //Send this to the CLIENT
    public static class RenderData {
        public ArrayList<Entity> entityList;
        public ArrayList<LevelTile> tiles;
        public PlayerPawn playerPawn;
    }

    //Send this to the SERVER
    public static class InputData {
        public Input playerInput; //This has the .isKeyPressed, etc methods.
        public float angle;
    }
}