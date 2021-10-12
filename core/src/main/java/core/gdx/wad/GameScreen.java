package core.gdx.wad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import core.game.logic.Entity;
import core.game.logic.GameLogic;
import core.game.logic.PlayerPawn;
import core.level.info.LevelData;
import core.level.info.LevelTile;

public class GameScreen implements Screen {

    Thread gameLogic;

    //Player and level
    PlayerPawn player;
    LevelData level;


    //screen
    OrthographicCamera camera;
    private final Vector2 mouseInWorld2D = new Vector2();
    private final Vector3 mouseInWorld3D = new Vector3();

    //graphics
    SpriteBatch batch;

    public GameScreen(Thread gameLogic, PlayerPawn player, LevelData level) {
        this.gameLogic = gameLogic;
        this.player = player;
        this.level = level;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);
        batch = new SpriteBatch();
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0,0,0,1F);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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

        for (Entity e : GameLogic.entityList) {
            batch.draw(e.getCurrentSprite(), e.getPos().x, e.getPos().y);
        }

        batch.end();
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
        gameLogic.interrupt();
    }

    @Override
    public void dispose() {
    }
}
