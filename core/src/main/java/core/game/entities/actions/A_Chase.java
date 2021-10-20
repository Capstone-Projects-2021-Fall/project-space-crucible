package core.game.entities.actions;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import core.game.entities.Entity;
import core.game.logic.CollisionLogic;

import java.util.concurrent.ThreadLocalRandom;

//Enemy chases its target. If target is null (no target), for whatever reason, set state to Idle and abort.
public class A_Chase implements StateAction {

    @Override
    public void run(Entity caller, Entity target) {

        //If there's no target, go back to idle.
        if (target == null || target.getHealth() <= 0)
        {
            caller.setState(caller.getStates()[Entity.IDLE]);
            return;
        }

        //If you can see the target, and you have a ranged attack, decide at random to attack.
        Vector2 start = caller.getCenter();
        if (ThreadLocalRandom.current().nextInt()  % 10 == 0
            && (CollisionLogic.checkFOVForEntity(start.x, start.y, caller.getPos().angle, caller, target))
            && caller.getStates()[Entity.MISSILE] != -1) {
            caller.setState(caller.getStates()[Entity.MISSILE]);
        }

        pursueTarget(caller, target);
    }

    private void pursueTarget(Entity caller, Entity target) {

        final float startX = caller.getPos().x;
        final float startY = caller.getPos().y;
        final int width = caller.getWidth();
        final int height = caller.getHeight();
        final int speed = caller.getSpeed();

        //Try to get to target
        Vector2 distance = new Vector2();
        distance.x = target.getPos().x - startX;
        distance.y = target.getPos().y - startY;
        float angleToTarget = distance.angleDeg();

        if (distance.len() <= 64f) {
            caller.setState(caller.getStates()[Entity.MELEE]);
            return;
        }
        distance.nor();

        float newX = startX + (speed * distance.x);
        float newY = startY + (speed * distance.y);

        Rectangle newBounds = new Rectangle(newX, newY, width, height);

        //No problems. Take direct route.
        if (CollisionLogic.simpleCollisionCheck(newBounds, caller)) {
            caller.getPos().angle = angleToTarget;
            caller.setPos(newBounds);
            return;
        }

        //There must've been a collision.
        int directionX = distance.x > 0? 1 : -1;
        int directionY = distance.y > 0? 1 : -1;

        //First, try to maintain current angle as long as possible.
        newBounds.set(startX + (float)(speed * Math.cos(Math.toRadians(caller.getPos().angle))),
                startY + (float)(speed * Math.sin(Math.toRadians(caller.getPos().angle))), width, height);
        if (CollisionLogic.simpleCollisionCheck(newBounds, caller)) {
            caller.setPos(newBounds);
            return;
        }

        //Can you go in the X direction towards the target?
        newBounds.set(startX + (speed * directionX), startY, width, height);
        if (CollisionLogic.simpleCollisionCheck(newBounds, caller)) {
            caller.getPos().angle = directionX > 0 ? Entity.EAST : Entity.WEST;
            caller.setPos(newBounds);
            return;
        }

        //Can you go in the Y direction towards the target?
        newBounds.set(startX, startY + (speed * directionY), width, height);
        if (CollisionLogic.simpleCollisionCheck(newBounds, caller)) {
            caller.getPos().angle = directionY > 0 ? Entity.NORTH : Entity.SOUTH;
            caller.setPos(newBounds);
            return;
        }

        //If the player x is closer, try opposite y direction first, else x. Try other opposite as last resort.
        System.out.println("Distance x: " + distance.x + "\nDistance y: " + distance.y);
        if (distance.x > distance.y) {
            newBounds.set(startX, startY - (speed * directionY), width, height);
            if (CollisionLogic.simpleCollisionCheck(newBounds, caller)) {
                caller.getPos().angle = directionY > 0 ? Entity.SOUTH : Entity.NORTH;
                caller.setPos(newBounds);
                return;
            }

            newBounds.set(startX - (speed * directionX), startY, width, height);
            if (CollisionLogic.simpleCollisionCheck(newBounds, caller)) {
                caller.getPos().angle = directionX > 0 ? Entity.WEST : Entity.EAST;
                caller.setPos(newBounds);

            }
        } else {
            newBounds.set(startX - (speed * directionX), startY, width, height);
            if (CollisionLogic.simpleCollisionCheck(newBounds, caller)) {
                caller.getPos().angle = directionX > 0 ? Entity.WEST : Entity.EAST;
                caller.setPos(newBounds);
                return;
            }

            newBounds.set(startX, startY - (speed * directionY), width, height);
            if (CollisionLogic.simpleCollisionCheck(newBounds, caller)) {
                caller.getPos().angle = directionY > 0 ? Entity.SOUTH : Entity.NORTH;
                caller.setPos(newBounds);
            }
        }

        //If none of that worked, you're probably stuck for some reason. Oops!
    }
}
