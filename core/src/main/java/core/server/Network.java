package core.server;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import core.game.entities.Entity;
import core.game.entities.PlayerPawn;
import core.game.entities.actions.*;
import core.level.info.LevelData;
import core.level.info.LevelObject;
import core.level.info.LevelTile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

//This class will store things common to both client and server
public class Network {

    public static enum ConnectionType {
        PLAYER,
        RCON
    }

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
        kryo.register(StartGame.class);
        kryo.register(CameraData.class);
        kryo.register(LevelChange.class);
        kryo.register(ChatMessage.class);
        kryo.register(AddTile.class);
        kryo.register(AddObject.class);
        kryo.register(LevelInfo.class);
        kryo.register(RCONLogin.class);
        kryo.register(RCONMessage.class);
        kryo.register(CheckConnection.class);
        kryo.register(ConnectionType.class);
        kryo.register(PromptConnectionType.class);

        //Level Classes
        kryo.register(LevelData.class);
        kryo.register(LevelTile.class);
        kryo.register(LevelTile.TilePosition.class);
        kryo.register(LevelObject.class);

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
        kryo.register(core.game.entities.Zombieman.class);

        //LibGDX classes
        kryo.register(Rectangle.class);
        kryo.register(Vector3.class);

        //Stock Java classes
        kryo.register(java.lang.Class.class);
        kryo.register(java.util.ArrayList.class);
        kryo.register(boolean[].class);
        kryo.register(Integer[].class);
        kryo.register(String[].class);
        kryo.register(java.util.HashSet.class);
        kryo.register(java.util.LinkedList.class);
    }

    //Send this to the CLIENT
    public static class RenderData {
        public ArrayList<Entity> entityList;
        public PlayerPawn playerPawn;
        public String username;
    }

    public static class MIDIData {
        public String midi;
    }

//    Send this to the client
    public static class ClientData{
        public HashSet<Integer> connected;
        public List<Integer> idToPlayerNum;
    }

    //Send this to the SERVER
    public static class InputData {
        public String username;
        public boolean[] controls;
        public float angle;
    }

    public static class CameraData {
        public Vector3 camerapositon;
        public float width;
        public float hight;
    }

    public static class SoundData {
        public String sound;
    }
    public static class LevelChange{
        public int number;
    }
    public static class CreateLobby{
        public ArrayList<String> names;
        public ArrayList<String> hashes;
    }
    public static class JoinLobby{
        public String lobbyCode;
        public ArrayList<String> names;
        public ArrayList<String> hashes;
    }
    public static class ServerDetails{
        public int tcpPort;
        public String lobbyCode;
        public String rconPass;
    }
    public static class ValidLobby{
        public boolean valid;
        public String reason;
    }
    public static class StartGame{
        public boolean startGame;
    }
    public static class ChatMessage {
        public String sender;
        public String message;
    }
    public static class LevelInfo {
        public Integer levelNumber;
        public String levelName;
        public String levelMIDI;
    }
    public static class AddTile {
        public Integer levelNumber;
        public LevelTile levelTile;
    }
    public static class AddObject {
        public Integer levelNumber;
        public LevelObject levelObject;
    }
    public static class RCONLogin {
        public String code;
        public String pass;
    }
    public static class RCONMessage {
        public String message;
    }
    public static class CheckConnection {
        public ConnectionType type;
    }
    public static class PromptConnectionType{}
}
