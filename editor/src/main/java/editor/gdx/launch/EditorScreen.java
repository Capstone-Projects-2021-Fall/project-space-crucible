package editor.gdx.launch;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import core.level.info.LevelData;
import core.level.info.LevelTile;
import core.wad.funcs.WadFuncs;
import net.mtrop.doom.WadFile;

import java.io.IOException;

public class EditorScreen implements Screen {

    private LevelEditor editor;
    private OrthographicCamera camera;
    private LevelData level;
    private ShapeRenderer sr;
    private SpriteBatch batch;
    private WadFile file;
    private float cameraspeed = 5;

    public EditorScreen(LevelEditor editor){
        this.editor = editor;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);
        camera.position.set(0, 0, 0);
        batch = new SpriteBatch();
        sr = new ShapeRenderer();


        try {
            file = new WadFile(Gdx.files.internal("assets/resource.wad").file());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1F);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        moveCamera();

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

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.end();
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
