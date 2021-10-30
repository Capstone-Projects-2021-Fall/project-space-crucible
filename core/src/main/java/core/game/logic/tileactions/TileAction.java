package core.game.logic.tileactions;

import core.game.entities.Entity;

public interface TileAction {

    public void run(Entity activator, int arg1, int arg2);
}
