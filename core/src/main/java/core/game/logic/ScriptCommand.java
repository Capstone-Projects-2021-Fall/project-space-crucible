package core.game.logic;

import core.game.entities.Entity;
import core.game.logic.tileactions.TileAction;

public class ScriptCommand {
    public int delay;
    public TileAction action;
    public int arg1, arg2, tag;

    public ScriptCommand(){delay = 0;}

    public ScriptCommand(TileAction action, int arg1, int arg2, int tag, int delay) {
        this.action = action;
        this.delay = delay;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.tag = tag;
    }

    public void run(Entity activator) {
        if (action != null) {
            System.out.println(action.getClass().getName());
            action.run(activator, arg1, arg2, tag);
        }
    }
}
