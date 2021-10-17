package core.game.entities.actions;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import core.game.entities.Entity;
import core.game.logic.CollisionLogic;

import java.util.concurrent.ThreadLocalRandom;

//Enemy chases its target. If target is null (no target), for whatever reason, set state to Idle and abort.
public class A_Chase implements StateAction {

    @Override
    public void run(Entity caller, Entity target) {

        if (target == null || target.getHealth() <= 0)
        {
            caller.setState(caller.getStates()[Entity.IDLE]);
            return;
        }

        Vector2 distance = new Vector2();
        distance.x = target.getPos().x - caller.getPos().x;
        distance.y = target.getPos().y - caller.getPos().y;

        if (distance.len() <= 64f) {
            caller.setState(caller.getStates()[Entity.MELEE]);
            return;
        }

        Vector2 start = caller.getCenter();

        if (ThreadLocalRandom.current().nextInt()  % 10 == 0
            && (CollisionLogic.checkFOVForEntity(start.x, start.y, caller.getPos().angle, caller, target))
            && caller.getStates()[Entity.MISSILE] != -1) {
            caller.setState(caller.getStates()[Entity.MISSILE]);
        }

        caller.getPos().angle = distance.angleDeg();
        distance.nor();

        float checkPosX = caller.getPos().x + caller.getSpeed() * distance.x;
        float checkPosY = caller.getPos().y + caller.getSpeed() * distance.y;

        //Check only x first
        Rectangle newBounds = new Rectangle(checkPosX, caller.getPos().y, caller.getWidth(), caller.getHeight());

        if(CollisionLogic.entityCollision(newBounds, caller) == null
                && CollisionLogic.entityTileCollision(newBounds,caller) == null){
            caller.setPos(checkPosX, caller.getPos().y, newBounds);
        }

        //Check y now
        newBounds.set(caller.getPos().x, checkPosY, caller.getWidth(), caller.getHeight());

        if(CollisionLogic.entityCollision(newBounds, caller) == null
                && CollisionLogic.entityTileCollision(newBounds,caller) == null){
            caller.setPos(caller.getPos().x, checkPosY, newBounds);
        }
    }
}
