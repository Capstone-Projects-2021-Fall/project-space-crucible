package core.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import core.game.entities.actions.A_Chase;
import core.game.logic.CollisionLogic;
import core.game.logic.GameLogic;
import core.level.info.LevelData;

import java.awt.*;

public class PlayerPawn extends Entity {

    final public static int HEALTH = 100;
    final private static int SPEED = 120;
    final private static int WIDTH = 32;
    final private static int HEIGHT = 56;

    final public static int IDLESTATE = 0;
    final public static int WALKSTATE = 1;
    final public static int MELEESTATE = 5;
    final public static int MISSILESTATE = 6;
    final public static int PAINSTATE = 7;
    final public static int DEATHSTATE = 9;


    public PlayerPawn(){}

    public PlayerPawn(Position pos, int tag) {
        super(HEALTH, pos, SPEED, WIDTH, HEIGHT,
                new Integer[]{IDLESTATE, WALKSTATE, MELEESTATE, MISSILESTATE, PAINSTATE, DEATHSTATE}, tag,
                SOLID);
    }

    public void movementUpdate(boolean[] controls) {

        if(controls == null){
            return;
        }
        //Debug keys- play, pain And death animations
        if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
            if (GameLogic.levels.get(GameLogic.currentLevel.getLevelnumber()+1) != null) {
                GameLogic.readyChangeLevel(getNewLevelData());
            }
        }

        float checkPosX = getPos().x;
        float checkPosY = getPos().y;

        //Is a movement key is CURRENTLY pressed, move player.

        if (getHealth() > 0) {
            if(controls[GameLogic.LEFT])
                checkPosX -= getSpeed() * Gdx.graphics.getDeltaTime();
            if(controls[GameLogic.RIGHT])
                checkPosX += getSpeed() * Gdx.graphics.getDeltaTime();
            if(controls[GameLogic.UP])
                checkPosY += getSpeed() * Gdx.graphics.getDeltaTime();
            if(controls[GameLogic.DOWN])
                checkPosY -= getSpeed() * Gdx.graphics.getDeltaTime();
        }

        //Check only x first
        Rectangle newBounds = new Rectangle(checkPosX, getPos().y, getWidth(), getHeight());

        if(CollisionLogic.entityCollision(newBounds, this) == null
            && CollisionLogic.entityTileCollision(newBounds, this) == null){
            setPos(checkPosX, getPos().y, newBounds);
        }

        //Check y now
        newBounds.set(getPos().x, checkPosY, getWidth(), getHeight());

        if(CollisionLogic.entityCollision(newBounds, this) == null
                && CollisionLogic.entityTileCollision(newBounds, this) == null){
            setPos(getPos().x, checkPosY, newBounds);
        }

        if(controls[GameLogic.SHOOT]) {

            if (getHealth() > 0 && GameLogic.ticCounter > 0) {
                setState(getStates()[Entity.MISSILE]);
                hitScanAttack(getPos().angle, 20);
            } else if (getRemainingStateTics() == -1) {
                GameLogic.readyChangeLevel(GameLogic.currentLevel);
            }
        }

        //If player is IDLE and is hitting a move key, set WALK state
        //Otherwise, if player is NOT MOVING and NOT DOING SOMETHING ELSE, set IDLE state
        if(getCurrentFrame() == 'A' && getRemainingStateTics() == -1
                && (controls[GameLogic.LEFT] || controls[GameLogic.RIGHT] || controls[GameLogic.UP] || controls[GameLogic.DOWN])) {
            setState(getStates()[Entity.WALK]);
        } else if (getCurrentFrame() <= 'D' && getRemainingStateTics() != -1
                && !(controls[GameLogic.LEFT]) && !(controls[GameLogic.RIGHT]) && !(controls[GameLogic.UP]) && !(controls[GameLogic.DOWN])) {
            setState(getStates()[Entity.IDLE]);
        }

        //CollisionLogic.entityCollision(Entity,Entity);
    }

    private LevelData getNewLevelData() {
        int newLevel = GameLogic.currentLevel.getLevelnumber() + 1;
        return GameLogic.levels.get(newLevel);
    }
}
