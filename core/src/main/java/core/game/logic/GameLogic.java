package core.game.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import core.gdx.wad.MyGDxTest;
import core.level.info.LevelData;
import core.level.info.LevelObject;
import core.wad.funcs.GameSprite;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class GameLogic {

    private static Timer gameTimer;
    final public static ArrayList<Entity> entityList = new ArrayList<>();
    final public static Map<String, GameSprite> spriteMap = new HashMap<>();
    final public static ArrayList<EntityState> stateList = new ArrayList<>();
    final public static ArrayList<Class<? extends Entity>> entityType = new ArrayList<>();

    public static void start() {
        gameTimer = new Timer();
        gameTimer.schedule(new TimerTask() {
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

        for (LevelObject obj : level.getObjects()) {
            try {
                entityList.add(entityType.get(obj.tag)
                        .getConstructor(Entity.Position.class, int.class)
                        .newInstance(obj.pos, obj.tag));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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
