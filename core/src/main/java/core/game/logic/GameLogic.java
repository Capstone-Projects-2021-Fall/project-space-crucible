package core.game.logic;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import core.game.entities.Entity;
import core.game.entities.PlayerPawn;
import core.level.info.LevelData;
import core.level.info.LevelObject;
import core.wad.funcs.GameSprite;
import core.wad.funcs.MIDIFuncs;
import net.mtrop.doom.WadEntry;
import net.mtrop.doom.WadFile;

import java.io.IOException;
import java.util.*;

public class GameLogic {

    private static Timer gameTimer;
    public static ArrayList<Entity> entityList = new ArrayList<>();
    final public static Queue<Entity> newEntityQueue = new Queue<>();
    final public static Queue<Entity> deleteEntityQueue = new Queue<>();
    final public static Map<String, GameSprite> spriteMap = new HashMap<>();
    final public static ArrayList<EntityState> stateList = new ArrayList<>();
    final public static ArrayList<Class<? extends Entity>> entityType = new ArrayList<>();
    final public static Map<Integer, LevelData> levels = new HashMap<>();
    public static LevelData currentLevel = null;
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

    public static void start() {
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
                ((PlayerPawn) e).movementUpdate();
            }
        }

        //Now add and remove all queued new entities
        while (!newEntityQueue.isEmpty()) {
            entityList.add(newEntityQueue.removeFirst());
        }

        while (!deleteEntityQueue.isEmpty()) {
            entityList.remove(deleteEntityQueue.removeFirst());
        }

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

    public static void loadEntities(LevelData level) {

        entityList.clear();

        for (LevelObject obj : level.getObjects()) {
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
        loadEntities(currentLevel);
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

