package core.game.entities.actions;

import com.badlogic.gdx.math.Vector2;
import core.game.entities.Entity;
import core.game.entities.Fireball;
import core.game.entities.Projectile;
import core.game.logic.GameLogic;

import java.lang.reflect.InvocationTargetException;

public class A_Projectile implements StateAction {

    Class<? extends Projectile> projectileClass;

    public A_Projectile(Class<? extends Projectile> projectileClass) {
        this.projectileClass = projectileClass;
    }

    @Override
    public void run(Entity caller, Entity target) {

        Vector2 start = caller.getCenter();

        try {
            GameLogic.newEntityQueue.addLast(
                    projectileClass.getDeclaredConstructor(Entity.Position.class, Entity.class)
                            .newInstance(new Entity.Position(start.x, start.y, caller.getPos().angle), caller));
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
