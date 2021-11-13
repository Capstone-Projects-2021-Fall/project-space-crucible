package core.game.logic;

import core.game.entities.Entity;
import core.game.logic.tileactions.TileAction;

public class ScriptCommand {
    public int delay;
    public TileAction action;
    int arg1, arg2;

    private ScriptCommand(TileAction action, int arg1, int arg2, int delay) {
        this.action = action;
        this.delay = delay;
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    public void run(Entity activator) {
        action.run(activator, arg1, arg2);
    }
}
