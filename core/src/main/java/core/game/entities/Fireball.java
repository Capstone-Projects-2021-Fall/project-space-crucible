package core.game.entities;

public class Fireball extends Projectile {

    final private static int SPEED = 20;
    final private static int WIDTH = 8;
    final private static int HEIGHT = 8;
    final private static int DAMAGE = 20;

    final public static int IDLESTATE = 37;
    final public static int WALKSTATE = -1;
    final public static int MELEESTATE = -1;
    final public static int MISSILESTATE = -1;
    final public static int PAINSTATE = -1;
    final public static int DEATHSTATE = 39;

    public Fireball(Position pos, Entity owner) {
        super(pos, SPEED, WIDTH, HEIGHT,
                new Integer[]{IDLESTATE, WALKSTATE, MELEESTATE, MISSILESTATE, PAINSTATE, DEATHSTATE}, owner, DAMAGE,
                0);
    }
}
