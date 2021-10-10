package core.game.logic;

import core.wad.funcs.GameSprite;

import java.util.ArrayList;

public class EntityState {

    //Static ArrayList of all game states
    final public static ArrayList<EntityState> stateList = new ArrayList<>();

    private GameSprite sprite;
    private Character frame;
    private Integer duration;
    private Integer nextState;

    public EntityState(String spriteName, Character frame, Integer duration, Integer nextState) {
        sprite = GameSprite.spriteMap.get(spriteName);
        this.frame = frame;
        this.duration = duration;
        this.nextState = nextState;
    }

    public GameSprite getSprite() {
        return sprite;
    }

    public Character getFrame() {
        return frame;
    }

    public Integer getDuration() {
        return duration;
    }

    public Integer getNextState() {
        return nextState;
    }
}
