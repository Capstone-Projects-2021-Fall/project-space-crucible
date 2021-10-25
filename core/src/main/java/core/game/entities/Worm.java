package core.game.entities;

public class Worm extends BaseMonster {

    final private static int HEALTH = 150;
    final private static int SPEED = 8;
    final private static int WIDTH = 30;
    final private static int HEIGHT = 56;

    final public static int IDLESTATE = 16;
    final public static int WALKSTATE = 18;
    final public static int MELEESTATE = 26;
    final public static int MISSILESTATE = -1;
    final public static int PAINSTATE = 29;
    final public static int DEATHSTATE = 31;

    final private static String seeSound = "worm/see";
    final private static String painSound = "worm/pain";
    final private static String dieSound = "worm/die";
    final private static String activeSound = "worm/active";

    public Worm(){}

    public Worm(Position pos, int tag) {
        super(HEALTH, pos, SPEED, WIDTH, HEIGHT,
                new Integer[]{IDLESTATE, WALKSTATE, MELEESTATE, MISSILESTATE, PAINSTATE, DEATHSTATE}, tag, SOLID,
                new String[]{seeSound, painSound, dieSound, activeSound});
    }
}
