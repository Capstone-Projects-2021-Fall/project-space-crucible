package core.game.entities.actions;

import com.badlogic.gdx.math.Vector2;
import core.game.entities.Entity;

public class A_FaceTarget implements StateAction {

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
    }
}
