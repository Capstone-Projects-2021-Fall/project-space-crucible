package core.gdx.wad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import core.game.entities.Entity;
import core.game.entities.PlayerPawn;
import core.game.logic.GameLogic;
import core.level.info.LevelTile;
import core.server.Network;
import core.server.Network.ClientData;
import core.server.Network.RenderData;
import core.server.Network.ServerDetails;
import core.server.SpaceClient;
import core.wad.funcs.SoundFuncs;

import java.util.ConcurrentModificationException;
import java.util.Objects;

public class GameScreen implements Screen {

    Thread gameLoop;
    MyGDxTest myGDxTest;
    public SpaceClient client;
    RenderData renderData = new RenderData();
    ClientData clientData = new ClientData();
    ServerDetails serverDetails = new ServerDetails();
    ChatWindow chatWindow;
    public int playerNumber;

    //screen
    OrthographicCamera camera;
    OrthographicCamera hudCamera;
    private final Vector2 mouseInWorld2D = new Vector2();
    private final Vector3 mouseInWorld3D = new Vector3();
    ShapeRenderer sr = new ShapeRenderer();
    ShapeRenderer shapeRenderer = new ShapeRenderer();
    boolean showBoxes = false;
    public boolean isSinglePlayer;
    BitmapFont font = new BitmapFont();
    private final Stage stage = new Stage(new ScreenViewport());
    final private Skin skin = new Skin(Gdx.files.internal("assets/uiskin.json"));

    float angle = 0;

    //graphics
    SpriteBatch batch;
    int ping;
    public int updatePing = 0;
    DeadPlayerWindow deadPlayerWindow;

