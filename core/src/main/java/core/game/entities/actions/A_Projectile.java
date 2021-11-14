package core.game.entities.actions;

import com.badlogic.gdx.math.Vector2;
import core.game.entities.Entity;
import core.game.entities.Projectile;
import core.game.logic.GameLogic;

import java.lang.reflect.InvocationTargetException;

public class A_Projectile implements StateAction {

    String projectileClass;

    public A_Projectile(){}
    public A_Projectile(String projectileClass) {
        this.projectileClass = projectileClass;
    }

    @Override
    public void run(Entity caller, Entity target) {

        Vector2 start = caller.getCenter();

        GameLogic.newEntityQueue.add(
                GameLogic.entityTable.get(projectileClass)
                    .spawnProjectile((new Entity.Position(start.x, start.y, caller.getPos().angle)), caller));

    }
}
