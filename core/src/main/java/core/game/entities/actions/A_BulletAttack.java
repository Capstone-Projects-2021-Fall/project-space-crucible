package core.game.entities.actions;

import core.game.entities.Entity;

import java.util.concurrent.ThreadLocalRandom;

public class A_BulletAttack implements StateAction{

    private int damage;
    private float angle;

    public A_BulletAttack(){}

    public A_BulletAttack(int damage, float angle) {
        this.damage = damage;
        this.angle = angle;
    }

    @Override
    public void run(Entity caller, Entity target) {

        float shootAngle = ThreadLocalRandom.current().nextFloat();
        if (ThreadLocalRandom.current().nextBoolean()) {shootAngle = -shootAngle;}

        caller.hitScanAttack(caller.getPos().angle + shootAngle, damage);
    }
}
