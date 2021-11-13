package core.game.logic;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import core.game.entities.BaseMonster;
import core.game.entities.Entity;
import core.game.entities.PlayerPawn;
import core.game.entities.actions.A_Chase;
import core.game.logic.tileactions.TileAction;
import core.level.info.LevelData;
import core.level.info.LevelObject;
import core.server.Network;
import core.server.Network.RenderData;
import core.server.SpaceServer;
import core.wad.funcs.SoundFuncs;
import net.mtrop.doom.WadEntry;
import net.mtrop.doom.WadFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GameLogic {

    private static Timer gameTimer;
    public static Boolean isSinglePlayer = true;
    public static ArrayList<Entity> entityList = new ArrayList<>();
    final public static Queue<Entity> newEntityQueue = new Queue<>();
    final public static Queue<Entity> deleteEntityQueue = new Queue<>();
    final public static LinkedList<EntityState> stateList = new LinkedList<>();
    final public static Map<String, EntitySpawner> entityTable = new HashMap<>();
    final public static Map<Integer, EntitySpawner> mapIDTable = new HashMap<>();
    final public static Map<Integer, LevelData> levels = new HashMap<>();
    final public static ArrayList<TileAction> effectList = new ArrayList<>();
    public static LevelData currentLevel = null;
    public static Server server = null;
    public static SpaceServer spaceServer = null;
    static boolean goingToNextLevel = false;
    static LevelData nextLevel = null;
    public static boolean switchingLevels = false;
    public static int ticCounter = 0;
    public static int difficulty = 2;

    final public static int BABY = 0;
    final public static int EASY = 1;
    final public static int MEDIUM = 2;
    final public static int HARD = 3;
    final public static int NIGHTMARE = 4;

    final public static int UP = 0;
    final public static int DOWN = 1;
    final public static int LEFT = 2;
    final public static int RIGHT = 3;
    final public static int SHOOT = 4;

    public static void start() {
        if (isSinglePlayer) {
            SoundFuncs.playMIDI(currentLevel.getMIDI());
        } else {
            Network.MIDIData midi = new Network.MIDIData();
            midi.midi = currentLevel.getMIDI();
            server.sendToAllTCP(midi);
            spaceServer.packetsSent += server.getConnections().size();
        }
        gameTimer = new Timer();
        gameTimer.schedule( new TimerTask() {
            @Override
            public void run() {
                gameTick();
            }
        }, Entity.TIC);
    }

    public static void stop() {

        gameTimer.cancel();
        gameTimer.purge();
    }

    private static void gameTick() {

        long startTime = new Date().getTime();

        //Update all existing entities first
        for (Entity e : GameLogic.entityList) {
            e.decrementTics();

            if (e instanceof PlayerPawn) {
                //If PlayerPawn is being controlled by a connected player
                if (isSinglePlayer || SpaceServer.connected.contains(SpaceServer.idToPlayerNum.get(e.getTag()))) {
                    ((PlayerPawn) e).movementUpdate();
                } else {
                    //Set player to chase closest enemy
                    setPlayerBotTarget((PlayerPawn) e);

                    try {
                        if (e.getHealth() > 0) {

                            Vector2 start = e.getCenter();
                            Entity target = ((PlayerPawn) e).botTarget;

                            Vector2 distance = new Vector2();
                            distance.x = target.getPos().x - e.getPos().x;
                            distance.y = target.getPos().y - e.getPos().y;

                            if ((CollisionLogic.checkFOVForEntity(start.x, start.y, e.getPos().angle, e, target)
                                    || distance.len() < 64f)
                                    && e.getCurrentStateIndex() <= e.getStates()[Entity.MELEE]) {


                                e.getPos().angle = distance.angleDeg();
                                e.setState(e.getStates()[Entity.MISSILE]);
                                e.hitScanAttack(e.getPos().angle, 15);
                                SoundFuncs.playSound("pistol/shoot");
                                GameLogic.alertMonsters(e);
                            } else {
                                e.pursueTarget(target);
                                if (e.getCurrentStateIndex() == e.getStates()[Entity.IDLE]) {
                                    e.setState(e.getStates()[Entity.WALK]);
                                }
                            }
                        }
                    } catch (NullPointerException ignored){}
                }
            }
        }

        //Now add and remove all queued new entities
        while (!newEntityQueue.isEmpty()) {
            entityList.add(newEntityQueue.removeFirst());
        }

        while (!deleteEntityQueue.isEmpty()) {
            entityList.remove(deleteEntityQueue.removeFirst());
        }

        if(!isSinglePlayer){
            //Send render data packet
            for (Connection c : server.getConnections()) {
                RenderData renderData = new RenderData();
                renderData.entityList = entitiesInsideView(c);
                renderData.playerPawn = getPlayer(SpaceServer.idToPlayerNum.indexOf(c.getID()));
                server.sendToTCP(c.getID(), renderData);
                spaceServer.packetsSent++;
            }
        }

        if (!goingToNextLevel) {

            long endTime = new Date().getTime();
            long subtract = MathUtils.clamp(endTime - startTime, 0, Entity.TIC);

            try {
                gameTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        gameTick();
                    }
                }, Entity.TIC - subtract);
            } catch (IllegalStateException ignored){}
        } else {
            switchingLevels = true;
            if (!isSinglePlayer) {
                System.out.println("Size before: " + SpaceServer.idToPlayerNum.size());
                SpaceServer.idToPlayerNum.removeIf(integer -> (!SpaceServer.connected.contains(integer) && integer != -1));
                SpaceServer.clientData.idToPlayerNum = SpaceServer.idToPlayerNum;
                server.sendToAllTCP(SpaceServer.clientData);
                spaceServer.packetsSent += server.getConnections().size();
                System.out.println("Size after: " + SpaceServer.idToPlayerNum.size());
                Network.LevelChange lc = new Network.LevelChange();
                System.out.println("Going to level " + nextLevel.getLevelnumber());
                lc.number = nextLevel.getLevelnumber();
                server.sendToAllTCP(lc);
                spaceServer.packetsSent += server.getConnections().size();
            }
            changeLevel(nextLevel);
        }

        ticCounter++;
    }

    public static void loadEntities(LevelData level, boolean editor) {

        entityList.clear();

        for (LevelObject obj : level.getObjects()) {

            //Skip object if it is not on this difficulty. Always show everything in the editor
            if (!obj.skill[difficulty] && !editor) {continue;}

            if (obj.type == 0) {
                if (isSinglePlayer && obj.tag > 1 && !editor) continue;

                System.out.println("Server IS " + (server == null ? "" : "NOT") +  " null.");

                if (server != null && obj.tag > SpaceServer.connected.size()) {
                    System.out.println("Skipping player " + obj.tag + " because they don't exist.");
                    continue;
                }
            }

            try {
                entityList.add(GameLogic.mapIDTable.get(obj.type)
                        .spawnEntity(new Entity.Position(obj.xpos, obj.ypos, obj.angle), obj.tag, obj.layer, obj.ambush));
            } catch (Exception e) {
                System.out.println("Unknown Entity with map ID " + obj.type);
            }
        }
    }

    public static void loadLevels(Array<WadFile> wads) {

        for (WadFile file : wads) {
            for (WadEntry we : file) {
                if (we.getName().startsWith("LEVEL")) {
                    int levelnum = Integer.parseInt(we.getName().substring(5));
                    try {
                        levels.put(levelnum, new LevelData(file, levelnum));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        currentLevel = levels.get(1);
    }

    public static void changeLevel(LevelData level) {
        currentLevel = level;
        if (isSinglePlayer) {
            if (SoundFuncs.sequencer.isRunning()) {
                SoundFuncs.stopMIDI();
            }
            if (level.getMIDI() != null) {
                SoundFuncs.playMIDI(level.getMIDI());
            }
        } else {
            Network.MIDIData midi = new Network.MIDIData();
            midi.midi = level.getMIDI();
            server.sendToAllTCP(midi);
            spaceServer.packetsSent += server.getConnections().size();
        }
        goingToNextLevel = false;
        switchingLevels = false;
        nextLevel = null;
        ticCounter = 0;
        loadEntities(currentLevel, false);

        gameTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                gameTick();
            }
        }, Entity.TIC);
    }

    public static PlayerPawn getPlayer(int tag) {

        for (Entity e : entityList) {
            if (e instanceof PlayerPawn && e.getTag() == tag) {
                return (PlayerPawn) e;
            }
        }
        return null;
    }

    public static void readyChangeLevel(LevelData newLevelData) {
        goingToNextLevel = true;
        nextLevel = newLevelData;
    }

    //check if the entity is inside the player's camera view
    public static ArrayList<Entity> entitiesInsideView(Connection c) {
        ArrayList<Entity> inside = new ArrayList<>();
        Network.CameraData player = ((SpaceServer.PlayerConnection)c).cameraData;
        int playerNum = SpaceServer.idToPlayerNum.indexOf(c.getID());
        try {
            Rectangle playerview = new Rectangle(player.camerapositon.x, player.camerapositon.y, player.width, player.hight);
            for (Entity e : entityList) {
                if (playerview.overlaps(e.getBounds())) {
                    inside.add(e);
                }
            }

            if (!inside.contains(getPlayer(playerNum))) {
                inside.add(getPlayer(playerNum));
            }
        } catch(NullPointerException n) {
            inside.add(getPlayer(playerNum));
        }
        return inside;
    }

    public static void playServerSound(String name) {
        Network.SoundData soundData = new Network.SoundData();
        soundData.sound = name;
        server.sendToAllTCP(soundData);
        spaceServer.packetsSent += server.getConnections().size();
    }

    public static void alertMonsters(Entity soundSource) {

        for (Entity e : entityList) {

            Vector2 distance = new Vector2();
            distance.x = soundSource.getPos().x - e.getPos().x;
            distance.y = soundSource.getPos().y - e.getPos().y;

            //Alert monster IF...

            if (e.getStates()[Entity.WALK] != -1        //There is a walk animation
                && e instanceof BaseMonster             //And the entity is a monster
                && ((BaseMonster) e).getTarget() == -1  //And is not busy
                && distance.len() < 1024f               //And is in hearing range
                && !((BaseMonster) e).ambush) {         //And is not in ambush mode
                ((BaseMonster) e).setTarget(GameLogic.entityList.indexOf(soundSource));
                SoundFuncs.playSound(((BaseMonster) e).getSound(BaseMonster.SEESOUND));
                e.setState(e.getStates()[Entity.WALK]);
            }
        }
    }

    private static void setPlayerBotTarget(PlayerPawn p) {
        Entity newTarget = null;
        Vector2 targetDistance = null;

        for (Entity t : GameLogic.entityList) {
            if (t.getHealth() > 0 && t instanceof BaseMonster) {
                final float startX = p.getPos().x;
                final float startY = p.getPos().y;

                //Get distance
                Vector2 distance = new Vector2();
                distance.x = t.getPos().x - startX;
                distance.y = t.getPos().y - startY;

                if (newTarget == null) {
                    newTarget = t;
                    targetDistance = distance;
                } else {
                    if (distance.len() < targetDistance.len()) {
                        newTarget = t;
                        targetDistance = distance;
                    }
                }
                System.out.println("NewTarget: " + newTarget.getClass().getName());
            }
        }
        p.botTarget = newTarget;
    }
}

