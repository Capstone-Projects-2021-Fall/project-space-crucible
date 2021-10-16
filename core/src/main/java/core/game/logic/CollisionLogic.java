package core.game.logic;

import com.badlogic.gdx.math.Rectangle;
import core.game.entities.Entity;
import core.game.entities.Projectile;
import core.level.info.LevelTile;

import java.util.ArrayList;

public class CollisionLogic {

    public static Entity entityCollision(Rectangle bounds, Entity entity){
        Entity collidedEntity = null;
        for(Entity entity2 : GameLogic.entityList){

            //No collision with self OR owner (if projectile)
            if(entity==entity2
                || entity2 instanceof Projectile && ((Projectile) entity2).getOwner() == entity){
                continue;
            }
            if(bounds.overlaps(entity2.getBounds())){
                collidedEntity = entity2;
                System.out.println("Hey there.\n");
                break;
            }
        }
        return collidedEntity;
    }


}
