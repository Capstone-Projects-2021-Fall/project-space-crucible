package core.game.entities.actions;

import com.badlogic.gdx.math.Vector2;
import core.game.entities.Entity;

public class A_MeleeAttack implements StateAction {

    int damage;

    public A_MeleeAttack(){}
    public A_MeleeAttack(int damage) {
        this.damage = damage;
    }

    @Override
    public void run(Entity caller, Entity target) {
        try {
            Vector2 distance = new Vector2();
            distance.x = target.getPos().x - caller.getPos().x;
            distance.y = target.getPos().y - caller.getPos().y;

            if (distance.len() <= 64f && target.currentLayer == caller.currentLayer) {
                target.takeDamage(caller, damage);
            }
        } catch (NullPointerException ignored) {}
    }
}
