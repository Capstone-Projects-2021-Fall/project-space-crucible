package core.gdx.wad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import core.server.Network;
import core.wad.funcs.SoundFuncs;
import core.wad.funcs.WadFuncs;

import java.util.TreeMap;

public class LobbyScreen implements Screen {

    MyGDxTest myGDxTest;
    GameScreen gameScreen;
    public static int difficultyLevelSelected = 0;
    SpriteBatch batch;
    Stage lobbyStage;
    Skin skin;
    TextureAtlas atlas;
    Label lobbyCode;
    boolean remove = false;
    TreeMap<String, Button> playerbuttons = new TreeMap<>();
    TextButton exitToMenu;
    TextButton play;
    public boolean startGame = false;
    TextButton difficultyLevel;
    public int playerNumber = 0;



    public LobbyScreen(MyGDxTest myGDxTest, GameScreen gameScreen) {
        this.myGDxTest = myGDxTest;
        this.gameScreen = gameScreen;

        atlas = new TextureAtlas("uiskin.atlas");
        skin = new Skin(Gdx.files.internal("uiskin.json"), atlas);
        batch = new SpriteBatch();
        lobbyStage = new Stage(new ScreenViewport(), batch);
        exitToMenu = new TextButton("Exit Lobby", skin);
        play = new TextButton("Start Game", skin);
        difficultyLevel = new TextButton("Difficulty Level", skin);
        play.setBounds((Gdx.graphics.getWidth() - 100) / 2f, 50, 100, 60);
        difficultyLevel.setBounds((Gdx.graphics.getWidth() + 250) / 2f, 120, 110, 60);
}

    @Override
    public void show() {
        SoundFuncs.stopMIDI();
        playerNumber = gameScreen.clientData.idToPlayerNum.indexOf(gameScreen.client.getGameClient().getID());
        System.out.println("I am player " + playerNumber);
        if (playerNumber == 1) {
            System.out.println("Sending .WAD data...");
            gameScreen.client.sendLevels();
            gameScreen.client.sendEntities();
            System.out.println("Done!");
        }

        Gdx.input.setInputProcessor(lobbyStage);
        addExitButton();
        if (playerNumber == 1) {
            lobbyStage.addActor(play);
            play.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    startGame = true;
                    Network.StartGame startGame = new Network.StartGame();
                    startGame.startGame = true;
                    startGame.difficultyLevel = difficultyLevelSelected;
                    gameScreen.client.getGameClient().sendTCP(startGame);
                    myGDxTest.setScreen(gameScreen);
                    play.removeListener(this);
                }
            });
            lobbyStage.addActor(difficultyLevel);
            difficultyLevel.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    ChooseDifficultyWindow window = new ChooseDifficultyWindow("Choose Difficulty:", skin,null, false);
                    window.setBounds(((Gdx.graphics.getWidth() - 150)/ 2f), ((Gdx.graphics.getHeight() - 110) / 2f), 150, 110);
                    lobbyStage.addActor(window);
                }
            });
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1F);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (gameScreen.clientData.connected == null)
            return;

        if(startGame)
            myGDxTest.setScreen(gameScreen);

        lobbyStage.act(Gdx.graphics.getDeltaTime()); //Perform ui logic
        lobbyStage.getBatch().begin();
        lobbyStage.getBatch().draw(WadFuncs.LOBBYSCREEN, 0, 0, lobbyStage.getWidth(), lobbyStage.getHeight());
        int x = 100;
        int y = 400;

        updatePlayerNumber();
        playerbuttons.forEach((k, v) -> v.remove());
        for (String name : gameScreen.clientData.playerNames.values()) {
            Button player = new TextButton(name, skin);
            player.setBounds(x, y, 80, 50);
            lobbyStage.addActor(player);
            y -= 50;
            playerbuttons.put(name, player);
        }

        if (gameScreen.serverDetails.lobbyCode != null && !remove) {
            if (gameScreen.client.getGameClient().getID() == 1) {
                lobbyCode = new Label("Lobby Code\n" + gameScreen.serverDetails.lobbyCode + "\nRCON Pass:\n" + gameScreen.serverDetails.rconPass, skin);
            } else {
                lobbyCode = new Label("Lobby Code\n" + gameScreen.serverDetails.lobbyCode, skin);
            }
            lobbyStage.addActor(lobbyCode);
            remove = true;
        }
        lobbyStage.getBatch().end();
        lobbyStage.draw(); //Draw the ui
    }

    @Override
    public void resize(int width, int height) {
        lobbyStage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
//        dispose();
    }

    @Override
    public void dispose() {
        lobbyStage.dispose();
        batch.dispose();
        skin.dispose();
        atlas.dispose();
    }


    private void addExitButton(){
        exitToMenu.setBounds((Gdx.graphics.getWidth() + 250) / 2f, 50, 100, 60);
        lobbyStage.addActor(exitToMenu);
        exitToMenu.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                System.out.println("exit");
                myGDxTest.setScreen(myGDxTest.titleScreen);
                TitleScreen.coopMenuTable.setVisible(false);
                TitleScreen.mainMenuTable.setVisible(true);
            }
        });
    }

    public void updatePlayerNumber() {
        playerNumber = gameScreen.clientData.idToPlayerNum.indexOf(gameScreen.client.getGameClient().getID());
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
                    gameScreen.client.getGameClient().sendTCP(startGame);
                    myGDxTest.setScreen(gameScreen);
                    play.removeListener(this);
                }
            });
            lobbyStage.addActor(difficultyLevel);
            difficultyLevel.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    ChooseDifficultyWindow window = new ChooseDifficultyWindow("Choose Difficulty:", skin,null, false);
                    window.setBounds(((Gdx.graphics.getWidth() - 150)/ 2f), ((Gdx.graphics.getHeight() - 110) / 2f), 150, 110);
                    lobbyStage.addActor(window);
                    window.removeListener(this);
                }
            });
        }
    }

}


