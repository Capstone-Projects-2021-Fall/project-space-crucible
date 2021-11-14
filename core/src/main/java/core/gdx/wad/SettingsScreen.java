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
import core.wad.funcs.WadFuncs;
import net.mtrop.doom.WadFile;

import java.io.IOException;

public class SettingsScreen implements Screen {
    MyGDxTest myGDxTest;
    OrthographicCamera camera;
    SpriteBatch batch;
    Texture texture;
    public boolean remove = false;
    private Stage stage = new Stage(new ScreenViewport());
    final private Skin skin = new Skin(Gdx.files.internal("assets/uiskin.json"));

     public SettingsScreen(MyGDxTest myGDxTest) {
        WadFile file;
        try {
            file = new WadFile(Gdx.files.internal("assets/resource.wad").file());
            camera = new OrthographicCamera();
            camera.setToOrtho(false, 1920, 1080);
            batch = new SpriteBatch();
            texture = WadFuncs.SETTINGSSCREEN;
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            this.myGDxTest=myGDxTest;

            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void show() {
         Gdx.input.setInputProcessor(stage);
        Actor settingsActor = new SettingsMenu("Settings", skin, this, stage, myGDxTest);
        stage.addActor(settingsActor);
        settingsActor.setPosition((Gdx.graphics.getWidth() - settingsActor.getWidth())/ 2f, (Gdx.graphics.getHeight() - settingsActor.getHeight())/ 2f);

    }

    @Override
    public void render(float delta) {

        if (remove) {
            myGDxTest.setScreen(myGDxTest.settingsScreen);
            dispose();
        }

        Gdx.gl.glClearColor(0,0,0,1F);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.enableBlending();
        batch.begin();
        batch.draw(texture,25,30);
        batch.end();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
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
        System.out.println("Remove: " + remove);
        if (!remove) {
            System.out.println("bye bye");
        }
    }

    @Override
    public void dispose() {

    }
}
