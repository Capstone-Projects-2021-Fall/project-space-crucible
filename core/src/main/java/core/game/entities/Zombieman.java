package core.game.entities;

public class Zombieman extends BaseMonster {

    final private static int HEALTH = 30;
    final private static int SPEED = 3;
    final private static int WIDTH = 32;
    final private static int HEIGHT = 56;

    final public static int IDLESTATE = 69;
    final public static int WALKSTATE = 71;
    final public static int MELEESTATE = 79;
    final public static int MISSILESTATE = 79;
    final public static int PAINSTATE = 82;
    final public static int DEATHSTATE = 84;

    final private static String seeSound = "zombieman/see";
    final private static String painSound = "zombieman/pain";
    final private static String dieSound = "zombieman/die";
    final private static String activeSound = "zombieman/active";

    public Zombieman(){}

    public Zombieman(Position pos, int tag) {
        super(HEALTH, pos, SPEED, WIDTH, HEIGHT,
                new Integer[]{IDLESTATE, WALKSTATE, MELEESTATE, MISSILESTATE, PAINSTATE, DEATHSTATE}, tag, SOLID,
                new String[]{seeSound, painSound, dieSound, activeSound});
    }

}
