package core.game.entities.actions;

import core.game.entities.Entity;

public class A_Fall implements StateAction {

    @Override
    public void run(Entity caller, Entity target) {
        caller.flags ^= Entity.SOLID;
    }
}
