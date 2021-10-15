package core.game.entities.actions;

//Interface for Entity AI actions.
//Each state has one action (or null). The action's parameters are passed in the constructor.
//The StateAction interface has one abstract method, run(), the code called when a state that holds the class is called.
//The run methods takes pointers to the calling entity and its target. PlayerPawns will pass a null target always.

import core.game.entities.Entity;

public interface StateAction {
    void run(Entity caller, Entity target);
}
