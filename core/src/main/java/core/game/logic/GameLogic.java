package core.game.logic;

import com.badlogic.gdx.Gdx;
import core.gdx.wad.MyGDxTest;
import core.wad.funcs.GameSprite;

import java.util.*;

public class GameLogic {

    private static Timer gameTimer;
    final public static ArrayList<Entity> entityList = new ArrayList<>();
    final public static Map<String, GameSprite> spriteMap = new HashMap<>();
    final public static ArrayList<EntityState> stateList = new ArrayList<>();

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
}
