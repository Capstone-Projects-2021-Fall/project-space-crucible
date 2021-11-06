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

import java.util.ConcurrentModificationException;

public class RenderFuncs {

    final public static Map<String, GameSprite> spriteMap = new HashMap<>();
    final public static Map<String, Texture> textureMap = new HashMap<>();

    public static void worldDraw(SpriteBatch batch, ArrayList <LevelTile> tiles, boolean editor, boolean fullbright) {
        for (LevelTile tile : tiles) {

            //Special exception, draw edges as black always, but only in non-editor mode
            if (!fullbright) {
                if (tile.graphicname.equals("EDGE") && !editor) {
                    batch.setColor(0f, 0f, 0f, 255f);
                } else {
                    float light = (float) tile.light / 255;
                    batch.setColor(light, light, light, 255f);
                }
            } else {batch.setColor(255f, 255f, 255f, 255f);}

            if (tile.solid) {
                batch.draw(textureMap.get(tile.graphicname),
                        tile.pos.x * LevelTile.TILE_SIZE,
                        tile.pos.y * LevelTile.TILE_SIZE);
            } else {
                batch.draw(textureMap.get(tile.graphicname),
                        tile.pos.x * LevelTile.TILE_SIZE,
                        tile.pos.y * LevelTile.TILE_SIZE,
                        LevelTile.TILE_SIZE, (float)LevelTile.TILE_SIZE / 2);
                batch.draw(textureMap.get(tile.graphicname),
                        tile.pos.x * LevelTile.TILE_SIZE,
                        tile.pos.y * LevelTile.TILE_SIZE + (float)(LevelTile.TILE_SIZE / 2),
                        LevelTile.TILE_SIZE, (float)LevelTile.TILE_SIZE / 2);
            }
        }
    }

    public static void entityDraw(SpriteBatch batch, ArrayList <Entity> entities) {

        try {
            for (Entity e : entities) {
                //Check tile light level at player half-height
                float x = e.getPos().x, y = (e.getPos().y);
                int tilex = (int)x/LevelTile.TILE_SIZE, tiley = (int)y/LevelTile.TILE_SIZE;

                if (GameLogic.currentLevel != null) {
                    LevelTile tile = GameLogic.currentLevel.getTile(tilex, tiley);

                    if (!GameLogic.switchingLevels) {

                        float light;

                        if (tile != null) {
                            light = (float) tile.light / 255;
                        } else {
                            light = 0f;
                        }
                        batch.setColor(light, light, light, 255f);
                    }
                }
                batch.draw(spriteMap.get(e.getCurrentSprite()).getFrame(e.getCurrentFrame(), e.getPos().angle), e.getPos().x, e.getPos().y);
            }
        } catch (ConcurrentModificationException | NullPointerException ignored) {}
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

