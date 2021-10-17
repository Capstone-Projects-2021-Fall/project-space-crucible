package core.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import core.game.entities.actions.A_Chase;
import core.game.logic.CollisionLogic;
import core.game.logic.GameLogic;
import core.level.info.LevelData;

public class PlayerPawn extends Entity {

    final private static int HEALTH = 100;
    final private static int SPEED = 120;
    final private static int WIDTH = 32;
    final private static int HEIGHT = 56;

    final public static int IDLESTATE = 0;
    final public static int WALKSTATE = 1;
    final public static int MELEESTATE = 5;
    final public static int MISSILESTATE = 6;
    final public static int PAINSTATE = 7;
    final public static int DEATHSTATE = 9;

    public PlayerPawn(Position pos, int tag) {
        super(HEALTH, pos, SPEED, WIDTH, HEIGHT,
                new Integer[]{IDLESTATE, WALKSTATE, MELEESTATE, MISSILESTATE, PAINSTATE, DEATHSTATE}, tag,
                SOLID);
    }


    public void movementUpdate(){
        //Input handling with polling method
        //This handles all the keys pressed with the keyboard.

        //Debug keys- play, pain And death animations
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            setState(getStates()[Entity.PAIN]);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            setState(getStates()[Entity.DIE]);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
            GameLogic.changeLevel(getNewLevelData());
        }

        float checkPosX = getPos().x;
        float checkPosY = getPos().y;

        //Is a movement key is CURRENTLY pressed, move player.
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))
            checkPosX -= getSpeed() * Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))
            checkPosX += getSpeed() * Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W))
            checkPosY += getSpeed() * Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S))
            checkPosY -= getSpeed() * Gdx.graphics.getDeltaTime();

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

        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            setState(getStates()[Entity.MISSILE]);
            hitScanAttack(getPos().angle, 20);
        }

        //If player is IDLE and is hitting a move key, set WALK state
        //Otherwise, if player is NOT MOVING and NOT DOING SOMETHING ELSE, set IDLE state
        if(getCurrentFrame() == 'A' && getRemainingStateTics() == -1
                && (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)
                || Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)
                || Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)
                || Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S))) {
            setState(getStates()[Entity.WALK]);
        } else if (getCurrentFrame() <= 'D' && getRemainingStateTics() != -1
                && !(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))
                && !(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))
                && !(Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W))
                && !(Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S))) {
            setState(getStates()[Entity.IDLE]);
        }

        //CollisionLogic.entityCollision(Entity,Entity);
    }

    private LevelData getNewLevelData() {
        int newLevel = GameLogic.currentLevel.getLevelnumber() + 1;
        return GameLogic.levels.get(newLevel);
    }
}
