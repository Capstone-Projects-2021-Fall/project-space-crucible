package core.gdx.wad;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import core.game.entities.Entity;
import core.game.logic.CollisionLogic;
import core.game.logic.GameLogic;
import core.game.entities.PlayerPawn;
import core.level.info.LevelData;
import core.wad.funcs.MIDIFuncs;

public class GameScreen implements Screen {

    Thread gameLoop;

    //screen
    OrthographicCamera camera;
    private final Vector2 mouseInWorld2D = new Vector2();
    private final Vector3 mouseInWorld3D = new Vector3();
    ShapeRenderer sr = new ShapeRenderer();
    boolean showBoxes = false;

    //graphics
    SpriteBatch batch;

    public GameScreen(Thread gameLoop) {
        this.gameLoop = gameLoop;
        GameLogic.loadEntities(GameLogic.currentLevel);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);
        batch = new SpriteBatch();
    }

    @Override
    public void show() {
        gameLoop.start();
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0,0,0,1F);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (GameLogic.switchingLevels || GameLogic.getPlayer(0) == null) {return;}

        //This centers the camera to the player
        //Get the angle where the mouse is pointing to on the screen in relation to where the player is
        //Referenced code - https://stackoverflow.com/questions/16381031/get-cursor-position-in-libgdx
        mouseInWorld3D.x = Gdx.input.getX() - GameLogic.getPlayer(0).getPos().x;
        mouseInWorld3D.y = Gdx.input.getY() + GameLogic.getPlayer(0).getPos().y;
        mouseInWorld3D.z = 0;
        camera.unproject(mouseInWorld3D); //unprojecting will give game world coordinates matching the pointer's position
        mouseInWorld2D.x = mouseInWorld3D.x;
        mouseInWorld2D.y = mouseInWorld3D.y;
        GameLogic.getPlayer(0).getPos().angle = mouseInWorld2D.angleDeg(); //Turn the vector2 into a degree angle
        //System.out.println(player.getPos().angle + ", " + player.getPos().x + ", " + player.getPos().y);

        camera.position.set(GameLogic.getPlayer(0).getPos().x + GameLogic.getPlayer(0).getWidth()/(float)2.0,
                GameLogic.getPlayer(0).getPos().y + GameLogic.getPlayer(0).getHeight()/(float)2.0, 0);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.enableBlending();
        batch.begin();
        RenderFuncs.worldDraw(batch, GameLogic.currentLevel);
        RenderFuncs.entityDraw(batch);
        batch.end();

        if (showBoxes) {
            showBoxes();
        }

        if (showBoxes) {showBoxes();}

        if (Gdx.input.isKeyJustPressed(Input.Keys.EQUALS)) {
            showBoxes = !showBoxes;
        }
    }

    private void showBoxes() {
        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Line);
        for (Entity e : GameLogic.entityList) {
            sr.rect(e.getBounds().x, e.getBounds().y, e.getBounds().width, e.getBounds().height);
        }
        sr.end();
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
        gameLoop.interrupt();
        MIDIFuncs.stopMIDI();
        MIDIFuncs.closeSequencer();
    }

    @Override
    public void dispose() {
    }
}
