package core.game.logic;

import com.badlogic.gdx.graphics.g2d.Sprite;
import core.wad.funcs.GameSprite;
import net.mtrop.doom.WadFile;

public class Entity {

    public static class Position {
        public int x;
        public int y;
        public float angle;

        public Position(int x, int y, float angle) {
            this.x = x;
            this.y = y;
            this.angle = angle;
        }
    }

    private int health;
    private Position pos;
    private int speed;
    private int width;
    private int height;
    private GameSprite sprite;
    private GameSprite.GameSpriteFrame currentFrame;

    public Entity (int health, Position pos, int speed, int width, int height, WadFile file, String spriteName) {
        this.health = health;
        this.pos = pos;
        this.speed = speed;
        this.width = width;
        this.height = height;
        sprite = new GameSprite(file, spriteName);
    }

    public Sprite getSprite(Character frame, float angle) {
        return sprite.getFrame(frame, angle);
    }

    public Position getPos() {
        return pos;
    }
}
