package core.game.logic;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import core.game.entities.*;
import core.game.logic.tileactions.T_ChangeLevel;
import core.level.info.LevelTile;
import org.lwjgl.system.CallbackI;

import java.util.ArrayList;
import java.util.List;

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

            /*else if (entity instanceof Keys) {
                Keys keys = (Keys) entity;
                if (bounds.overlaps(keys.getBounds())) {
                    if (entity instanceof PlayerPawn) {
                        PlayerPawn playerPawn = (PlayerPawn) entity;
                        playerPawn.addKeys(keys);
                        collidedEntity = entity;
                        System.out.println("Keys got picked up.");
                    }
                }
            }*/


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

    public static Entity entityKeyCollision(Rectangle bounds, Entity genericEntity){
        Entity collidedKey = null;
        for(Entity entity : GameLogic.entityList){
            if (entity instanceof Keys) {
                YellowKey Ykey = (YellowKey) entity;
                if (bounds.overlaps(Ykey.getBounds())) {
                    if (genericEntity instanceof PlayerPawn) {
                        PlayerPawn playerPawn = (PlayerPawn) genericEntity;
                        playerPawn.addYellowKey(Ykey);
                        collidedKey = entity;
                        int n=0;
                        n++;
                        System.out.println("Yellow keys: "+ n);
                        GameLogic.deleteEntityQueue.addLast(Ykey);
                    }
                }
            }
        }
        return collidedKey;
    }
   public static Entity entityBlueKeyCollision(Rectangle bounds, Entity genericBlueEntity){
        Entity collidedBKey = null;
        for(Entity entity : GameLogic.entityList){
            if (entity instanceof BlueKey) {
                BlueKey Bkey = (BlueKey) entity;
                if (bounds.overlaps(Bkey.getBounds())) {
                    if (genericBlueEntity instanceof PlayerPawn) {
                        PlayerPawn playerPawn = (PlayerPawn) genericBlueEntity;
                        playerPawn.addBlueKey(Bkey);
                        collidedBKey = entity;
                        int n=0;
                        n++;
                        System.out.println("Blue keys: "+ n);
                        GameLogic.deleteEntityQueue.addLast(Bkey);
                    }
                }
            }
        }
        return collidedBKey;
    }
    public static Entity entityRedKeyCollision(Rectangle bounds, Entity genericRedEntity){
        Entity collidedRKey = null;
        for(Entity entity : GameLogic.entityList){
            if (entity instanceof RedKey) {
                RedKey Rkey = (RedKey) entity;
                if (bounds.overlaps(Rkey.getBounds())) {
                    if (genericRedEntity instanceof PlayerPawn) {
                        PlayerPawn playerPawn = (PlayerPawn) genericRedEntity;
                        playerPawn.addRedKey(Rkey);
                        collidedRKey = entity;
                        int n=0;
                        n++;
                        System.out.println("Red Keys: "+ n );
                        GameLogic.deleteEntityQueue.addLast(Rkey);
                    }
                }
            }
        }
        return collidedRKey;
    }

    public static Entity entityShotgunCollision(Rectangle bounds, Entity genericShotgunEntity){
        Entity collidedshotgun = null;
        for(Entity entity : GameLogic.entityList){
            if (entity instanceof Shotgun) {
                Shotgun shot_gun = (Shotgun) entity;
                if (bounds.overlaps(shot_gun.getBounds())) {
                    if (genericShotgunEntity instanceof PlayerPawn) {
                        PlayerPawn playerPawn = (PlayerPawn) genericShotgunEntity;
                        playerPawn.addShotgun(shot_gun);
                        collidedshotgun = entity;
                        int n=0;
                        n++;
                        System.out.println("shotgun picked: "+ n);
                        GameLogic.deleteEntityQueue.addLast(shot_gun);
                    }
                }
            }
        }
        return collidedshotgun;
    }


    public static Entity entityChaingunCollision(Rectangle bounds, Entity genericChainEntity){
        Entity collidedchaingun = null;
        for(Entity entity : GameLogic.entityList){
            if (entity instanceof Chaingun) {
                Chaingun chain_gun = (Chaingun) entity;
                if (bounds.overlaps(chain_gun.getBounds())) {
                    if (genericChainEntity instanceof PlayerPawn) {
                        PlayerPawn playerPawn = (PlayerPawn) genericChainEntity;
                        playerPawn.addChaingun(chain_gun);
                        collidedchaingun = entity;
                        int n=0;
                        n++;
                        System.out.println("chainguns picked: " + n);
                        GameLogic.deleteEntityQueue.addLast(chain_gun);
                    }
                }
            }
        }
        return collidedchaingun;
    }

    public static Entity entityPlasmaweaponCollision(Rectangle bounds, Entity genericPlasmaEntity){
        Entity collidedplasma = null;
        for(Entity entity : GameLogic.entityList){
            if (entity instanceof PlasmaWeapon) {
                PlasmaWeapon plasma_weapon = (PlasmaWeapon) entity;
                if (bounds.overlaps(plasma_weapon.getBounds())) {
                    if (genericPlasmaEntity instanceof PlayerPawn) {
                        PlayerPawn playerPawn = (PlayerPawn) genericPlasmaEntity;
                        playerPawn.addPlasmaWeapon(plasma_weapon);
                        collidedplasma = entity;
                        int n=0;
                        n++;
                        System.out.println("Plasma weapons picked up: " + n);
                        GameLogic.deleteEntityQueue.addLast(plasma_weapon);
                    }
                }
            }
        }
        return collidedplasma;
    }

    public static Entity entityRocketLauncherCollision(Rectangle bounds, Entity genericRocketEntity){
        Entity collidedrocket = null;
        for(Entity entity : GameLogic.entityList){
            if (entity instanceof RocketLauncher) {
                RocketLauncher rocket_launcher = (RocketLauncher) entity;
                if (bounds.overlaps(rocket_launcher.getBounds())) {
                    if (genericRocketEntity instanceof PlayerPawn) {
                        PlayerPawn playerPawn = (PlayerPawn) genericRocketEntity;
                        playerPawn.addRocketLauncher(rocket_launcher);
                        collidedrocket = entity;
                        int n=0;
                        n++;
                        System.out.println("rocket launcher picked: " + n);
                        GameLogic.deleteEntityQueue.addLast(rocket_launcher);
                    }
                }
            }
        }
        return collidedrocket;
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
                                new Blood(new Entity.Position(xpos, ypos, 0)));
                    }
                    return entity2;
                }
            }

            //Check for tiles
            for(LevelTile t :GameLogic.currentLevel.getTiles()) {

                //Don't waste time on non-solid tiles
                if(t.solid) {
                    Rectangle tileBounds
                            = new Rectangle(t.pos.x * LevelTile.TILE_SIZE,
                            t.pos.y * LevelTile.TILE_SIZE,
                            LevelTile.TILE_SIZE, LevelTile.TILE_SIZE);

                    if(tileBounds.contains(xpos, ypos)) {
                        if (attack) {
                        GameLogic.newEntityQueue.addLast(
                                new BulletPuff(new Entity.Position(xpos, ypos, 0)));
                    }
                        return null;
                    }
                }
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
                && CollisionLogic.entityTileCollision(bounds, caller) == null
                && CollisionLogic.entityKeyCollision(bounds, caller) == null
                && CollisionLogic.entityBlueKeyCollision(bounds, caller) == null
                && CollisionLogic.entityRedKeyCollision(bounds, caller) == null
                && CollisionLogic.entityShotgunCollision(bounds, caller) == null
                && CollisionLogic.entityPlasmaweaponCollision(bounds, caller) == null
                && CollisionLogic.entityChaingunCollision(bounds, caller) == null
                && CollisionLogic.entityRocketLauncherCollision(bounds, caller) == null;
    }


}
