package editor.gdx.launch;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import core.gdx.wad.RenderFuncs;
import core.level.info.LevelData;
import core.level.info.LevelTile;
import core.wad.funcs.WadFuncs;
import editor.gdx.windows.EditTileWindow;
import editor.gdx.windows.FileChooserWindow;
import editor.gdx.windows.LevelChooserWindow;
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

    public WadFile file = null;
    public Array<WadFile> resources = new Array<>();
    private LevelData level;
    public Integer levelnum;
    public boolean windowOpen;

    //UI Stuff
    public Stage stage = new Stage(new ScreenViewport());
    final private Skin skin = new Skin(Gdx.files.internal("assets/uiskin.json"));

    public EditorScreen(LevelEditor editor) {
        this.editor = editor;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);
        camera.position.set(0, 0, 0);
        batch = new SpriteBatch();
        sr = new ShapeRenderer();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        openFilePrompt();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1F);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        checkShortcuts();
        checkControls();
        moveCamera();

        if (level != null) {
            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            RenderFuncs.worldDraw(batch, level);
            RenderFuncs.entityDraw(batch);
            batch.end();

            sr.setProjectionMatrix(camera.combined);
            sr.begin(ShapeRenderer.ShapeType.Line);
            RenderFuncs.gridDraw(camera, sr);
            sr.end();
        }

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    private void checkControls() {

        if (windowOpen) {return;}

        int x = (int)(mouseInWorld.x);
        int y = (int)(mouseInWorld.y);

        int tilex = x/LevelTile.TILE_SIZE;
        int tiley = y/LevelTile.TILE_SIZE;

        if (x < 0) {tilex--;}
        if (y < 0) {tiley--;}

        if(Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            editTilePrompt(tilex, tiley);
        } else if (Gdx.input.isButtonJustPressed(Input.Buttons.MIDDLE)) {

            LevelTile tile = level.getTile(tilex, tiley);
            level.getTiles().remove(tile);
        }
    }

    private void checkShortcuts() {

        if (windowOpen) {return;}

        if (isCtrlPressed()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {

                try {
                    LevelWriter.write(file, level, levelnum);
                } catch (IOException e) {
                    System.out.println("Could not save!");
                    e.printStackTrace();
                }

            } else if (Gdx.input.isKeyJustPressed(Input.Keys.O)) {
                if (isShiftPressed()) {
                    level = null;
                    openLevelPrompt();
                    windowOpen = true;
                } else {
                    openFilePrompt();
                    windowOpen = true;
                }
            }
        }
    }

    private void moveCamera() {

        if (windowOpen) {return;}

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

    private void editTilePrompt(int tilex, int tiley) {

        LevelTile tile = level.getTile(tilex, tiley);

        if (tile == null) {
            tile = new LevelTile(new LevelTile.TilePosition(tilex, tiley),
                    false, "WALL1", 0, 0,
                    0, 0, false, 0 , resources);
            level.getTiles().add(tile);
        }

        stage.addActor(new EditTileWindow("Edit Tile", skin, tile, resources, this));
        windowOpen = true;
    }

    public void openFilePrompt() {
        windowOpen = true;
        stage.addActor(new FileChooserWindow("Choose File:", skin, this));
    }

    public void openLevelPrompt() {
        windowOpen = true;
        stage.addActor(new LevelChooserWindow("Choose ", skin, file, this));
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        stage.getViewport().update(width, height, true);
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

    public void loadLevel() {
        level = WadFuncs.loadLevel(file, levelnum);
    }

    public void loadNewLevel(String name, Integer level) {
        this.level = new LevelData(name, level);
        levelnum = level;
    }

    private boolean isCtrlPressed() {
        return (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT));
    }
    private boolean isShiftPressed() {
        return (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));
    }
}
