package core.game.entities;

import com.badlogic.gdx.math.Rectangle;
import core.game.logic.CollisionLogic;

public abstract class Projectile extends Entity {

    private int damage;
    private Entity owner;

    //Projectiles have no health or tag.
    public Projectile(Position pos, int speed, int width, int height, Integer[] states, Entity owner, int damage) {
        super(-1, pos, speed, width, height, states, -1);
        this.owner = owner;
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }

    public Entity getOwner() {
        return owner;
    }

    @Override
    public void decrementTics() {
        super.decrementTics();

        float checkPosX = (float) (getPos().x + getSpeed() * Math.cos(Math.toRadians(getPos().angle)));
        float checkPosY = (float) (getPos().y + getSpeed() * Math.sin(Math.toRadians(getPos().angle)));
        Rectangle newBounds = new Rectangle(checkPosX, checkPosY, getWidth(), getHeight());
        Entity hit = CollisionLogic.entityCollision(newBounds, this);

        if(hit == null){
            setPos(checkPosX, checkPosY, newBounds);
        } else {
            //Damage the thing you hit
            hit.takeDamage(owner, damage);

            //Also kill the projectile.
            setState(getStates()[DIE]);
        }
    }
}
