package core.game.logic;

public class Worm extends BaseMonster {

    final private static int HEALTH = 150;
    final private static int SPEED = 100;
    final private static int WIDTH = 30;
    final private static int HEIGHT = 56;

    final private static int IDLESTATE = 16;
    final private static int WALKSTATE = 18;
    final private static int MELEESTATE = 26;
    final private static int MISSILESTATE = -1;
    final private static int PAINSTATE = 29;
    final private static int DEATHSTATE = 31;

    public Worm(Position pos, int tag) {
        super(HEALTH, pos, SPEED, WIDTH, HEIGHT,
                new Integer[]{IDLESTATE, WALKSTATE, MELEESTATE, MISSILESTATE, PAINSTATE, DEATHSTATE}, tag);
    }
}
