package core.server;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import core.game.entities.Entity;
import core.game.entities.PlayerPawn;
import core.game.entities.actions.A_Chase;
import core.game.entities.actions.A_PrintMessage;
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
        kryo.register(core.level.info.LevelTile.class);
        kryo.register(core.level.info.LevelTile.TilePosition.class);
        kryo.register(core.game.entities.Entity.class);
        kryo.register(core.game.entities.Entity.Position.class);
        kryo.register(core.game.logic.EntityState.class);
        kryo.register(A_Chase.class);
        kryo.register(A_PrintMessage.class);
        kryo.register(core.game.entities.PlayerPawn.class);
        kryo.register(core.game.entities.Worm.class);
        kryo.register(core.game.entities.Fireball.class);
        kryo.register(Rectangle.class);
        kryo.register(boolean[].class);
        kryo.register(Integer[].class);
    }

    //Send this to the CLIENT
    public static class RenderData {
        public ArrayList<Entity> entityList;
        public ArrayList<LevelTile> tiles;
        public PlayerPawn playerPawn;
    }

    //Send this to the SERVER
    public static class InputData {
        public boolean[] controls;
        public float angle;
    }
}
