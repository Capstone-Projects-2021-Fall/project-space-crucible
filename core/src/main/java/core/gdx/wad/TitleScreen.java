package core.gdx.wad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import core.config.Config;
import core.server.SpaceClient;
import core.wad.funcs.SoundFuncs;
import core.wad.funcs.WadFuncs;
import editor.launch.EditorScreen;
import net.mtrop.doom.WadFile;

import java.io.IOException;

public class TitleScreen implements Screen {
    public static boolean changeScreen;
    OrthographicCamera camera;
    SpriteBatch batch;
    Texture texture;
    private final Stage stage = new Stage(new ScreenViewport());
    final private Skin skin = new Skin(Gdx.files.internal("assets/uiskin.json"));
    public static boolean update = false;
    private final GLProfiler profiler;

    public MyGDxTest myGDxTest;
    SpaceClient client;
    final TitleScreen titleScreen = this;
    public GameScreen gameScreen;
    public LobbyScreen lobbyScreen;
    public static Table mainMenuTable;
    public static Table coopMenuTable;
    public static Table joinLobbyTable;
    ChooseDifficultyWindow difficultyWindow = new ChooseDifficultyWindow("Choose Difficulty:", skin, titleScreen, true);
    PopupWindow error;
    SettingsMenu settingsMenu;
    Config config = new Config();
    public static String playerName="Player";
    public static float sfx = 50;
    public static float bgm = 50;

    public TitleScreen(MyGDxTest myGDxTest) {
        WadFile file;
        profiler = new GLProfiler(Gdx.graphics);
        profiler.enable();
        SoundFuncs.startSequencer();
        try {
            if(Config.file.exists()){
                if(config.getText("name") != null)
                    TitleScreen.playerName = config.getText("name");
                System.out.println(TitleScreen.playerName);
                if(config.getText("sfx") != null)
                    TitleScreen.sfx = Float.parseFloat(config.getText("sfx"));
                if(config.getText("bgm") != null)
                    TitleScreen.bgm = Float.parseFloat(config.getText("bgm"));
            }
            settingsMenu = new SettingsMenu("Choose Difficulty:", skin, stage);
            file = new WadFile(Gdx.files.internal("assets/resource.wad").file());
            this.myGDxTest=myGDxTest;
            camera = new OrthographicCamera();
            camera.setToOrtho(false, 1920, 1080);
            batch = new SpriteBatch();
            texture = WadFuncs.TITLESCREEN; //title screen
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        createMenus();
    }

    @Override
    public void show() {
        //Play lobby music
        SoundFuncs.playMIDI("TITLE");
        SoundSettings.handleBGM(TitleScreen.bgm);
        SoundSettings.handleSFX(TitleScreen.sfx);

        Gdx.input.setInputProcessor(stage);
        if(client != null){
            try {
                client.getGameClient().dispose();
                client.getMasterClient().dispose();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void render(float delta) {
        profiler.reset();
        if(update){
            System.out.println("loading wadfile");
            MyGDxTest.loadWADS();
            update = false;
        }
        if(changeScreen){
//            myGDxTest.setScreen(gameScreen);
            myGDxTest.setScreen(lobbyScreen);
            coopMenuTable.setVisible(false);
            changeScreen = false;
        }
        Gdx.gl.glClearColor(0,0,0,1F);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.enableBlending();
        //drawing sprite background

        stage.act(Gdx.graphics.getDeltaTime());
        batch.begin();
        batch.draw(texture, 0, 0, camera.viewportWidth, camera.viewportHeight);
        batch.end();
        stage.draw();
//        float drawCalls = profiler.getDrawCalls();
//        float textureBinds = profiler.getTextureBindings();
//        System.out.println(drawCalls);
//        System.out.println(textureBinds);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        settingsMenu.setPosition(((Gdx.graphics.getWidth() - settingsMenu.getWidth())/ 2f), ((Gdx.graphics.getHeight() - settingsMenu.getHeight()) / 2f));
        settingsMenu.resize();
        difficultyWindow.setBounds(((Gdx.graphics.getWidth() - 150)/ 2f), ((Gdx.graphics.getHeight() - 110) / 2f), 150, 110);
        if(error != null)
            error.setPosition((Gdx.graphics.getWidth() - error.getWidth())/ 2f, (Gdx.graphics.getHeight() - error.getHeight())/ 2f);
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

    private void createMenus(){
        mainMenuTable = new Table();
        mainMenuTable.setFillParent(true);
        mainMenuTable.bottom().padBottom(20);
        coopMenuTable = new Table();
        coopMenuTable.setFillParent(true);
        coopMenuTable.center().padTop(50);
        joinLobbyTable = new Table();
        joinLobbyTable.setFillParent(true);
        joinLobbyTable.center();

        CreateImageButton startButton = new CreateImageButton("buttons/start.png", "buttons/startnobg.png");
        CreateImageButton coopButton = new CreateImageButton("buttons/coop.png", "buttons/coopnobg.png");
        CreateImageButton settingsButton = new CreateImageButton("buttons/settings.png", "buttons/settingsnobg.png");
        CreateImageButton levelEditorButton = new CreateImageButton("buttons/leveleditor.png", "buttons/leveleditornobg.png");
        CreateImageButton exitButton = new CreateImageButton("buttons/exit.png", "buttons/exitnobg.png");
        CreateImageButton createLobbyButton = new CreateImageButton("buttons/createlobby.png", "buttons/createlobbynobg.png");
        CreateImageButton joinLobbyButton = new CreateImageButton("buttons/joinlobby.png", "buttons/joinlobbynobg.png");
        CreateImageButton backButton = new CreateImageButton("buttons/back.png", "buttons/backnobg.png");
        TextButton submit = new TextButton("submit", skin);
        TextButton back = new TextButton("back", skin);
        TextField lobbyCode = new TextField("Lobby Code", skin);

        startButton.button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                mainMenuTable.setVisible(false);
                stage.addActor(difficultyWindow);
            }
        });
        coopButton.button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(TitleScreen.playerName.length() == 0){
                    error = new PopupWindow("No Username Found!", skin, "Go to settings and create a username");
                    showPopup(error);
                    error.setPosition((Gdx.graphics.getWidth() - error.getWidth())/ 2f, (Gdx.graphics.getHeight() - error.getHeight())/ 2f);
                    return;
                }
                gameScreen= new GameScreen(null, false, myGDxTest);
                lobbyScreen = new LobbyScreen(myGDxTest, gameScreen);
                client = new SpaceClient(lobbyScreen, gameScreen, titleScreen);
                if(client.getMasterClient() == null) {
                    error = new PopupWindow("Connection Error", skin,
                            "Error connecting to the server!\nTry Again Later.");
                    showPopup(error);
                    error.setPosition((Gdx.graphics.getWidth() - error.getWidth())/ 2f, (Gdx.graphics.getHeight() - error.getHeight())/ 2f);
                    coopButton.button.addListener(this);
                    return;
                }
                mainMenuTable.setVisible(false);
                coopMenuTable.setVisible(true);
                gameScreen.client = client;
            }
        });

