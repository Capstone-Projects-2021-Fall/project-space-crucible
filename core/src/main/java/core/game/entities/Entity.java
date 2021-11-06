package core.game.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import core.game.logic.CollisionLogic;
import core.game.logic.EntityState;
import core.game.logic.GameLogic;

public class Entity {

    final public static int IDLE = 0;
    final public static int WALK = 1;
    final public static int MELEE = 2;
    final public static int MISSILE = 3;
    final public static int PAIN = 4;
    final public static int DIE = 5;

    final public static long TIC = 18;

    final public static long SOLID = 1;

    final public static float EAST = 0f;
    final public static float NORTH = 90f;
    final public static float WEST = 180f;
    final public static float SOUTH = 270f;

    public static class Position {
        public float x;
        public float y;
        public float angle;

        public Position(){}

        public Position(float x, float y, float angle) {
            this.x = x;
            this.y = y;
            this.angle = angle;
        }
    }

    private int health;
    private Position pos;
    private int speed;
    private int width;
    private int height;
    protected EntityState currentState;
    protected int currentStateIndex;
    protected int remainingStateTics;
    private Integer[] states;
    protected int tag;
    private Rectangle bound;
    public long flags;

    public Entity(){}

    //Like sprites, each state is only stored once in a global ArrayList, which is memory-efficient.
    public Entity (String name, int health, Position pos, int speed, int width, int height, Integer[] states, int tag, long flags) {
        this.health = health;
        this.pos = pos;
        this.speed = speed;
        this.width = width;
        this.height = height;
        this.states = states;
        this.tag = tag;
        this.flags = flags;
        bound = new Rectangle(pos.x, pos.y, width, height);
        setState(this.states[IDLE]);
    }

    public int getHealth() {return health;}

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getRemainingStateTics() {return remainingStateTics;}

    public int getSpeed() {return speed;}

    public int getTag() {return tag;}

    public String getCurrentSprite() {return currentState.getSprite();}

    public Character getCurrentFrame() {return currentState.getFrame();}

    public Position getPos() {
        return pos;
    }

    public void setPos(Rectangle bounds) {
        getPos().x = bounds.x;
        getPos().y = bounds.y;
        getBounds().set(bounds);
    }

    public Integer[] getStates() {
        return states;
    }

    public Rectangle getBounds() {return bound;}

    public Vector2 getCenter() {
        return new Vector2(pos.x + (width/2f), pos.y + (height/2f));
    }

    public void setState(Integer state) {

        //nextState == -1 means remove after state call.
        if (state == -1) {
            GameLogic.deleteEntityQueue.addLast(this);
            return;
        }

        currentState = GameLogic.stateList.get(state);
        currentStateIndex = state;
        remainingStateTics = currentState.getDuration();

        if (currentState.getAction() != null) {currentState.getAction().run(this, null);}
    }

    //Damage this Entity. Set painstate if non-lethal, deathstate if lethal.
    public void takeDamage(Entity cause, int damage) {

        if (currentStateIndex >= getStates()[DIE]) {return;}

        health -= damage;

        if (health <= 0) {
            setState(getStates()[DIE]);
        } else {
            setState(getStates()[PAIN]);
        }
    }

    public void decrementTics() {

        if (remainingStateTics > 0) {
            remainingStateTics--;
        }
        if (remainingStateTics == 0) {

            if (currentState.getNextState() != -1) {
                setState(currentState.getNextState());
            } else {
                GameLogic.deleteEntityQueue.addLast(this);
            }
        }
    }

    public String getSpriteAngle(Character frame, float angle) {

        int spriteAngle = -1;

        if (angle < 22.5 || angle >= 337.5) {
            spriteAngle = 7;
        } else if (angle < 337.5 && angle >= 292.5) {
            spriteAngle = 8;
        } else if (angle < 292.5 && angle >= 247.5) {
            spriteAngle = 1;
        } else if (angle < 247.5 && angle >= 202.5) {
            spriteAngle = 2;
        } else if (angle < 202.5 && angle >= 157.5) {
            spriteAngle = 3;
        } else if (angle < 157.5 && angle >= 112.5) {
            spriteAngle = 4;
        } else if (angle < 112.5 && angle >= 67.5) {
            spriteAngle = 5;
        } else if (angle < 67.5 && angle >= 22.5) {
            spriteAngle = 6;
        }

        if (spriteAngle == -1) {
            System.out.println("Angle " + angle + " is impossible.");
            return null;
        }

        return Integer.toString(spriteAngle);
    }
    public boolean getFlag(long flag) {return (flags & flag) == 1;}

    public void hitScanAttack(float angle, int damage) {

        Vector2 startPoint = getCenter();
        Entity hit = CollisionLogic.hitscanLine(startPoint.x, startPoint.y, angle, this, true);

        if (hit != null) {
            hit.takeDamage(this, damage);
        }
    }
}
