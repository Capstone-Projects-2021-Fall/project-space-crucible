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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import core.game.logic.Entity;
import core.game.logic.PlayerPawn;
import core.wad.funcs.GameSprite;
import core.wad.funcs.WadFuncs;
import net.mtrop.doom.WadFile;

import java.io.File;
import java.io.IOException;

public class GameScreen implements Screen {

    //launcher = myGame
    private MyGDxTest game;
    private WadFile file;

    PlayerPawn player;

    //screen
    OrthographicCamera camera;
    private final Vector2 mouseInWorld2D = new Vector2();
    private final Vector3 mouseInWorld3D = new Vector3();

    //graphics
    SpriteBatch batch;
    //Texture textureBack;
    //Sprite spriteBack;

    //Player Speed
    public static final float SPEED = 120;

    //character movement

    public GameScreen(MyGDxTest game) {
        try {
            file = new WadFile(Gdx.files.internal("assets/resource.wad").file());
            String path = file.getFileAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.game=game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);
        batch = new SpriteBatch();
        player = new PlayerPawn(100, new Entity.Position(0, 0, 0), 100, 32, 56,
                file, "PLAY");
        try {
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
        //This updates the player on the screen
        movementUpdate();

        //This centers the camera to the player
        //Get the angle where the mouse is pointing to on the screen in relation to where the player is
        //Referenced code - https://stackoverflow.com/questions/16381031/get-cursor-position-in-libgdx
        mouseInWorld3D.x = Gdx.input.getX() - player.getPos().x;
        mouseInWorld3D.y = Gdx.input.getY() + player.getPos().y;
        mouseInWorld3D.z = 0;
        camera.unproject(mouseInWorld3D); //unprojecting will give game world coordinates matching the pointer's position
        mouseInWorld2D.x = mouseInWorld3D.x;
        mouseInWorld2D.y = mouseInWorld3D.y;
        player.getPos().angle = mouseInWorld2D.angleDeg(); //Turn the vector2 into a degree angle
        System.out.println(player.getPos().angle + ", " + player.getPos().x + ", " + player.getPos().y);

        camera.position.set(player.getPos().x + player.getSprite('A', player.getPos().angle).getWidth()/2,
                player.getPos().y + player.getSprite('A', player.getPos().angle).getHeight()/2, 0);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(player.getSprite('A', player.getPos().angle), player.getPos().x, player.getPos().y);
        batch.end();
    }

    public void movementUpdate(){
        //Input handling with polling method
        //This handles all the keys pressed with the keyboard.
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))
            player.getPos().x -= SPEED * Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))
            player.getPos().x += SPEED * Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W))
            player.getPos().y += SPEED * Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S))
            player.getPos().y -= SPEED * Gdx.graphics.getDeltaTime();
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
