package core.game.logic.tileactions;

import core.game.entities.Entity;
import core.game.logic.GameLogic;
import core.game.logic.LevelScript;

public class T_ExecuteScript implements TileAction{

    @Override
    public void run(Entity activator, int arg1, int arg2) {
        if (GameLogic.scripts.containsKey(arg1)) {
            GameLogic.newScriptQueue.add(new LevelScript(activator, GameLogic.scripts.get(arg1)));
        } else {
            System.out.println("Unknown script number " + arg1);
        }
    }
}
