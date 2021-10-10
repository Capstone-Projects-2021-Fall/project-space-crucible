package core.game.logic;

import com.badlogic.gdx.graphics.g2d.Sprite;
import core.wad.funcs.GameSprite;
import net.mtrop.doom.WadFile;

import java.util.Timer;
import java.util.TimerTask;

public abstract class Entity {

    final public static int IDLE = 0;
    final public static int WALK = 1;
    final public static int MELEE = 2;
    final public static int MISSILE = 3;
    final public static int PAIN = 4;
    final public static int DIE = 5;

    final static double TIC = 1.0 / 35.0;

    public static class Position {
        public int x;
        public int y;
        public float angle;

        public Position(int x, int y, float angle) {
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
    private EntityState currentState;
    private Integer[] states;

    //Like sprites, each state is only stored once in a global ArrayList, which is memory-efficient.
    public Entity (int health, Position pos, int speed, int width, int height, Integer[] states) {
        this.health = health;
        this.pos = pos;
        this.speed = speed;
        this.width = width;
        this.height = height;
        this.states = states;
        setState(this.states[IDLE]);
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Sprite getCurrentSprite() {return currentState.getSprite().getFrame(currentState.getFrame(), pos.angle);}

    public Position getPos() {
        return pos;
    }

    public Integer[] getStates() {
        return states;
    }

    public void setState(Integer state) {
        currentState = EntityState.stateList.get(state);

        System.out.println("State " + state + "\nDuration: " + currentState.getDuration());

        if (currentState.getDuration() != -1) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    setState(currentState.getNextState());
                }
            }, (long)((currentState.getDuration() * TIC) * 1000));
        }
    }
}
