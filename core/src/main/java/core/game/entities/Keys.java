package core.game.entities;

import com.badlogic.gdx.math.Rectangle;
import core.game.logic.CollisionLogic;

public abstract class Keys extends Entity{
    /*final private static int WIDTH =13;
    final private static int HEIGHT =25;

    final public static int IDLESTATE = 89;
    final public static int HEALTH = -1;
    final public static int SPEED= 0;


    final public static int WALKSTATE = -1;
    final public static int MELEESTATE = -1;
    final public static int MISSILESTATE = -1;
    final public static int PAINSTATE = -1;
    final public static int DEATHSTATE = -1;*/

    public Keys() {
    }

    public Keys(int health, Position pos, int width, int speed, int height, Integer[] states, int flags, int tag){
        super(health, pos, speed, width, height, states, tag, flags);
    }


}

