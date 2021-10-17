package core.game.logic;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import core.game.entities.Entity;
import core.game.entities.Projectile;
import core.level.info.LevelTile;

import javax.swing.text.Position;

public class CollisionLogic {

    public static Entity entityCollision(Rectangle bounds, Entity entity){
        Entity collidedEntity = null;
        for(Entity entity2 : GameLogic.entityList){

            //No collision with self OR owner (if projectile)
            if(entity==entity2
                || entity instanceof Projectile && ((Projectile) entity).getOwner() == entity2
                || !entity2.getFlag(Entity.SOLID)){
                continue;
            }
            if(bounds.overlaps(entity2.getBounds())){
                collidedEntity = entity2;
                break;
            }
        }
        return collidedEntity;
    }

    public static LevelTile entityTileCollision(Rectangle bounds, Entity entity){
        LevelTile collidedTile = null;
        for(LevelTile levelTile : GameLogic.currentLevel.getTiles()){
            if(levelTile.solid) {
                Rectangle tileBounds
                        = new Rectangle(levelTile.pos.x * LevelTile.TILE_SIZE,
                                        levelTile.pos.y * LevelTile.TILE_SIZE,
                                            LevelTile.TILE_SIZE, LevelTile.TILE_SIZE);

                if(bounds.overlaps(tileBounds)) {
                    collidedTile = levelTile;
                    break;
                }
            }
        }
        return collidedTile;
    }


}
