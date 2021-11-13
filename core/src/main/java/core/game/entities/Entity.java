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
    public int currentLayer = 0;
    public int bridgeLayer = -1;
    private String name;

    public Entity(){}

    //Like sprites, each state is only stored once in a global ArrayList, which is memory-efficient.
    public Entity (String name, int health, Position pos, int speed, int width, int height, Integer[] states, int tag, long flags, int layer) {
        this.health = health;
        this.pos = pos;
        this.speed = speed;
        this.width = width;
        this.height = height;
        this.states = states;
        this.tag = tag;
        this.flags = flags;
        this.name = name;
        this.currentLayer = layer;
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

    public void setSpeed(int speed) {this.speed = speed;}

    public int getTag() {return tag;}

    public String getName() {return name;}

    public String getCurrentSprite() {return currentState.getSprite();}

    public Character getCurrentFrame() {return currentState.getFrame();}

    public int getCurrentStateIndex() {return currentStateIndex;}

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
            GameLogic.deleteEntityQueue.add(this);
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
                GameLogic.deleteEntityQueue.add(this);
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

    public void pursueTarget(Entity target) {

        final float startX = getPos().x;
        final float startY = getPos().y;
        final int width = getWidth();
        final int height = getHeight();
        final int speed = getSpeed();

        //Try to get to target
        Vector2 distance = new Vector2();
        distance.x = target.getPos().x - startX;
        distance.y = target.getPos().y - startY;
        float angleToTarget = distance.angleDeg();

        if (distance.len() <= 64f && currentLayer == target.currentLayer) {
            setState(getStates()[Entity.MELEE]);
            return;
        }
        distance.nor();

        float newX = startX + (speed * distance.x);
        float newY = startY + (speed * distance.y);

        Rectangle newBounds = new Rectangle(newX, newY, width, height);

        //No problems. Take direct route.
        if (CollisionLogic.simpleCollisionCheck(newBounds, this)) {
            getPos().angle = angleToTarget;
            setPos(newBounds);
            return;
        }

        //There must've been a collision.
        int directionX = distance.x > 0? 1 : -1;
        int directionY = distance.y > 0? 1 : -1;

        //First, try to maintain current angle as long as possible.
        newBounds.set(startX + (float)(speed * Math.cos(Math.toRadians(getPos().angle))),
                startY + (float)(speed * Math.sin(Math.toRadians(getPos().angle))), width, height);
        if (CollisionLogic.simpleCollisionCheck(newBounds, this)) {
            setPos(newBounds);
            return;
        }

        //Can you go in the X direction towards the target?
        newBounds.set(startX + (speed * directionX), startY, width, height);
        if (CollisionLogic.simpleCollisionCheck(newBounds, this)) {
            getPos().angle = directionX > 0 ? Entity.EAST : Entity.WEST;
            setPos(newBounds);
            return;
        }

        //Can you go in the Y direction towards the target?
        newBounds.set(startX, startY + (speed * directionY), width, height);
        if (CollisionLogic.simpleCollisionCheck(newBounds, this)) {
            getPos().angle = directionY > 0 ? Entity.NORTH : Entity.SOUTH;
            setPos(newBounds);
            return;
        }

        //If the player x is closer, try opposite y direction first, else x. Try other opposite as last resort.
        System.out.println("Distance x: " + distance.x + "\nDistance y: " + distance.y);
        if (Math.abs(distance.x) > Math.abs(distance.y)) {
            newBounds.set(startX, startY - (speed * directionY), width, height);
            if (CollisionLogic.simpleCollisionCheck(newBounds, this)) {
                getPos().angle = directionY > 0 ? Entity.SOUTH : Entity.NORTH;
                setPos(newBounds);
                return;
            }

            newBounds.set(startX - (speed * directionX), startY, width, height);
            if (CollisionLogic.simpleCollisionCheck(newBounds, this)) {
                getPos().angle = directionX > 0 ? Entity.WEST : Entity.EAST;
                setPos(newBounds);

            }
        } else {
            newBounds.set(startX - (speed * directionX), startY, width, height);
            if (CollisionLogic.simpleCollisionCheck(newBounds, this)) {
                getPos().angle = directionX > 0 ? Entity.WEST : Entity.EAST;
                setPos(newBounds);
                return;
            }

            newBounds.set(startX, startY - (speed * directionY), width, height);
            if (CollisionLogic.simpleCollisionCheck(newBounds, this)) {
                getPos().angle = directionY > 0 ? Entity.SOUTH : Entity.NORTH;
                setPos(newBounds);
            }
        }

        //If none of that worked, you're probably stuck for some reason. Oops!
    }
}
