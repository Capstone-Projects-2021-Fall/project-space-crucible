package core.game.logic;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.esotericsoftware.kryonet.Server;
import core.game.entities.Entity;
import core.game.entities.PlayerPawn;
import core.level.info.LevelData;
import core.level.info.LevelObject;
import core.server.Network;
import core.level.info.LevelTile;
import core.wad.funcs.GameSprite;
import core.wad.funcs.MIDIFuncs;
import core.wad.funcs.WadFuncs;
import net.mtrop.doom.WadEntry;
import net.mtrop.doom.WadFile;

import core.server.Network.RenderData;

import java.io.IOException;
import java.util.*;

public class GameLogic {

    private static Timer gameTimer;
    public static Boolean isSinglePlayer = true;
    public static ArrayList<Entity> entityList = new ArrayList<>();
    final public static Queue<Entity> newEntityQueue = new Queue<>();
    final public static Queue<Entity> deleteEntityQueue = new Queue<>();
    final public static ArrayList<EntityState> stateList = new ArrayList<>();
    final public static ArrayList<Class<? extends Entity>> entityType = new ArrayList<>();
    final public static Map<Integer, LevelData> levels = new HashMap<>();
    public static LevelData currentLevel = null;
    private static Server server;
    public static boolean[] controls;
    static boolean goingToNextLevel = false;
    static LevelData nextLevel = null;
    public static boolean switchingLevels = false;
    public static boolean midTic = false;
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

    public static void start(Server s) {
        server = s;
        MIDIFuncs.playMIDI(currentLevel.getMIDI());
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
        //Update all existing entities first
        for (Entity e : GameLogic.entityList) {
            e.decrementTics();

            //Check ticCounter because Concurrency error might occur if player shoots on first tic.
            if (e instanceof PlayerPawn) {
                ((PlayerPawn) e).movementUpdate(controls);
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
            RenderData renderData = new RenderData();
            renderData.entityList = GameLogic.entityList;
            renderData.tiles = GameLogic.currentLevel.getTiles();
            renderData.playerPawn = getPlayer(0);
            server.sendToAllTCP(renderData);
        }

        gameTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                gameTick();
            }
        }, Entity.TIC);

        if (!goingToNextLevel) {
            gameTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    gameTick();
                }
            }, Entity.TIC);
        } else {
            switchingLevels = true;
            changeLevel(nextLevel);
        }

        ticCounter++;
    }

    public static void loadEntities(LevelData level, boolean editor) {

        entityList.clear();

        for (LevelObject obj : level.getObjects()) {

            //Skip object if it is not on this difficulty. Always show everything in the editor
            if (!obj.skill[difficulty] && !editor) {continue;}

            try {
                entityList.add(entityType.get(obj.type)
                        .getConstructor(Entity.Position.class, int.class)
                        .newInstance(new Entity.Position(obj.xpos, obj.ypos, obj.angle), obj.tag));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadLevels(WadFile file, Array<WadFile> wads) {

        for (WadEntry we : file) {
            if (we.getName().startsWith("LEVEL")) {
                int levelnum = Integer.parseInt(we.getName().substring(5));
                try {
                    levels.put(levelnum, new LevelData(file, levelnum, wads));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        currentLevel = levels.get(1);
    }

    public static void changeLevel(LevelData level) {
        currentLevel = level;
        if (MIDIFuncs.sequencer.isRunning()) {
            MIDIFuncs.stopMIDI();
        }
        if (level.getMIDI() != null) {
            MIDIFuncs.playMIDI(level.getMIDI());
        }
        goingToNextLevel = false;
        switchingLevels = false;
        nextLevel = null;
        loadEntities(currentLevel, false);
        gameTimer.schedule( new TimerTask() {
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
}

