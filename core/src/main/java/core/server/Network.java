package core.server;

import com.badlogic.gdx.math.Rectangle;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import core.game.entities.Entity;
import core.game.entities.PlayerPawn;
import core.game.entities.actions.*;
import core.level.info.LevelTile;

import java.util.ArrayList;
import java.util.HashSet;

//This class will store things common to both client and server
public class Network {

    //Ports for clients to listen on
    public static int udpPort = 27960, tcpPort = 27970;

    public static void register(EndPoint endPoint){
        Kryo kryo = endPoint.getKryo();

        //Network classes
        kryo.register(RenderData.class);
        kryo.register(InputData.class);
        kryo.register(MIDIData.class);
        kryo.register(ClientData.class);
        kryo.register(SoundData.class);
        kryo.register(CreateLobby.class);
        kryo.register(JoinLobby.class);
        kryo.register(ServerDetails.class);
        kryo.register(ValidLobby.class);



        //Level classes
        kryo.register(core.level.info.LevelTile.class);
        kryo.register(core.level.info.LevelTile.TilePosition.class);

        //Entity classes
        kryo.register(core.game.entities.Entity.class);
        kryo.register(core.game.entities.Entity.Position.class);
        kryo.register(core.game.logic.EntityState.class);

        //State Actions
        kryo.register(A_Look.class);
        kryo.register(A_Projectile.class);
        kryo.register(A_MeleeAttack.class);
        kryo.register(A_Fall.class);
        kryo.register(A_FaceTarget.class);
        kryo.register(A_Chase.class);
        kryo.register(A_Pain.class);
        kryo.register(A_Scream.class);
        kryo.register(A_PrintMessage.class);

        //Entities
        kryo.register(core.game.entities.PlayerPawn.class);
        kryo.register(core.game.entities.Worm.class);
        kryo.register(core.game.entities.Fireball.class);
        kryo.register(core.game.entities.Serpentipede.class);
        kryo.register(core.game.entities.BulletPuff.class);
        kryo.register(core.game.entities.Blood.class);

        //LibGDX classes
        kryo.register(Rectangle.class);

        //Stock Java classes
        kryo.register(java.lang.Class.class);
        kryo.register(java.util.ArrayList.class);
        kryo.register(boolean[].class);
        kryo.register(Integer[].class);
        kryo.register(String[].class);
        kryo.register(java.util.HashSet.class);
    }

    //Send this to the CLIENT
    public static class RenderData {
        public ArrayList<Entity> entityList;
        public ArrayList<LevelTile> tiles;
        public PlayerPawn playerPawn;
        public String username;
    }

    public static class MIDIData {
        public String midi;
    }

//    Send this to the client
    public static class ClientData{
        public HashSet<Integer> connected;
        public int playerCount;
    }

    //Send this to the SERVER
    public static class InputData {
        public String username;
        public boolean[] controls;
        public float angle;
    }

    public static class SoundData {
        public String sound;
    }

    public static class CreateLobby{
        public int playerCount;
    }
    public static class JoinLobby{
        public String lobbyCode;
    }
    public static class ServerDetails{
        public int tcpPort;
        public int udpPort;
        public String lobbyCode;
    }
    public static class ValidLobby{
        public boolean valid;
    }

}
