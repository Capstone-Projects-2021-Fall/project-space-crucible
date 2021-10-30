package core.game.logic.tileactions;

import core.game.entities.Entity;
import core.game.entities.PlayerPawn;
import core.game.logic.GameLogic;

public class T_SlimeDamage implements TileAction {

    //Damage activator every 50 tics standing on the slime.
    @Override
    public void run(Entity activator, int arg1, int arg2) {
        if (GameLogic.ticCounter % 50 == 0
            && (activator instanceof PlayerPawn || arg2 == 1)) {
            activator.takeDamage(null, arg1);
        }
    }
}
