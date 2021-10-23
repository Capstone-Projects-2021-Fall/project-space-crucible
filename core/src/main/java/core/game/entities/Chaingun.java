package core.game.entities;

public class Chaingun extends Entity {
    final private static int WIDTH = 13;
    final private static int HEIGHT = 25;

    final public static int IDLESTATE = 91;
    final public static int HEALTH = -1;
    final public static int SPEED = 0;


    final public static int WALKSTATE = -1;
    final public static int MELEESTATE = -1;
    final public static int MISSILESTATE = -1;
    final public static int PAINSTATE = -1;
    final public static int DEATHSTATE = -1;

    public Chaingun() {
    }

    public Chaingun(Position pos, int tag) {
        super(HEALTH, pos, WIDTH, SPEED,
                HEIGHT, new Integer[]{IDLESTATE,WALKSTATE, MELEESTATE, MISSILESTATE, PAINSTATE, DEATHSTATE}, tag,0);


    }
}
