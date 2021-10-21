package core.game.entities.actions;

import core.game.entities.Entity;
import core.wad.funcs.SoundFuncs;

import java.util.concurrent.ThreadLocalRandom;

public class A_BulletAttack implements StateAction {

    private int damage;
    private float angle;
    private String sound;

    public A_BulletAttack(){}

    public A_BulletAttack(int damage, float angle, String sound) {
        this.damage = damage;
        this.angle = angle;
        this.sound = sound;
    }

    @Override
    public void run(Entity caller, Entity target) {

        float shootAngle = ThreadLocalRandom.current().nextFloat() * angle;
        if (ThreadLocalRandom.current().nextBoolean()) {shootAngle = -shootAngle;}

        System.out.println("Attack angle: " + shootAngle);

        SoundFuncs.playSound(sound);
        caller.hitScanAttack(caller.getPos().angle + shootAngle, damage);
    }
}
