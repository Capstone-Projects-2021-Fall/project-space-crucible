package core.game.logic;

import com.badlogic.gdx.math.Rectangle;
import core.game.entities.Entity;
import core.level.info.LevelTile;

import java.util.ArrayList;

public class CollisionLogic {

    public static Entity entityCollision(Rectangle bounds, Entity entity){
        Entity collidedEntity = null;
        for(Entity entity2 : GameLogic.entityList){
            if(entity==entity2){
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