        backButton.button.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                client.getMasterClient().close();
                coopMenuTable.setVisible(false);
                mainMenuTable.setVisible(true);
            }
        });

        createLobbyButton.button.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                client.makeLobby();
            }
        });
        joinLobbyButton.button.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                coopMenuTable.setVisible(false);
                joinLobbyTable.setVisible(true);
            }
        });

        lobbyCode.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                lobbyCode.setText("");
            }
        });
        submit.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y) {
                String lCode = lobbyCode.getText();
                lCode = lCode.toUpperCase();
                client.sendLobbyCode(lCode);
                joinLobbyTable.setVisible(false);
                System.out.println("Waiting...");
                synchronized (titleScreen){
                    try {
                        titleScreen.wait(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Done waiting.");
                if(!client.validLobby.valid) {
                    System.out.println("Invalid.");
                    joinLobbyTable.setVisible(true);
                    error = new PopupWindow("Invalid Lobby", skin, client.validLobby.reason);
                    showPopup(error);
                    error.setPosition((Gdx.graphics.getWidth() - error.getWidth())/ 2f, (Gdx.graphics.getHeight() - error.getHeight())/ 2f);
                }
            }
        });
        back.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y) {
                joinLobbyTable.setVisible(false);
                coopMenuTable.setVisible(true);
            }
        });
        settingsButton.button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                stage.addActor(settingsMenu);
                mainMenuTable.setVisible(false);
            }
        });

        levelEditorButton.button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                titleScreen.dispose();
                myGDxTest.setScreen(new EditorScreen(myGDxTest, titleScreen));
            }
        });

        exitButton.button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                System.exit(0);
            }
        });

        mainMenuTable.add(startButton.button).width(Value.percentWidth(.35f, mainMenuTable)).height(Value.percentHeight(.13f, mainMenuTable));
        mainMenuTable.row();
        mainMenuTable.add(coopButton.button).width(Value.percentWidth(.35f, mainMenuTable)).height(Value.percentHeight(.13f, mainMenuTable));
        mainMenuTable.row();
        mainMenuTable.add(levelEditorButton.button).width(Value.percentWidth(.35f, mainMenuTable)).height(Value.percentHeight(.133f, mainMenuTable));
        mainMenuTable.row();
        mainMenuTable.add(settingsButton.button).width(Value.percentWidth(.35f, mainMenuTable)).height(Value.percentHeight(.13f, mainMenuTable));
        mainMenuTable.row();
        mainMenuTable.add(exitButton.button).width(Value.percentWidth(.35f, mainMenuTable)).height(Value.percentHeight(.13f, mainMenuTable));
        mainMenuTable.pack();
        stage.addActor(mainMenuTable);

        coopMenuTable.add(createLobbyButton.button).width(Value.percentWidth(.35f, coopMenuTable)).height(Value.percentHeight(.13f, coopMenuTable));
        coopMenuTable.row();
        coopMenuTable.add(joinLobbyButton.button).width(Value.percentWidth(.35f, coopMenuTable)).height(Value.percentHeight(.13f, coopMenuTable));
        coopMenuTable.row();
        coopMenuTable.add(backButton.button).width(Value.percentWidth(.35f, coopMenuTable)).height(Value.percentHeight(.13f, coopMenuTable));
        coopMenuTable.pack();
        stage.addActor(coopMenuTable);
        coopMenuTable.setVisible(false);

        joinLobbyTable.add(lobbyCode).padRight(10).width(Value.percentWidth(.2f, joinLobbyTable)).height(Value.percentHeight(.08f, joinLobbyTable));
        joinLobbyTable.add(submit).width(Value.percentWidth(.1f, joinLobbyTable)).height(Value.percentHeight(.08f, joinLobbyTable));
        joinLobbyTable.row();
        joinLobbyTable.add(back).padTop(10).width(Value.percentWidth(.1f, joinLobbyTable)).height(Value.percentHeight(.08f, joinLobbyTable));
        joinLobbyTable.pack();
        stage.addActor(joinLobbyTable);
        joinLobbyTable.setVisible(false);


    }

    public void showPopup(PopupWindow invalid_lobby) {
        stage.addActor(invalid_lobby);
        invalid_lobby.setPosition(
                (stage.getWidth()/2f) - invalid_lobby.getWidth(),
                (stage.getHeight()/2f) - invalid_lobby.getHeight());
    }
}
