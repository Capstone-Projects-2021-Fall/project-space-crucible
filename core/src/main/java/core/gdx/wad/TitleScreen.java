package core.gdx.wad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import core.wad.funcs.SoundFuncs;
import core.wad.funcs.WadFuncs;
import net.mtrop.doom.WadFile;

import java.io.IOException;

public class TitleScreen implements Screen {
    public MyGDxTest game;
    OrthographicCamera camera;
    SpriteBatch batch;
    Texture texture;
    Thread gameLoop;
    private Stage stage = new Stage(new ScreenViewport());
    final private Skin skin = new Skin(Gdx.files.internal("assets/uiskin.json"));
    public boolean remove = false;
    public boolean isSinglePlayer = true;
    private boolean windowOpen = false;

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
        remove=false;
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {

        if (remove) {
            game.setScreen(new GameScreen(gameLoop, isSinglePlayer));
            dispose();
        }

        Gdx.gl.glClearColor(0,0,0,1F);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.enableBlending();
        //drawing sprite background
        batch.begin();
        batch.draw(texture,25,30);
        Actor startMenuActor = new StartMenu("Main Menu", skin, this, stage, game);
        stage.addActor(startMenuActor);
        startMenuActor.setPosition(Gdx.graphics.getWidth() / 2 - Gdx.graphics.getWidth() / 12, Gdx.graphics.getHeight() / 2 - Gdx.graphics.getHeight()/6);

        batch.end();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
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
        System.out.println("Remove: " + remove);
        if (!remove) {
            System.out.println("bye bye");
            System.exit(0);
        }
    }

    @Override
    public void dispose() {

    }
}