    public GameScreen(Thread gameLoop, boolean isSinglePlayer, MyGDxTest myGdxTest) {
        this.gameLoop = gameLoop;
        this.myGDxTest = myGdxTest;
        this.isSinglePlayer = isSinglePlayer;
        GameLogic.loadEntities(GameLogic.currentLevel, false);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);
        hudCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        hudCamera.position.set(hudCamera.viewportWidth / 2.0f, hudCamera.viewportHeight / 2.0f, 1.0f);
        batch = new SpriteBatch();
        if(!isSinglePlayer){
            chatWindow = new ChatWindow("Chat", skin, this, stage);
        }
    }

    @Override
    public void show() {
        SoundFuncs.stopMIDI();
        if (isSinglePlayer) {
            playerNumber = 1;
            gameLoop.start();
        } else {
              playerNumber = clientData.idToPlayerNum.indexOf(client.getGameClient().getID());
        }
        if(!isSinglePlayer)
            addChatWindow();
    }

    @Override
    public void render(float delta) {

        if (GameLogic.switchingLevels || GameLogic.getPlayer(1) == null) {return;}

        Gdx.gl.glClearColor(0,0,0,1F);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);



        batch.setProjectionMatrix(camera.combined);
        batch.enableBlending();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.begin();



        if(isSinglePlayer){
            Gdx.input.setInputProcessor(stage);
            if(deadPlayerWindow == null){
                deadPlayerWindow = new DeadPlayerWindow("Press enter to hide", skin, myGDxTest, stage, this);
                deadPlayerWindow.setPosition((Gdx.graphics.getWidth() - deadPlayerWindow.getWidth()) / 2f, (Gdx.graphics.getHeight() - deadPlayerWindow.getHeight()) / 2f);
            }
            getAngle(true);
            GameLogic.getPlayer(1).getPos().angle = angle; //Turn the vector2 into a degree angle
            camera.position.set(GameLogic.getPlayer(1).getPos().x + GameLogic.getPlayer(1).getWidth() / (float) 2.0,
                    GameLogic.getPlayer(1).getPos().y + GameLogic.getPlayer(1).getHeight() / (float) 2.0, 0);
            camera.update();

            try {
                RenderFuncs.worldDraw(batch, GameLogic.currentLevel.getTiles(), false, false, GameLogic.entityList, GameLogic.getPlayer(1));
                drawHealth();//TODO test this and the other in multiplayer
//                if(GameLogic.getPlayer(playerNumber).getHealth()>0){
//                    font.draw(batch,"HP:" +GameLogic.getPlayer(playerNumber).getHealth(),
//                            GameLogic.getPlayer(playerNumber).getPos().x,
//                            GameLogic.getPlayer(playerNumber).getPos().y);
//                }else{
//                    font.draw(batch,"HP:0",
//                            GameLogic.getPlayer(playerNumber).getPos().x,
//                            GameLogic.getPlayer(playerNumber).getPos().y);
//                }
                //TODO: re-add tags for layer and bridge before pr
                //font.draw(batch, "Layer:" + GameLogic.getPlayer(playerNumber).currentLayer, GameLogic.getPlayer(playerNumber).getPos().x, GameLogic.getPlayer(playerNumber).getPos().y-10);
                //font.draw(batch, "Bridge:" + GameLogic.getPlayer(playerNumber).bridgeLayer, GameLogic.getPlayer(playerNumber).getPos().x, GameLogic.getPlayer(playerNumber).getPos().y-20);
                font.draw(batch, TitleScreen.playerName,
                        GameLogic.getPlayer(playerNumber).getPos().x,
                        GameLogic.getPlayer(playerNumber).getPos().y + GameLogic.getPlayer(playerNumber).getHeight() + 10);
                if (showBoxes) {
                    showBoxes();
                }
                if(GameLogic.getPlayer(playerNumber).getHealth()<=0) {
                    stage.addActor(deadPlayerWindow);
                }
                if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
                    stage.addActor(deadPlayerWindow);
                }
                if(Gdx.input.isTouched() || Gdx.input.isKeyPressed(Input.Keys.ENTER)){
                    deadPlayerWindow.remove();
                }
            } catch (ConcurrentModificationException cme) {
                batch.end();
                return;
            }
            Objects.requireNonNull(GameLogic.getPlayer(1)).controls = getControls();

        }else { //If co-op mode
            if (clientData.connected == null)  {
                batch.end();
                return;
            }
            //The ping interval
            if(updatePing == 0) {
                updatePing = 50;
                client.getGameClient().updateReturnTripTime();
            }
            updatePing--;

            if (renderData.entityList == null)  {
                batch.end();
                return;
            }
            if (getPlayer(playerNumber) == null) {
                batch.end();
                return;
            }
            if (GameLogic.currentLevel == null) {
                batch.end();
                return;
            }
            try {
                if(deadPlayerWindow == null){
                    deadPlayerWindow = new DeadPlayerWindow("Press enter to hide", skin, myGDxTest, stage, this);
                    deadPlayerWindow.setPosition((Gdx.graphics.getWidth() - deadPlayerWindow.getWidth()) / 2f, (Gdx.graphics.getHeight() - deadPlayerWindow.getHeight()) / 2f);
                }
                getAngle(false);
                camera.position.set(getPlayer(playerNumber).getPos().x + getPlayer(playerNumber).getWidth() / (float) 2.0,
                        getPlayer(playerNumber).getPos().y + getPlayer(playerNumber).getHeight() / (float) 2.0, 0);
                camera.update();
                RenderFuncs.worldDraw(batch, GameLogic.currentLevel.getTiles(), false, false, renderData.entityList, getPlayer(playerNumber));
                drawHealth();
                int playerSize = clientData.idToPlayerNum.size();
                for(int player = 1; player < playerSize; player++){
                    int playerId = clientData.idToPlayerNum.get(player);
                    if(getPlayer(player) != null) {
                        if(clientData.playerNames.get(playerId) == null){
                            font.draw(batch, "Bot",
                                    Objects.requireNonNull(getPlayer(player)).getPos().x,
                                    Objects.requireNonNull(getPlayer(player)).getPos().y + Objects.requireNonNull(getPlayer(player)).getHeight() + 10);
                        }else {
                            font.draw(batch, clientData.playerNames.get(playerId),
                                    Objects.requireNonNull(getPlayer(player)).getPos().x,
                                    Objects.requireNonNull(getPlayer(player)).getPos().y + Objects.requireNonNull(getPlayer(player)).getHeight() + 10);
                        }
                        if (getPlayer(player).getHealth() > 0) {
                            font.draw(batch, "HP:" + getPlayer(player).getHealth(),
                                    getPlayer(player).getPos().x,
                                    getPlayer(player).getPos().y);
                        } else {
                            font.draw(batch, "HP: 0",
                                    getPlayer(player).getPos().x,
                                    getPlayer(player).getPos().y);
                        }
                    }
                    if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
                        stage.addActor(deadPlayerWindow);
                    }
                    if(Gdx.input.isTouched() || Gdx.input.isKeyPressed(Input.Keys.ENTER)){
                        deadPlayerWindow.remove();
                    }
                }
                if (showBoxes) {
                    showBoxes();
                }
                client.getInput(getControls());
                client.getCameraData(getCameraData());
            }
            catch (NullPointerException | ConcurrentModificationException e) {
                try {batch.end();} catch(NullPointerException ignored){}
                return;
            }
        }
        if (showBoxes) {showBoxes();}

        if (Gdx.input.isKeyJustPressed(Input.Keys.EQUALS)) {
            showBoxes = !showBoxes;
        }
        batch.end();

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 0, hudCamera.viewportHeight);
        if(!isSinglePlayer) {
            font.draw(batch, "Ping: " + ping, 0, hudCamera.viewportHeight - 13);
            font.draw(batch, "Lobby Code " + serverDetails.lobbyCode, 0, font.getLineHeight());
        }
        batch.end();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        hudCamera.update();


        drawMiniMap();
    }

    private void drawHealth() {
        if (isSinglePlayer) {
            for (Entity entity : GameLogic.entityList) {
                if (entity.getHeight() > 1) {
                    if (entity.getHealth() > 0) {
                        font.draw(batch, "HP:" + entity.getHealth(),
                                entity.getPos().x,
                                entity.getPos().y);

                    }
                }
            }
        }else{
            for(Entity entity: renderData.entityList) {
                if (entity.getHeight() > 1) {
                    if (entity.getHealth() > 0) {
                        font.draw(batch, "HP:" + entity.getHealth(),
                                entity.getPos().x,
                                entity.getPos().y);
                    }
                }
            }
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
            mouseInWorld3D.x = Gdx.input.getX() - GameLogic.getPlayer(1).getPos().x - GameLogic.getPlayer(1).getWidth()/2f;
            mouseInWorld3D.y = Gdx.input.getY() + GameLogic.getPlayer(1).getPos().y + GameLogic.getPlayer(1).getHeight()/2f;
        } else if(renderData.entityList != null && getPlayer(playerNumber) != null) {
            mouseInWorld3D.x = Gdx.input.getX() - getPlayer(playerNumber).getPos().x - getPlayer(playerNumber).getWidth()/2f;
            mouseInWorld3D.y = Gdx.input.getY() + getPlayer(playerNumber).getPos().y + getPlayer(playerNumber).getHeight()/2f;
        }
        mouseInWorld3D.z = 0;
        camera.unproject(mouseInWorld3D); //unproject will give game world coordinates matching the pointer's position
        mouseInWorld2D.x = mouseInWorld3D.x;
        mouseInWorld2D.y = mouseInWorld3D.y;
        angle = mouseInWorld2D.angleDeg();
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        stage.getViewport().update(width, height, true);
        if(chatWindow != null){
            chatWindow.setBounds(Gdx.graphics.getWidth(), 0, width/2f, height/3.1f);
        }
        if(deadPlayerWindow != null)
            deadPlayerWindow.setPosition((Gdx.graphics.getWidth() - deadPlayerWindow.getWidth()) / 2f, (Gdx.graphics.getHeight() - deadPlayerWindow.getHeight()) / 2f);
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
        try {GameLogic.stop();} catch (NullPointerException ignored){}
        stage.dispose();
        batch.dispose();
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

    public void setServerDetails(Network.ServerDetails object){ serverDetails = object;}


    private boolean[] getControls() {
        boolean[] controls = new boolean[5];
        controls[GameLogic.UP] = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP);
        controls[GameLogic.DOWN] = Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN);
        controls[GameLogic.LEFT] = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
        controls[GameLogic.RIGHT] = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        controls[GameLogic.SHOOT] = Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);

        return controls;
    }

    private Network.CameraData getCameraData() {
        Network.CameraData newCameraData = new Network.CameraData();
        newCameraData.width = camera.viewportWidth;
        newCameraData.hight = camera.viewportHeight;
        Vector3 bottomLeft = new Vector3();
        bottomLeft.x = camera.position.x - camera.viewportWidth/2;
        bottomLeft.y = camera.position.y - camera.viewportHeight/2;
        bottomLeft.z = 0;
        newCameraData.camerapositon = bottomLeft;
        return newCameraData;
    }

    private PlayerPawn getPlayer(int tag) {

        for (Entity e : renderData.entityList) {
            if (e instanceof PlayerPawn) {
                if (e.getTag() == tag) {
                    return (PlayerPawn) e;
                }
            }
        }
        return null;
    }

    public void addChatWindow() {
        Gdx.input.setInputProcessor(stage);
        chatWindow.setBounds(Gdx.graphics.getWidth(), 0, chatWindow.getWidth(),chatWindow.getHeight());
        stage.addActor(chatWindow);
    }

    public void addChatToWindow(Network.ChatMessage chat) {
        chatWindow.addToChatLog(chat.sender + ": " + chat.message);
    }

    public void setPing(int returnTripTime) {
        ping = returnTripTime;
    }

    private void drawMiniMap() {
        //chatLog.setPosition(((Gdx.graphics.getWidth() - chatLog.getWidth())/ 2f), ((Gdx.graphics.getHeight() - chatLog.getHeight()) / 2f));
        float miniSquareWidth = Gdx.graphics.getWidth()/200f;
        float miniSquareHeight = miniSquareWidth;
        float drawMiniX = 0;
        float drawMiniY = camera.viewportHeight - camera.viewportHeight/3;
        //float drawMiniY = Gdx.graphics.getHeight() - Gdx.graphics.getHeight()/3; //apparently does the same thing as above
        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        //float mapSpacing = 4;
        float mapSpacing = Gdx.graphics.getWidth()/200f;
        for(LevelTile levelTile : GameLogic.currentLevel.getTiles()){
            shapeRenderer.rect(levelTile.pos.x*mapSpacing+drawMiniX+ camera.viewportWidth/12,
                    levelTile.pos.y*mapSpacing+drawMiniY+ camera.viewportHeight/7, miniSquareWidth,miniSquareHeight,
                    Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY);
            if(levelTile.solid) {
                shapeRenderer.rect(levelTile.pos.x*mapSpacing+drawMiniX+ camera.viewportWidth/12,
                        levelTile.pos.y*mapSpacing+drawMiniY+ camera.viewportHeight/7, miniSquareWidth,miniSquareHeight,
                        Color.RED, Color.RED, Color.RED, Color.RED);
            }
            if(isSinglePlayer) {
                if (!levelTile.solid && levelTile.pos.x == (int) GameLogic.getPlayer(playerNumber).getPos().x / LevelTile.TILE_SIZE &&
                        levelTile.pos.y == (int) GameLogic.getPlayer(playerNumber).getPos().y / LevelTile.TILE_SIZE) {
                    shapeRenderer.rect(levelTile.pos.x * mapSpacing + drawMiniX + camera.viewportWidth / 12,
                            levelTile.pos.y * mapSpacing + drawMiniY + camera.viewportHeight / 7, miniSquareWidth, miniSquareHeight,
                            Color.BLUE, Color.BLUE, Color.BLUE, Color.BLUE);
                }
            }else{
                if (!levelTile.solid && levelTile.pos.x == (int) getPlayer(playerNumber).getPos().x / LevelTile.TILE_SIZE &&
                        levelTile.pos.y == (int) getPlayer(playerNumber).getPos().y / LevelTile.TILE_SIZE) {
                    shapeRenderer.rect(levelTile.pos.x * mapSpacing + drawMiniX + camera.viewportWidth / 12,
                            levelTile.pos.y * mapSpacing + drawMiniY + camera.viewportHeight / 7, miniSquareWidth, miniSquareHeight,
                            Color.BLUE, Color.BLUE, Color.BLUE, Color.BLUE);
                }
            }
        }
        shapeRenderer.end();
    }
}