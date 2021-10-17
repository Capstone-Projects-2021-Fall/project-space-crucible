package core.gdx.wad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import core.game.logic.GameLogic;
import core.wad.funcs.WadFuncs;
import net.mtrop.doom.WadFile;

import java.io.IOException;

public class TitleScreen implements Screen {
    private MyGDxTest game;
    OrthographicCamera camera;
    SpriteBatch batch;
    Texture texture;
    Thread gameLoop;

    public TitleScreen(MyGDxTest game, Thread gameLoop) {
        WadFile file;

        try {
            file = new WadFile(Gdx.files.internal("assets/resource.wad").file());
            String path = file.getFileAbsolutePath();
            this.game=game;
            this.gameLoop=gameLoop;
            camera = new OrthographicCamera();
            camera.setToOrtho(false, 1920, 1080);
            batch = new SpriteBatch();
            texture = WadFuncs.getTexture(file, "TITLESCR"); //title screen
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

            file.close();
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

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.setProjectionMatrix(camera.combined);
        batch.enableBlending();
        //drawing sprite background
        batch.begin();
        //this is for testing using the input based on screen size instead of hardcoded pixels
        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.rect(camera.viewportWidth/9+40, camera.viewportHeight/5+25, 120,50);
        batch.draw(texture,15,15);
        if(Gdx.input.getX() > 260 && Gdx.input.getX() < 350 && Gdx.input.getY() > 180 && Gdx.input.getY() < 250){
            if(Gdx.input.isTouched()){
                GameLogic.changeLevel(GameLogic.levels.get(1));
                game.setScreen(new GameScreen(gameLoop));
                dispose();
            }
        }
        batch.end();
        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {

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
