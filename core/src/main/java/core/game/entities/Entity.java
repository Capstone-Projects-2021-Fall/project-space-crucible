package core.game.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import core.game.logic.CollisionLogic;
import core.game.logic.EntityState;
import core.game.logic.GameLogic;
import core.wad.funcs.GameSprite;
import net.mtrop.doom.WadFile;

import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public abstract class Entity {

    final public static int IDLE = 0;
    final public static int WALK = 1;
    final public static int MELEE = 2;
    final public static int MISSILE = 3;
    final public static int PAIN = 4;
    final public static int DIE = 5;

    final public static long TIC = 18;

    final public static long SOLID = 1;

    public static class Position {
        public float x;
        public float y;
        public float angle;

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
    protected int remainingStateTics;
    private Integer[] states;
    private int tag;
    private Rectangle bound;
    public long flags;

    //Like sprites, each state is only stored once in a global ArrayList, which is memory-efficient.
    public Entity (int health, Position pos, int speed, int width, int height, Integer[] states, int tag, long flags) {
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

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getRemainingStateTics() {return remainingStateTics;}

    public int getSpeed() {return speed;}

    public int getTag() {return tag;}

    public Sprite getCurrentSprite() {return currentState.getSprite().getFrame(currentState.getFrame(), pos.angle);}

    public Character getCurrentFrame() {return currentState.getFrame();}

    public Position getPos() {
        return pos;
    }

    public void setPos(float x, float y, Rectangle bounds) {
        getPos().x = x;
        getPos().y = y;
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
        remainingStateTics = currentState.getDuration();

        if (currentState.getAction() != null) {currentState.getAction().run(this, null);}
    }

    //Damage this Entity. Set painstate if non-lethal, deathstate if lethal.
    public void takeDamage(Entity cause, int damage) {

        if (currentState.getIndex() > getStates()[DIE]) {return;}

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

    public boolean getFlag(long flag) {return (flags & flag) == 1;}

    public void hitScanAttack(float angle, int damage) {

        Vector2 startPoint = getCenter();
        Entity hit = CollisionLogic.hitscanLine(startPoint.x, startPoint.y, angle, this, true);

        if (hit != null) {
            hit.takeDamage(this, damage);
        }
    }
}
