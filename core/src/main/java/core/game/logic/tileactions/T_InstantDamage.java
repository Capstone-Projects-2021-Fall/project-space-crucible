package core.game.logic.tileactions;

import core.game.entities.Entity;
import core.game.entities.PlayerPawn;
import core.game.logic.GameLogic;

public class T_InstantDamage implements TileAction {

    @Override
    public void run(Entity activator, int arg1, int arg2, int tag) {
        if ((activator instanceof PlayerPawn || arg2 == 1)) {
            activator.takeDamage(null, arg1);
        }
    }
}
