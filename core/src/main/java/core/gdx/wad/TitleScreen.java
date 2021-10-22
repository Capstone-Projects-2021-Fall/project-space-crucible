package core.gdx.wad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import core.game.logic.GameLogic;
import core.wad.funcs.WadFuncs;
import net.mtrop.doom.WadFile;

import javax.swing.text.StyledEditorKit;
import java.io.IOException;

public class TitleScreen implements Screen {
    public MyGDxTest game;
    OrthographicCamera camera;
    SpriteBatch batch;
    Texture texture;
    Thread gameLoop;
    public Stage stage = new Stage(new ScreenViewport());
    final private Skin skin = new Skin(Gdx.files.internal("assets/uiskin.json"));
    public boolean remove = false;
    public boolean isSinglePlayer = true;

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

    public static class StartMenu extends Window {
        public StartMenu(String title, Skin skin, TitleScreen titleScreen) {
            super(title, skin);
            setModal(false);
            List<String> startList = new List<>(skin);
//            startList.setHeight(1000); //not working
//            startList.setWidth(500); //not working
//            startList.setPosition(500, 500); //not working (startMenuActor.setPosition below)
            startList.setItems("Start", "Co-op", "Settings");
            add(startList);
            row();
            pack();
            startList.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    String clickedItem = startList.getPressedItem();
                    if(clickedItem==startList.getItemAt(0)){
                        System.out.println("Start Selected\n");
                    }
                    if(clickedItem==startList.getItemAt(1)){
                        System.out.println("Co-op Selected\n");
                    }
                }
            });
        }
    }





    @Override
    public void show() {
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
        Actor startMenuActor = new StartMenu("Main Menu", skin, this);
        stage.addActor(startMenuActor);
        startMenuActor.setPosition(270,140);
        //startMenuActor.setPosition(camera.viewportWidth, camera.viewportHeight); //instead of hardcoded position
        if(Gdx.input.getX() > 260 && Gdx.input.getX() < 350 && Gdx.input.getY() > 180 && Gdx.input.getY() < 250){
            if(Gdx.input.isTouched()){
                stage.addActor(new ChooseDifficultyWindow("Choose Difficulty:", skin, this));
            }
        }else if(Gdx.input.getX() > 260 && Gdx.input.getX() < 350 && Gdx.input.getY() > 300 && Gdx.input.getY() < 350){
            if(Gdx.input.isTouched()){
                isSinglePlayer = false;
                remove = true;
            }
        }
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
