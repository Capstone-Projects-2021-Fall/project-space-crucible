package core.gdx.wad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import core.game.entities.Entity;
import core.game.logic.GameLogic;
import core.game.entities.PlayerPawn;
import core.server.SpaceClient;
import core.server.Network.RenderData;
import core.wad.funcs.SoundFuncs;
import core.server.Network.ClientData;
import core.server.Network.ServerDetails;

public class GameScreen implements Screen {

    Thread gameLoop;
    public SpaceClient client;
    RenderData renderData = new RenderData();
    ClientData clientData = new ClientData();
    ServerDetails serverDetails = new ServerDetails();
    int lobbySize = 2;
    int playerNumber = 1;

    //screen
    OrthographicCamera camera;
    private final Vector2 mouseInWorld2D = new Vector2();
    private final Vector3 mouseInWorld3D = new Vector3();
    ShapeRenderer sr = new ShapeRenderer();
    boolean showBoxes = false;
    boolean isSinglePlayer;
    BitmapFont font = new BitmapFont();


    float angle = 0;

    //graphics
    SpriteBatch batch;
    Stage lobbyStage;
    Texture background = new Texture("spaceBackground.png");
    Skin uiSkin = new Skin(Gdx.files.internal("uiskin.json"));

    public GameScreen(Thread gameLoop, boolean isSinglePlayer) {
        this.gameLoop = gameLoop;
        GameLogic.loadEntities(GameLogic.currentLevel, false);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);
        batch = new SpriteBatch();
        lobbyStage = new Stage();
        this.isSinglePlayer = isSinglePlayer;
//        if(!isSinglePlayer){ //If it is co-op mode create a new client.
//            client = new SpaceClient(this);
//        }
    }

    @Override
    public void show() {
        SoundFuncs.stopMIDI();
        if (isSinglePlayer) {
            gameLoop.start();
        } else {
            playerNumber = client.getClient().getID();
        }
    }

    @Override
    public void render(float delta) {

        if (GameLogic.switchingLevels || GameLogic.getPlayer(1) == null) {return;}

        Gdx.gl.glClearColor(0,0,0,1F);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if(clientData.connected != null) {
            if (!isSinglePlayer && clientData.connected.size() <= lobbySize) {
                lobbyStage.act(Gdx.graphics.getDeltaTime()); //Perform ui logic
                lobbyStage.getBatch().begin();
                lobbyStage.getBatch().draw(background, 0, 0, lobbyStage.getWidth(), lobbyStage.getHeight());
                int x = 100;
                int y = 400;
                for (int element : clientData.connected) {
                    String clientId = "Player " + element;
                    TextButton player = new TextButton(clientId, uiSkin);
                    player.setBounds(x, y, 80, 50);
                    lobbyStage.addActor(player);
                    y -= 50;
                }
                TextButton lobbyCode = new TextButton(serverDetails.lobbyCode, uiSkin);
                lobbyStage.addActor(lobbyCode);
                lobbyStage.getBatch().end();
                lobbyStage.draw(); //Draw the ui
                return;
            }
        }

        getAngle(isSinglePlayer);
        if(isSinglePlayer)
            GameLogic.getPlayer(1).getPos().angle = angle; //Turn the vector2 into a degree angle

        if(isSinglePlayer) {
            camera.position.set(GameLogic.getPlayer(1).getPos().x + GameLogic.getPlayer(1).getWidth() / (float) 2.0,
                    GameLogic.getPlayer(1).getPos().y + GameLogic.getPlayer(1).getHeight() / (float) 2.0, 0);
        } else if(renderData.tiles != null && renderData.entityList != null){
            camera.position.set(getPlayer(playerNumber).getPos().x + getPlayer(playerNumber).getWidth() / (float) 2.0,
                    getPlayer(playerNumber).getPos().y + getPlayer(playerNumber).getHeight() / (float) 2.0, 0);
        }
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.enableBlending();
        batch.begin();
        if(isSinglePlayer) {
            RenderFuncs.worldDraw(batch, GameLogic.currentLevel.getTiles());
            RenderFuncs.entityDraw(batch, GameLogic.entityList);

        }else if(renderData.tiles != null && renderData.entityList != null){
            RenderFuncs.worldDraw(batch, renderData.tiles);
            RenderFuncs.entityDraw(batch, renderData.entityList);
        }

        font.draw(batch,"HP:" +GameLogic.getPlayer(playerNumber).getHealth(), GameLogic.getPlayer(playerNumber).getPos().x, GameLogic.getPlayer(playerNumber).getPos().y);
        batch.end();

        if (showBoxes) {
            showBoxes();
        }

        if (showBoxes) {showBoxes();}

        if (Gdx.input.isKeyJustPressed(Input.Keys.EQUALS)) {
            showBoxes = !showBoxes;
        }
        if(!isSinglePlayer) {
            client.getInput(getControls());
        } else {
            GameLogic.getPlayer(1).controls = getControls();
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

    public void getAngle(boolean isSinglePlayer){
        //This centers the camera to the player
        //Get the angle where the mouse is pointing to on the screen in relation to where the player is
        //Referenced code - https://stackoverflow.com/questions/16381031/get-cursor-position-in-libgdx
        if (isSinglePlayer) {
            mouseInWorld3D.x = Gdx.input.getX() - GameLogic.getPlayer(1).getPos().x;
            mouseInWorld3D.y = Gdx.input.getY() + GameLogic.getPlayer(1).getPos().y;
        } else if(renderData.tiles != null && renderData.entityList != null) {
            mouseInWorld3D.x = Gdx.input.getX() - getPlayer(1).getPos().x;
            mouseInWorld3D.y = Gdx.input.getY() + getPlayer(1).getPos().y;
        }
        mouseInWorld3D.z = 0;
        camera.unproject(mouseInWorld3D); //unprojecting will give game world coordinates matching the pointer's position
        mouseInWorld2D.x = mouseInWorld3D.x;
        mouseInWorld2D.y = mouseInWorld3D.y;
        angle = mouseInWorld2D.angleDeg();
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
        SoundFuncs.stopMIDI();
        SoundFuncs.closeSequencer();
//        System.exit(0);
    }

    @Override
    public void dispose() {
    }

    public float getAngle() {
        return angle;
    }

    public void setRenderData(RenderData object) {
        renderData = object;
    }

    public void setClientData(ClientData object) {
        clientData = object;
    }

    public void setServerDetails(ServerDetails object){ serverDetails = object;}

    private boolean[] getControls() {
        boolean[] controls = new boolean[5];
        controls[GameLogic.UP] = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP);
        controls[GameLogic.DOWN] = Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN);
        controls[GameLogic.LEFT] = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
        controls[GameLogic.RIGHT] = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        controls[GameLogic.SHOOT] = Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);

        return controls;
    }

    private PlayerPawn getPlayer(int tag) {

        for (Entity e : renderData.entityList) {
            if (e instanceof PlayerPawn && e.getTag() == tag) {
                return (PlayerPawn) e;
            }
        }
        return null;
    }
}
