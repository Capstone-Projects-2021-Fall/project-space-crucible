package core.game.entities.actions;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import core.game.entities.BaseMonster;
import core.game.entities.Entity;
import core.game.logic.CollisionLogic;

import java.util.concurrent.ThreadLocalRandom;

//Enemy chases its target. If target is null (no target), for whatever reason, set state to Idle and abort.
public class A_Chase implements StateAction {

    @Override
    public void run(Entity caller, Entity target) {

        if (target == null || target.getHealth() <= 0)
        {
            caller.setState(caller.getStates()[Entity.IDLE]);
            return;
        }

        Vector2 start = caller.getCenter();

        if (ThreadLocalRandom.current().nextInt()  % 10 == 0
            && (CollisionLogic.checkFOVForEntity(start.x, start.y, caller.getPos().angle, caller, target))
            && caller.getStates()[Entity.MISSILE] != -1) {
            caller.setState(caller.getStates()[Entity.MISSILE]);
        }

        smartCollision(caller, target);
    }

    private void smartCollision(Entity caller, Entity target) {

        Vector2 distance = new Vector2();

        //Try to get to player
        distance.x = target.getPos().x - caller.getPos().x;
        distance.y = target.getPos().y - caller.getPos().y;

        if (distance.len() <= 64f) {
            caller.setState(caller.getStates()[Entity.MELEE]);
            return;
        }
        caller.getPos().angle = distance.angleDeg();
        distance.nor();

        float checkPosX = caller.getPos().x + caller.getSpeed() * distance.x;
        float checkPosY = caller.getPos().y + caller.getSpeed() * distance.y;

        int directionX = distance.x > 0? 1 : -1;
        int directionY = distance.y > 0? 1 : -1;

        //Check only x first
        Rectangle newBounds = new Rectangle(checkPosX, caller.getPos().y, caller.getWidth(), caller.getHeight());

        if (CollisionLogic.entityCollision(newBounds, caller) == null
                && CollisionLogic.entityTileCollision(newBounds, caller) == null) {
            caller.hitx = false;
        } else {caller.hitx = true;}

        //Check y now
        newBounds.set(caller.getPos().x, checkPosY, caller.getWidth(), caller.getHeight());

        if (CollisionLogic.entityCollision(newBounds, caller) == null
                && CollisionLogic.entityTileCollision(newBounds, caller) == null) {
            caller.hity = false;
        } else {caller.hity = true;}

        //No problems. Take direct route. No x or y hit.
        if (!(caller.hitx || caller.hity)) {
            newBounds.set(checkPosX, checkPosY, caller.getWidth(), caller.getHeight());
            caller.setPos(checkPosX, checkPosY, newBounds);
            return;
        }

        //Now things get funky. First, check for an x coordinate hit.
        if (caller.hitx) {
            checkPosX = caller.getPos().x; //Don't move x

            //If there's a y hit, try opposite direction (no x movement)
            if (caller.hity) {
                checkPosY = caller.getPos().y - (directionY * caller.getSpeed()); // No x movement
                caller.getPos().angle = directionY > 0 ? Entity.SOUTH : Entity.NORTH;
            }

            //Else, try same direction (no x movement)
            else {
                checkPosY = caller.getPos().y + (directionY * caller.getSpeed()); // No x movement
                caller.getPos().angle = directionY > 0 ? Entity.NORTH : Entity.SOUTH;
            }

            //New position works for either y hit case. Proceed.
            newBounds.set(checkPosX, checkPosY, caller.getWidth(), caller.getHeight());
            if (CollisionLogic.entityCollision(newBounds, caller) == null
                    && CollisionLogic.entityTileCollision(newBounds, caller) == null) {
                caller.setPos(checkPosX, checkPosY, newBounds);
                return;
            }

            //Blocked on both y sides and x. Move in reverse x direction.
            //If blocked on all sides just give up. Something's wrong lol.
            checkPosY = caller.getPos().y;
            checkPosX = caller.getPos().x - (directionX * caller.getSpeed());
            caller.getPos().angle = directionX > 0 ? Entity.WEST : Entity.EAST;

            if (CollisionLogic.entityCollision(newBounds, caller) == null
                    && CollisionLogic.entityTileCollision(newBounds, caller) == null) {
                caller.setPos(checkPosX, checkPosY, newBounds);
            }
        }

        //Only one possibility: there is no x hit but there is a y hit.
        else {
            checkPosY = caller.getPos().y;
            checkPosX = caller.getPos().x + (directionX * caller.getSpeed());
            caller.getPos().angle = directionX > 0 ? Entity.EAST : Entity.WEST;
            newBounds.set(checkPosX, checkPosY, caller.getWidth(), caller.getHeight());

            //Try one way
            if (CollisionLogic.entityCollision(newBounds, caller) == null
                    && CollisionLogic.entityTileCollision(newBounds, caller) == null) {
                caller.setPos(checkPosX, checkPosY, newBounds);
                return;
            }

            //Try other way
            checkPosX = caller.getPos().x - (directionX * caller.getSpeed());
            caller.getPos().angle = directionX > 0 ? Entity.WEST : Entity.EAST;
            if (CollisionLogic.entityCollision(newBounds, caller) == null
                    && CollisionLogic.entityTileCollision(newBounds, caller) == null) {
                caller.setPos(checkPosX, checkPosY, newBounds);
                return;
            }

            //Both fail. Do reverse. Stop if all fail.
            checkPosY = caller.getPos().y - (directionY * caller.getSpeed());
            checkPosX = caller.getPos().x;
            caller.getPos().angle = directionY > 0 ? Entity.SOUTH : Entity.NORTH;

            if (CollisionLogic.entityCollision(newBounds, caller) == null
                    && CollisionLogic.entityTileCollision(newBounds, caller) == null) {
                caller.setPos(checkPosX, checkPosY, newBounds);
            }
        }
    }
}
