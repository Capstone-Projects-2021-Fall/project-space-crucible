package core.game.entities.actions;

import com.badlogic.gdx.math.Vector2;
import core.game.entities.BaseMonster;
import core.game.entities.Entity;
import core.game.logic.CollisionLogic;
import core.game.logic.GameLogic;

public class A_Look implements StateAction {
    @Override
    public void run(Entity caller, Entity target) {

        if (!(caller instanceof BaseMonster) || GameLogic.currentLevel == null) {return;}

        float refAngle = caller.getPos().angle;
        Vector2 startPoint = caller.getCenter();

        for (float angle = refAngle-45; angle != refAngle + 45; angle += 9) {
            Entity e = CollisionLogic.hitscanLine(startPoint.x, startPoint.y, angle, caller);

            if (e != null) {
                ((BaseMonster) caller).setTarget(e);
                caller.setState(caller.getStates()[Entity.WALK]);
                return;
            }
        }
    }
}
