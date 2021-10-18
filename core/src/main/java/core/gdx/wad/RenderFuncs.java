package core.gdx.wad;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import core.game.entities.Entity;
import core.game.logic.GameLogic;
import core.level.info.LevelData;
import core.level.info.LevelTile;
import core.wad.funcs.GameSprite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RenderFuncs {

    final public static Map<String, GameSprite> spriteMap = new HashMap<>();
    final public static Map<String, Texture> textureMap = new HashMap<>();

    public static void worldDraw(SpriteBatch batch, ArrayList <LevelTile> tiles) {
        for (LevelTile tile : tiles) {
            batch.draw(textureMap.get(tile.graphicname),
                    tile.pos.x * LevelTile.TILE_SIZE,
                    tile.pos.y * LevelTile.TILE_SIZE);
        }
    }

    public static void entityDraw(SpriteBatch batch, ArrayList <Entity> entities) {

        for (Entity e : entities) {
            batch.draw(RenderFuncs.spriteMap.get(e.getCurrentSprite()).getFrame(e.getCurrentFrame(), e.getPos().angle),
                    e.getPos().x, e.getPos().y);
        while (GameLogic.midTic) {}
        for (Entity e : GameLogic.entityList) {
            batch.draw(e.getCurrentSprite(), e.getPos().x, e.getPos().y);
        }
    }

    public static void gridDraw(OrthographicCamera camera, ShapeRenderer sr) {
        for (float x = camera.position.x - camera.viewportWidth/2; x < camera.position.x + camera.viewportWidth/2; x += 1.0) {
            if (((int)x) % LevelTile.TILE_SIZE == 0) {
                sr.line(x, camera.position.y + (camera.viewportHeight/2),
                        x, camera.position.y - (camera.viewportHeight/2));
            }
        }

        for (float y = camera.position.y - camera.viewportHeight/2; y < camera.position.y + camera.viewportWidth/2; y+= 1.0) {
            if (((int)y) % LevelTile.TILE_SIZE == 0) {
                sr.line(camera.position.x + (camera.viewportWidth/2), y,
                        camera.position.x - (camera.viewportWidth/2), y);
            }
        }
    }
}

