package core.game.logic;

import com.badlogic.gdx.utils.Array;
import core.game.entities.BaseMonster;
import core.game.entities.Entity;
import core.game.entities.PlayerPawn;
import core.game.entities.Worm;
import core.level.info.LevelData;
import core.level.info.LevelObject;
import core.wad.funcs.GameSprite;
import net.mtrop.doom.WadEntry;
import net.mtrop.doom.WadFile;

import java.io.IOException;
import java.util.*;

public class GameLogic {

    private static Timer gameTimer;
    final public static ArrayList<Entity> entityList = new ArrayList<>();
    final public static Map<String, GameSprite> spriteMap = new HashMap<>();
    final public static ArrayList<EntityState> stateList = new ArrayList<>();
    final public static ArrayList<Class<? extends Entity>> entityType = new ArrayList<>();
    final public static Map<Integer, LevelData> levels = new HashMap<>();
    public static LevelData currentLevel = null;

    public static void start() {
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
        for (Entity e : GameLogic.entityList) {
            e.decrementTics();

            if (e instanceof PlayerPawn) {
                ((PlayerPawn) e).movementUpdate();
            }
        }

        gameTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                gameTick();
            }
        }, Entity.TIC);
    }

    public static void loadEntities(LevelData level) {

        entityList.clear();

        for (LevelObject obj : level.getObjects()) {
            try {
                entityList.add(entityType.get(obj.type)
                        .getConstructor(Entity.Position.class, int.class)
                        .newInstance(obj.pos, obj.tag));
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

    public static PlayerPawn getPlayer(int tag) {

        for (Entity e : entityList) {
            if (e instanceof PlayerPawn && e.getTag() == tag) {
                return (PlayerPawn) e;
            }
        }

        return null;
    }
}

