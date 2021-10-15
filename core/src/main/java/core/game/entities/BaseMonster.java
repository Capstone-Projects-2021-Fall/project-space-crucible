package core.game.entities;


//The parent class of all AI-controlled enemies.
//The hold a target pointer for whichever Entity is drawing their aggression. This need not be a PlayerPawn.
//If target is null, the monster goes idle.
public abstract class BaseMonster extends Entity {

    private Entity target = null;

    public BaseMonster(int health, Position pos, int speed, int width, int height, Integer[] states, int tag) {
        super(health, pos, speed, width, height, states, tag);
    }
}
