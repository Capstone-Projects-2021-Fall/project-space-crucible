package core.game.entities.actions;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import core.game.entities.Entity;
import core.game.logic.CollisionLogic;

//Enemy chases its target. If target is null (no target), for whatever reason, set state to Idle and abort.
public class A_Chase implements StateAction {

    @Override
    public void run(Entity caller, Entity target) {

        if (target == null)
        {
            caller.setState(caller.getStates()[Entity.IDLE]);
            return;
        }

        Vector2 distance = new Vector2();
        distance.x = target.getPos().x - caller.getPos().x;
        distance.y = target.getPos().y - caller.getPos().y;
        caller.getPos().angle = distance.angleDeg();
        distance.nor();

        System.out.println(distance.x + " x  " + distance.y + " y");

        float checkPosX = caller.getPos().x + caller.getSpeed() * distance.x;
        float checkPosY = caller.getPos().y + caller.getSpeed() * distance.y;
        Rectangle newBounds = new Rectangle(checkPosX, checkPosY, caller.getWidth(), caller.getHeight());

        if(CollisionLogic.entityCollision(newBounds, caller) == null){
           caller.getPos().x = checkPosX;
           caller.getPos().y = checkPosY;
           caller.getBounds().set(newBounds);
        }
    }
}
