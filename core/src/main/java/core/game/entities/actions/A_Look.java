package core.game.entities.actions;

import com.badlogic.gdx.math.Vector2;
import core.game.entities.BaseMonster;
import core.game.entities.Entity;
import core.game.entities.PlayerPawn;
import core.game.logic.CollisionLogic;
import core.game.logic.GameLogic;

public class A_Look implements StateAction {
    @Override
    public void run(Entity caller, Entity target) {

        if (!(caller instanceof BaseMonster) || GameLogic.currentLevel == null) {return;}

        float refAngle = caller.getPos().angle;
        Vector2 startPoint = caller.getCenter();

        Entity seen = CollisionLogic.checkFOVForClass(startPoint.x, startPoint.y, refAngle, caller, PlayerPawn.class);

        if (seen != null) {
            ((BaseMonster) caller).setTarget(seen);
            caller.setState(caller.getStates()[Entity.WALK]);
        }
    }
}
