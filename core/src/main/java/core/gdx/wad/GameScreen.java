package core.gdx.wad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import core.game.logic.Entity;
import core.game.logic.EntityState;
import core.game.logic.PlayerPawn;
import core.level.info.LevelData;
import core.level.info.LevelTile;
import core.wad.funcs.GameSprite;
import core.wad.funcs.WadFuncs;
import net.mtrop.doom.WadFile;

import java.io.IOException;

public class GameScreen implements Screen {

    //launcher = myGame
    private MyGDxTest game;

    PlayerPawn player;

    //screen
    OrthographicCamera camera;
    private final Vector2 mouseInWorld2D = new Vector2();
    private final Vector3 mouseInWorld3D = new Vector3();

    //Level
    LevelData level;

    //graphics
    SpriteBatch batch;

    //Player Speed
    public static final float SPEED = 120;

    //character movement

    public GameScreen(MyGDxTest game) {
        WadFile file;

        try {
            file = new WadFile(Gdx.files.internal("assets/resource.wad").file());
            String path = file.getFileAbsolutePath();
            this.game=game;
            camera = new OrthographicCamera();
            camera.setToOrtho(false, 1920, 1080);
            batch = new SpriteBatch();

            level = new LevelData(file, 1);
            loadSprites(file);
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Temporarily hard-code statelist for proof-of-concept.
        loadStates();
        player = new PlayerPawn(100, new Entity.Position(0, 0, 0), 100, 32, 56);
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
        //System.out.println(player.getPos().angle + ", " + player.getPos().x + ", " + player.getPos().y);

        camera.position.set(player.getPos().x + player.getWidth()/(float)2.0,
                player.getPos().y + player.getHeight()/(float)2.0, 0);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.enableBlending();
        batch.begin();

        //Draw game world in the background
        worldDraw();
        batch.draw(player.getCurrentSprite(), player.getPos().x, player.getPos().y);
        batch.end();
    }

    private void movementUpdate(){
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

        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            player.setState(player.getStates()[Entity.MISSILE]);
        }
    }

    private void worldDraw() {
        for (LevelTile tile : level.getTiles()) {
            batch.draw(tile.graphic,
                    tile.pos.x * LevelTile.TILE_SIZE,
                    tile.pos.y * LevelTile.TILE_SIZE);
        }
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

    private void loadSprites(WadFile file) {
        //For now, just load player sprites. I'll generalize this later.
        GameSprite.spriteMap.put("PLAY", new GameSprite(file, "PLAY"));
    }

    private void loadStates() {
        EntityState.stateList.add(new EntityState("PLAY", 'A', -1, 0)); //0
        EntityState.stateList.add(new EntityState("PLAY", 'A', 4, 2));  //1
        EntityState.stateList.add(new EntityState("PLAY", 'B', 4, 3));  //2
        EntityState.stateList.add(new EntityState("PLAY", 'C', 4, 4));  //3
        EntityState.stateList.add(new EntityState("PLAY", 'D', 4, 1));  //4
        EntityState.stateList.add(new EntityState("PLAY", 'E', 12, 0)); //5
        EntityState.stateList.add(new EntityState("PLAY", 'F', 6, 5));  //6
        EntityState.stateList.add(new EntityState("PLAY", 'G', 4, 8));  //7
        EntityState.stateList.add(new EntityState("PLAY", 'G', 4, 0));  //8
        EntityState.stateList.add(new EntityState("PLAY", 'H', 10, 10));//9
        EntityState.stateList.add(new EntityState("PLAY", 'I', 10, 11));//10
        EntityState.stateList.add(new EntityState("PLAY", 'J', 10, 12));//11
        EntityState.stateList.add(new EntityState("PLAY", 'K', 10, 13));//12
        EntityState.stateList.add(new EntityState("PLAY", 'L', 10, 14));//13
        EntityState.stateList.add(new EntityState("PLAY", 'M', 10, 15));//14
        EntityState.stateList.add(new EntityState("PLAY", 'N', -1, 15));//15
    }

}
