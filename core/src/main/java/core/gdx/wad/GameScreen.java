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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
import core.wad.funcs.WadFuncs;

import java.util.ConcurrentModificationException;
import java.util.Objects;
import java.util.TreeMap;


public class GameScreen implements Screen {

    public static int difficultyLevelSelected = 0;
    Thread gameLoop;
    MyGDxTest myGDxTest;
    public SpaceClient client;
    RenderData renderData = new RenderData();
    ClientData clientData = new ClientData();
    ServerDetails serverDetails = new ServerDetails();
    ChatWindow chatWindow;
    public int playerNumber = 0;

    //screen
    OrthographicCamera camera;
    private final Vector2 mouseInWorld2D = new Vector2();
    private final Vector3 mouseInWorld3D = new Vector3();
    ShapeRenderer sr = new ShapeRenderer();
    ShapeRenderer shapeRenderer = new ShapeRenderer();
    boolean showBoxes = false;
    public boolean isSinglePlayer;
    BitmapFont font = new BitmapFont();
    private Stage stage = new Stage(new ScreenViewport());
    final private Skin skin = new Skin(Gdx.files.internal("assets/uiskin.json"));

    float angle = 0;

    //graphics
    SpriteBatch batch;
    Stage lobbyStage;
    Skin uiSkin = new Skin(Gdx.files.internal("uiskin.json"));
    TextButton play = new TextButton("Start Game", uiSkin);
    TextButton exitToMenu = new TextButton("Exit Lobby", uiSkin);
    TextButton difficultyLevel = new TextButton("Difficulty Level", uiSkin);
    public boolean startGame = false;
    Label lobbyCode;
    boolean remove = false;
    int ping;
    public int updatePing = 0;
    TreeMap<String, Button> playerbuttons = new TreeMap<>();
    DeadPlayerWindow deadPlayerWindow;

