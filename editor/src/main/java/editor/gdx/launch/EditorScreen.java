package editor.gdx.launch;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import core.level.info.LevelData;
import core.level.info.LevelTile;
import core.wad.funcs.WadFuncs;
import editor.gdx.prompts.EditTilePrompt;

import editor.gdx.prompts.EditorFrame;
import editor.gdx.write.LevelWriter;
import net.mtrop.doom.WadFile;

import javax.swing.*;
import java.io.IOException;

public class EditorScreen implements Screen {

    private LevelEditor editor;
    private OrthographicCamera camera;
    private ShapeRenderer sr;
    private SpriteBatch batch;
    private float cameraspeed = 5;
    private Vector3 mouseInWorld = new Vector3();

    private WadFile file;
    private LevelData level;
    private Integer levelnum;

    public EditorScreen(LevelEditor editor, WadFile file, Integer levelnum) {
        this.editor = editor;
        this.file = file;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);
        camera.position.set(0, 0, 0);
        batch = new SpriteBatch();
        sr = new ShapeRenderer();

        this.levelnum = levelnum;

        try {
            level = WadFuncs.loadLevel(file, levelnum);
        } catch (Exception e) {
            level = new LevelData(levelnum);

        }
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1F);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        checkShortcuts();
        checkControls();
        moveCamera();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        worldDraw();
        batch.end();

        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Line);
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
        sr.end();
    }

    private void checkControls() {

        int x = (int)(mouseInWorld.x);
        int y = (int)(mouseInWorld.y);

        int tilex = x/LevelTile.TILE_SIZE;
        int tiley = y/LevelTile.TILE_SIZE;

        if (x < 0) {tilex--;}
        if (y < 0) {tiley--;}

        System.out.println("Map tile is " + tilex + ", " + tiley);
        System.out.println("Mouse is at " + x + ", " + y);

        if(Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            editTilePrompt(tilex, tiley);
        } else if (Gdx.input.isButtonJustPressed(Input.Buttons.MIDDLE)) {

            LevelTile tile = level.getTile(tilex, tiley);
            level.getTiles().remove(tile);
        }
    }

    private void checkShortcuts() {
        if ((Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT))) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {

                try {
                    LevelWriter.write(file, level, levelnum);
                } catch (IOException e) {
                    System.out.println("Could not save!");
                    e.printStackTrace();
                }

            }
        }
    }

    private void moveCamera() {
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))
            camera.position.set(camera.position.x - cameraspeed, camera.position.y, 0);
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))
            camera.position.set(camera.position.x + cameraspeed, camera.position.y, 0);
        if(Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W))
            camera.position.set(camera.position.x, camera.position.y + cameraspeed, 0);
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S))
            camera.position.set(camera.position.x, camera.position.y - cameraspeed, 0);

        camera.update();
        //System.out.println(camera.position.x + ", " + camera.position.y);

        mouseInWorld.x = Gdx.input.getX();
        mouseInWorld.y = Gdx.input.getY();
        mouseInWorld.z = 0;
        camera.unproject(mouseInWorld);
    }

    private void worldDraw() {
        for (LevelTile tile : level.getTiles()) {
            batch.draw(tile.graphic,
                    tile.pos.x * LevelTile.TILE_SIZE,
                    tile.pos.y * LevelTile.TILE_SIZE);
        }
    }

    private void editTilePrompt(int tilex, int tiley) {

        LevelTile tile = level.getTile(tilex, tiley);

        if (tile == null) {
            tile = new LevelTile(new LevelTile.TilePosition(tilex, tiley),
                    false, "WALL1", 0, 0,
                    0, 0, false, 0 , file);
            level.getTiles().add(tile);
        }

        EditorFrame prompt = new EditorFrame(this);
        prompt.setContentPane(new EditTilePrompt(prompt, this, tile, file));
        prompt.setSize(430, 360);
        prompt.setResizable(false);
        prompt.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        prompt.setVisible(true);

        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        tile.graphic = WadFuncs.getTexture(file, tile.graphicname);
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
    }
}