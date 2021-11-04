package core.game.entities;

public class BulletPuff extends Entity {

    final public static int HEALTH = -1;
    final public static int SPEED = 0;
    final public static int WIDTH = 0;
    final public static int HEIGHT = 0;

    final public static int IDLESTATE = 45;
    final public static int WALKSTATE = -1;
    final public static int MELEESTATE = -1;
    final public static int MISSILESTATE = -1;
    final public static int PAINSTATE = -1;
    final public static int DEATHSTATE = -1;

    public BulletPuff(){}

    public BulletPuff(Position pos) {
        super("BulletPuff", HEALTH, pos, SPEED, WIDTH,
                HEIGHT, new Integer[]{IDLESTATE, WALKSTATE, MELEESTATE, MISSILESTATE, PAINSTATE, DEATHSTATE}, 0, 0);
    }
}
