package core.game.entities;

import com.badlogic.gdx.math.Rectangle;
import core.game.logic.CollisionLogic;
import core.game.logic.GameLogic;

public class Projectile extends Entity {

    private int damage;
    private Entity owner;

    public Projectile(){}

    //Projectiles have no health or tag.
    public Projectile(String name, Position pos, int speed, int width, int height, Integer[] states, Entity owner, int damage, long flags) {
        super(name, -1, pos, speed, width, height, states, -1, flags, owner.currentLayer);
        this.owner = owner;
        this.damage = damage;

        //Projectiles get thrown to the layer of their target
        if (owner instanceof BaseMonster) {
            try {
                this.bridgeLayer = GameLogic.entityList.get(((BaseMonster) owner).getTarget()).currentLayer;
                if (this.bridgeLayer == this.currentLayer) {
                    this.bridgeLayer = -1;
                }
            } catch(Exception e) {this.bridgeLayer = -1;}
            System.out.println("Proj bridge layer: " + this.bridgeLayer);
        }
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

        if (currentStateIndex >= getStates()[DIE]) {return;}

        float checkPosX = (float) (getPos().x + getSpeed() * Math.cos(Math.toRadians(getPos().angle)));
        float checkPosY = (float) (getPos().y + getSpeed() * Math.sin(Math.toRadians(getPos().angle)));
        Rectangle newBounds = new Rectangle(checkPosX, checkPosY, getWidth(), getHeight());

        if (CollisionLogic.entityTileCollision(newBounds, this) != null) {
            setState(getStates()[DIE]);
            return;
        }

        Entity hit = CollisionLogic.entityCollision(newBounds, this);

        if(hit == null){
            setPos(newBounds);
        } else {
            //Damage the thing you hit
            hit.takeDamage(owner, damage);

            //Also kill the projectile.
            setState(getStates()[DIE]);
        }
    }
}
