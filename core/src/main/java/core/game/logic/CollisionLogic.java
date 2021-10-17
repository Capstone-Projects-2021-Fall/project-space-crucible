package core.game.logic;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
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

    public static Entity hitscanLine(float startx, float starty, float angle, Entity source) {
        float xpos = startx;
        float ypos = starty;
        Vector2 end = new Vector2(startx, starty);

        Vector2 distance = new Vector2();
        distance.x = (float) Math.cos(Math.toRadians(angle));
        distance.y = (float) Math.sin(Math.toRadians(angle));

        while(true) {
            for(Entity entity2 : GameLogic.entityList){

                if (entity2 == source) {continue;}

                if(entity2.getBounds().contains(xpos, ypos)){
                    end.set(xpos, ypos);
                    return entity2;
                }
            }

            for(LevelTile t :GameLogic.currentLevel.getTiles()) {
                if(t.solid) {
                    Rectangle tileBounds
                            = new Rectangle(t.pos.x * LevelTile.TILE_SIZE,
                            t.pos.y * LevelTile.TILE_SIZE,
                            LevelTile.TILE_SIZE, LevelTile.TILE_SIZE);

                    if(tileBounds.contains(xpos, ypos)) {
                        return null;
                    }
                }
            }

            xpos += distance.x;
            ypos += distance.y;
        }
    }
}
