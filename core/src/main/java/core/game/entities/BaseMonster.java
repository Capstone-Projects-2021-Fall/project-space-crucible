package core.game.entities;

public abstract class BaseMonster extends Entity {

    public BaseMonster(int health, Position pos, int speed, int width, int height, Integer[] states, int tag) {
        super(health, pos, speed, width, height, states, tag);
    }
}
