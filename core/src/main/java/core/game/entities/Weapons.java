package core.game.entities;

public class Weapons extends Entity {
        final private static int WIDTH = 13;
        final private static int HEIGHT = 25;

        final public static int IDLESTATE = 0;
        final public static int HEALTH = -1;
        final public static int SPEED = 0;


        final public static int WALKSTATE = -1;
        final public static int MELEESTATE = -1;
        final public static int MISSILESTATE = -1;
        final public static int PAINSTATE = -1;
        final public static int DEATHSTATE = -1;

        public Weapons() {
        }

        public Weapons(Position pos, int tag, long flag) {
                super(HEALTH, pos, WIDTH, SPEED,
                        HEIGHT, new Integer[]{WALKSTATE, MELEESTATE, MISSILESTATE, PAINSTATE, DEATHSTATE}, tag, flag = 0);


        }
}
