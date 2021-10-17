package core.game.logic;

import core.game.entities.actions.StateAction;
import core.wad.funcs.GameSprite;

public class EntityState {
    //Static ArrayList of all game states

    private GameSprite sprite;
    private Character frame;
    private Integer duration;
    private Integer nextState;
    private StateAction action;
    private Integer index;

    public EntityState(Integer index, String spriteName, Character frame, Integer duration, Integer nextState, StateAction action) {
        this.index = index;
        sprite = GameLogic.spriteMap.get(spriteName);
        this.frame = frame;
        this.duration = duration;
        this.nextState = nextState;
        this.action = action;
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

    public StateAction getAction() {return action;}

    public Integer getIndex() {return index;}
}
