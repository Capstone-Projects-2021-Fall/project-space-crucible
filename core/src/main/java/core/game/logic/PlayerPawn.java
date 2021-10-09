package core.game.logic;

import net.mtrop.doom.WadFile;

public class PlayerPawn extends Entity {

    public PlayerPawn(int health, Position pos, int speed, int width, int height, WadFile file, String spriteName) {
        super(health, pos, speed, width, height, file, spriteName);
    }
}
