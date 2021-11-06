package core.game.logic;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import core.game.entities.*;
import core.game.logic.tileactions.T_ChangeLevel;
import core.level.info.LevelTile;

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
            if(levelTile.solid || levelTile.effect > 0) {
                Rectangle tileBounds
                        = new Rectangle(levelTile.pos.x * LevelTile.TILE_SIZE,
                                        levelTile.pos.y * LevelTile.TILE_SIZE,
                                            LevelTile.TILE_SIZE, LevelTile.TILE_SIZE);

                if(bounds.overlaps(tileBounds)) {
                    if(levelTile.effect > 0) {
                        GameLogic.effectList.get(levelTile.effect - 1)
                                .run(entity, levelTile.arg1, levelTile.arg2);
                        break;
                    }
                    else if (levelTile.solid) {
                        collidedTile = levelTile;
                        break;
                    }
                }
            }
        }
        return collidedTile;
    }

    public static Entity hitscanLine(float startx, float starty, float angle, Entity source, boolean attack) {
        float xpos = startx;
        float ypos = starty;
        Vector2 distance = new Vector2();
        distance.x = (float) Math.cos(Math.toRadians(angle));
        distance.y = (float) Math.sin(Math.toRadians(angle));

        //Advance line until something is hit.
        //If this locks the game, something is wrong! The world must be exposed to the void.
        while(true) {

            //Check for Entities
            for(Entity entity2 : GameLogic.entityList){

                //Skip source Entity and non-solid Entities
                if (entity2 == source
                        || (!entity2.getFlag(Entity.SOLID))) {continue;}

                if(entity2.getBounds().contains(xpos, ypos)){
                    if (attack) {
                        GameLogic.newEntityQueue.addLast(
                                GameLogic.entityTable.get("Blood")
                                        .spawnEntity(new Entity.Position(xpos, ypos, 0f), 0));
                    }
                    return entity2;
                }
            }

            //Check for tiles
            boolean foundTile = false;
            for(LevelTile t :GameLogic.currentLevel.getTiles()) {

                Rectangle tileBounds
                        = new Rectangle(t.pos.x * LevelTile.TILE_SIZE,
                        t.pos.y * LevelTile.TILE_SIZE,
                        LevelTile.TILE_SIZE, LevelTile.TILE_SIZE);

                if(tileBounds.contains(xpos, ypos)) {
                    foundTile = true;

                    if (t.solid) {
                        if (attack) {
                            GameLogic.newEntityQueue.addLast(
                                    GameLogic.entityTable.get("BulletPuff")
                                            .spawnEntity(new Entity.Position(xpos, ypos, 0f), 0));
                        }
                        return null;
                    }
                    break;
                }
            }

            if (!foundTile) {
                System.out.println("Caught a would-be runaway hitscan. ;)");
                return null;
            }

            xpos += distance.x;
            ypos += distance.y;
        }
    }

    //Checks FOV for any instance of the given class- make sure they're not dead.
    public static Entity checkFOVForClass(float startx, float starty, float refAngle, Entity caller, Class<? extends Entity> seeking) {
        for (float angle = refAngle-90; angle != refAngle + 90; angle += 9) {
            Entity e = CollisionLogic.hitscanLine(startx, starty, angle, caller, false);
            if (e != null) {

                if (seeking.isInstance(e) && e.getHealth() > 0) {
                    return e;
                }
            }
        }
        return null;
    }

    //Checks FOV for a specific Entity
    public static boolean checkFOVForEntity(float startx, float starty, float refAngle, Entity caller, Entity seeking) {
        for (float angle = refAngle-90; angle != refAngle + 90; angle += 9) {
            Entity e = CollisionLogic.hitscanLine(startx, starty, angle, caller, false);
            if (e != null) {

                if (seeking == e) {
                    return seeking.getHealth() > 0;
                }
            }
        }
        return false;
    }

    //Plain collision check with no return values needed
    public static boolean simpleCollisionCheck(Rectangle bounds, Entity caller) {
        return CollisionLogic.entityCollision(bounds, caller) == null
                && CollisionLogic.entityTileCollision(bounds, caller) == null;
    }
}
