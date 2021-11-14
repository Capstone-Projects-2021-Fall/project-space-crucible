package core.game.entities.actions;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import core.game.entities.Entity;
import core.game.logic.CollisionLogic;

import java.util.concurrent.ThreadLocalRandom;

//Enemy chases its target. If target is null (no target), for whatever reason, set state to Idle and abort.
public class A_Chase implements StateAction {

    @Override
    public void run(Entity caller, Entity target) {

        //If there's no target, go back to idle.
        if (target == null || target.getHealth() <= 0)
        {
            caller.setState(caller.getStates()[Entity.IDLE]);
            return;
        }

        //If you can see the target, and you have a ranged attack, decide at random to attack.
        Vector2 start = caller.getCenter();
        if (ThreadLocalRandom.current().nextInt()  % 10 == 0
            && (CollisionLogic.checkFOVForEntity(start.x, start.y, caller.getPos().angle, caller, target))
            && caller.getStates()[Entity.MISSILE] != -1) {
            caller.setState(caller.getStates()[Entity.MISSILE]);
        }

        caller.pursueTarget(target);
    }
}
