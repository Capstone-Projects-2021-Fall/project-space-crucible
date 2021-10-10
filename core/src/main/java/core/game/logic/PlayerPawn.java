package core.game.logic;

import net.mtrop.doom.WadFile;

public class PlayerPawn extends Entity {

    public PlayerPawn(int health, Position pos, int speed, int width, int height) {
        super(health, pos, speed, width, height, new Integer[]{0, 1, 5, 6, 7, 9});
    }


}