    public GameScreen(Thread gameLoop, boolean isSinglePlayer, MyGDxTest myGdxTest) {
        this.gameLoop = gameLoop;
        this.myGDxTest = myGdxTest;
        GameLogic.loadEntities(GameLogic.currentLevel, false);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);
        batch = new SpriteBatch();
        lobbyStage = new Stage();
        this.isSinglePlayer = isSinglePlayer;
        play.setBounds((Gdx.graphics.getWidth() - 100) / 2f, 50, 100, 60);
        difficultyLevel.setBounds((Gdx.graphics.getWidth() + 250) / 2f, 120, 110, 60);
    }

    @Override
    public void show() {
        SoundFuncs.stopMIDI();
        if (isSinglePlayer) {
            playerNumber = 1;
            gameLoop.start();
        } else {
            playerNumber = clientData.idToPlayerNum.indexOf(client.getGameClient().getID());
            System.out.println("I am player " + playerNumber);
            if (playerNumber == 1) {
                System.out.println("Sending .WAD data...");
                client.sendLevels();
                client.sendEntities();
                System.out.println("Done!");
            }
        }
        if(!isSinglePlayer) {
            if(!startGame) {
                Gdx.input.setInputProcessor(lobbyStage);
                addExitButton();
                if (playerNumber == 1) {
//                    play.setBounds((Gdx.graphics.getWidth() - 100) / 2f, 50, 100, 60);
                    lobbyStage.addActor(play);
                    play.addListener(new ClickListener() {
                        public void clicked(InputEvent event, float x, float y) {
                            startGame = true;
                            Network.StartGame startGame = new Network.StartGame();
                            startGame.startGame = true;
                            startGame.difficultyLevel = difficultyLevelSelected;
                            client.getGameClient().sendTCP(startGame);
                            play.removeListener(this);
                        }
                    });
                    lobbyStage.addActor(difficultyLevel);
                    difficultyLevel.addListener(new ClickListener() {
                        public void clicked(InputEvent event, float x, float y) {
                            ChooseDifficultyWindow window = new ChooseDifficultyWindow("Choose Difficulty:", skin, null, null, false);
                            window.setBounds(((Gdx.graphics.getWidth() - 150)/ 2f), ((Gdx.graphics.getHeight() - 110) / 2f), 150, 110);
                            lobbyStage.addActor(window);
                        }
                    });
                }
            } else {
                addChatWindow();
            }
        }
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
                //RenderFuncs.entityDraw(batch, GameLogic.entityList);
                if(GameLogic.getPlayer(playerNumber).getHealth()>0){
                    font.draw(batch,"HP:" +GameLogic.getPlayer(playerNumber).getHealth(),
                            GameLogic.getPlayer(playerNumber).getPos().x,
                            GameLogic.getPlayer(playerNumber).getPos().y);
                }else{
                    font.draw(batch,"HP:0",
                            GameLogic.getPlayer(playerNumber).getPos().x,
                            GameLogic.getPlayer(playerNumber).getPos().y);
                }
                font.draw(batch, "Layer:" + GameLogic.getPlayer(playerNumber).currentLayer, GameLogic.getPlayer(playerNumber).getPos().x, GameLogic.getPlayer(playerNumber).getPos().y-10);
                font.draw(batch, "Bridge:" + GameLogic.getPlayer(playerNumber).bridgeLayer, GameLogic.getPlayer(playerNumber).getPos().x, GameLogic.getPlayer(playerNumber).getPos().y-20);
                font.draw(batch, NameChangeWindow.playerName,
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
            GameLogic.getPlayer(1).controls = getControls();

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

            if (!startGame) {
                lobbyStage.act(Gdx.graphics.getDeltaTime()); //Perform ui logic
                lobbyStage.getBatch().begin();
                lobbyStage.getBatch().draw(WadFuncs.LOBBYSCREEN, 0, 0, lobbyStage.getWidth(), lobbyStage.getHeight());
                int x = 100;
                int y = 400;

                updatePlayerNumber();
                playerbuttons.forEach((k, v) -> v.remove());
                for (String name : clientData.playerNames.values()) {
                    Button player = new TextButton(name, uiSkin);
                    player.setBounds(x, y, 80, 50);
                    lobbyStage.addActor(player);
                    y -= 50;
                    playerbuttons.put(name, player);
                }

                if (serverDetails.lobbyCode != null && !remove) {
                    if (client.getGameClient().getID() == 1) {
                        lobbyCode = new Label("Lobby Code\n" + serverDetails.lobbyCode + "\nRCON Pass:\n" + serverDetails.rconPass, uiSkin);
                    } else {
                        lobbyCode = new Label("Lobby Code\n" + serverDetails.lobbyCode, uiSkin);
                    }
                    lobbyStage.addActor(lobbyCode);
                    remove = true;
                }
                lobbyStage.getBatch().end();
                lobbyStage.draw(); //Draw the ui
                batch.end();
                return;
            }
            if (renderData.entityList == null)  {
                batch.end();
                return;
            }
            if (getPlayer(playerNumber) == null) {
                batch.end();
                return;
            }
            getAngle(false);

            if (GameLogic.currentLevel == null) {
                batch.end();
                return;
            }
            try {
                if(deadPlayerWindow == null){
                    deadPlayerWindow = new DeadPlayerWindow("Press enter to hide", skin, myGDxTest, stage, this);
                    deadPlayerWindow.setPosition((Gdx.graphics.getWidth() - deadPlayerWindow.getWidth()) / 2f, (Gdx.graphics.getHeight() - deadPlayerWindow.getHeight()) / 2f);

                }
                camera.position.set(getPlayer(playerNumber).getPos().x + getPlayer(playerNumber).getWidth() / (float) 2.0,
                        getPlayer(playerNumber).getPos().y + getPlayer(playerNumber).getHeight() / (float) 2.0, 0);
                camera.update();
                RenderFuncs.worldDraw(batch, GameLogic.currentLevel.getTiles(), false, false, renderData.entityList, getPlayer(playerNumber));

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
                font.draw(batch, "Ping: " + ping, getPlayer(playerNumber).getPos().x, getPlayer(playerNumber).getPos().y-13);

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
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        drawMiniMap();
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
        camera.unproject(mouseInWorld3D); //unprojecting will give game world coordinates matching the pointer's position
        mouseInWorld2D.x = mouseInWorld3D.x;
        mouseInWorld2D.y = mouseInWorld3D.y;
        angle = mouseInWorld2D.angleDeg();
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        stage.getViewport().update(width, height, true);
        lobbyStage.getViewport().update(width, height, true);
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
    }

    @Override
    public void dispose() {
        client.getGameClient().stop();
        System.exit(0);
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

    private Network.CameraData getCameraData() {
        Network.CameraData newCameraData = new Network.CameraData();
        newCameraData.width = camera.viewportWidth;
        newCameraData.hight = camera.viewportHeight;
        Vector3 bottomleft = new Vector3();
        bottomleft.x = camera.position.x - camera.viewportWidth/2;
        bottomleft.y = camera.position.y - camera.viewportHeight/2;
        bottomleft.z = 0;
        newCameraData.camerapositon = bottomleft;
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
        chatWindow = new ChatWindow("Chat", skin, this, stage, myGDxTest);
        stage.addActor(chatWindow);
        chatWindow.setBounds(Gdx.graphics.getWidth(), 0, chatWindow.getWidth(),chatWindow.getHeight());
//        chatWindow.setPosition(camera.viewportWidth,0);
//        chatWindow.setSize(chatWindow.getWidth(), chatWindow.getHeight());
    }

    private void addExitButton(){
        exitToMenu.setBounds((Gdx.graphics.getWidth() + 250) / 2f, 50, 100, 60);
        lobbyStage.addActor(exitToMenu);
        exitToMenu.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                System.out.println("exit");
                client.getGameClient().close();
                client.getMasterClient().close();
                myGDxTest.setScreen(myGDxTest.titleScreen);
                TitleScreen.setCoopButtonsVisible(false);
                TitleScreen.setMainMenuButtonsVisible(true);
            }
        });
    }

    public void addChatToWindow(Network.ChatMessage chat) {
        chatWindow.addToChatLog(chat.sender + ": " + chat.message);
    }

    public void updatePlayerNumber() {
        playerNumber = clientData.idToPlayerNum.indexOf(client.getGameClient().getID());
        if(!startGame && playerNumber == 1 && !lobbyStage.getActors().contains(play, true) && !lobbyStage.getActors().contains(difficultyLevel, true)){
            lobbyStage.addActor(play);
            play.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    startGame = true;
                    Network.StartGame startGame = new Network.StartGame();
                    startGame.startGame = true;
                    startGame.difficultyLevel = difficultyLevelSelected;
                    client.getGameClient().sendTCP(startGame);
                    play.removeListener(this);
                }
            });
            lobbyStage.addActor(difficultyLevel);
            difficultyLevel.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    ChooseDifficultyWindow window = new ChooseDifficultyWindow("Choose Difficulty:", skin, null, null, false);
                    window.setBounds(((Gdx.graphics.getWidth() - 150)/ 2f), ((Gdx.graphics.getHeight() - 110) / 2f), 150, 110);
                    lobbyStage.addActor(window);
                    window.removeListener(this);
                }
            });
        }
//        System.out.println("My playernumber is " + playerNumber);
    }

    public void setPing(int returnTripTime) {
        ping = returnTripTime;
    }

    private void drawMiniMap() {
        float miniSquareWidth = camera.viewportWidth/200;
        float miniSquareHeight = miniSquareWidth;
        float drawMiniX = 0;
        float drawMiniY = 345;
        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        float mapSpacing =4;
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