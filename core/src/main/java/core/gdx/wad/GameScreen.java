package core.gdx.wad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import core.wad.funcs.WadFuncs;
import net.mtrop.doom.WadFile;

import java.io.File;
import java.io.IOException;

public class GameScreen implements Screen {

    //launcher = myGame
    private MyGDxTest game;
    private WadFile file;

    //screen
    OrthographicCamera camera;

    //graphics
    SpriteBatch batch;
    //Texture textureBack;
    //Sprite spriteBack;

    //character movement
    int x = 0;
    int y = 0;

    public GameScreen(MyGDxTest game) {
        try {
            file = new WadFile(Gdx.files.internal("assets/resource.wad").file());
            String path = file.getFileAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.game=game;
        this.file = file;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);
        batch = new SpriteBatch();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(00,00,00,1F);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        if(Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            y+=5;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)){
            x+=5;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN) ||  Gdx.input.isKeyPressed(Input.Keys.S)){
            y-=5;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)){
            x-=5;
        }

        batch.begin();
        batch.draw(WadFuncs.getSprite(file, "PLAYA1"), x, y);
        batch.end();

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
