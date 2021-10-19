package core.game.entities;

public class Serpentipede extends BaseMonster {

    final private static int HEALTH = 60;
    final private static int SPEED = 3;
    final private static int WIDTH = 32;
    final private static int HEIGHT = 56;

    final public static int IDLESTATE = 49;
    final public static int WALKSTATE = 51;
    final public static int MELEESTATE = 59;
    final public static int MISSILESTATE = 59;
    final public static int PAINSTATE = 62;
    final public static int DEATHSTATE = 64;

    public Serpentipede(){}

    public Serpentipede(Position pos, int tag) {
        super(HEALTH, pos, SPEED, WIDTH, HEIGHT,
                new Integer[]{IDLESTATE, WALKSTATE, MELEESTATE, MISSILESTATE, PAINSTATE, DEATHSTATE}, tag, SOLID,
                    new String[]{"DSBGSIT1", "DSPOPAIN", "DSBGDTH1", "DSBGACT"});
    }
}
