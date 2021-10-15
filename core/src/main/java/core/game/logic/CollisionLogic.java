package core.game.logic;

import com.badlogic.gdx.math.Rectangle;
import core.game.entities.Entity;
import core.level.info.LevelTile;

public class CollisionLogic {

    public static void entityCollision(Entity entity1, Entity entity2){
        if(entity1.getBounds().overlaps(entity2.getBounds())){
            /*code here to block entity from advancing*/
            System.out.println("Hey there.\n");
        }
    }


    public static void entityTileCollisionOccurred(Entity entity, LevelTile levelTile){

    }


}
