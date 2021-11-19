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
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import core.server.SpaceClient;
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
    static CreateImageButton startButton = new CreateImageButton("buttons/start.png", "buttons/startnobg.png");
    static CreateImageButton coopButton = new CreateImageButton("buttons/coop.png", "buttons/coopnobg.png");
    static CreateImageButton settingsButton = new CreateImageButton("buttons/settings.png", "buttons/settingsnobg.png");
    static CreateImageButton levelEditorButton = new CreateImageButton("buttons/leveleditor.png", "buttons/leveleditornobg.png");
    static CreateImageButton exitButton = new CreateImageButton("buttons/exit.png", "buttons/exitnobg.png");
    static CreateImageButton createLobbyButton = new CreateImageButton("buttons/createlobby.png", "buttons/createlobbynobg.png");
    static CreateImageButton joinLobbyButton = new CreateImageButton("buttons/joinlobby.png", "buttons/joinlobbynobg.png");
    static CreateImageButton backButton = new CreateImageButton("buttons/back.png", "buttons/backnobg.png");
    static Button[] MainMenuButtons = {startButton.button, coopButton.button, settingsButton.button, exitButton.button, levelEditorButton.button};
    static Button[] CoopButtons = {createLobbyButton.button, joinLobbyButton.button, backButton.button};
    TextButton submit = new TextButton("submit", skin);
    TextButton back = new TextButton("back", skin);
    TextField lobbyCode = new TextField("Lobby Code", skin);
    public GameScreen gameScreen;

    public TitleScreen(MyGDxTest myGDxTest) {
        WadFile file;
        profiler = new GLProfiler(Gdx.graphics);
        profiler.enable();
        try {
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
        Gdx.input.setInputProcessor(stage);
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
            myGDxTest.setScreen(gameScreen);
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
        float w = width/2.5f;
        float h = height/7.5f;
        startButton.button.setBounds((Gdx.graphics.getWidth()-w)/ 2f, (Gdx.graphics.getHeight())/ 2f, w, h);
        coopButton.button.setBounds((Gdx.graphics.getWidth() - w)/ 2f, (Gdx.graphics.getHeight() - h*1.8f)/ 2f, w, h);
        settingsButton.button.setBounds((Gdx.graphics.getWidth() - w)/ 2f, (Gdx.graphics.getHeight() - h*3.6f)/ 2f, w, h);
        levelEditorButton.button.setBounds((Gdx.graphics.getWidth() - w)/ 2f, (Gdx.graphics.getHeight() - h*5.4f)/ 2f, w, h);
        exitButton.button.setBounds((Gdx.graphics.getWidth() - w)/ 2f, (Gdx.graphics.getHeight() - h*7.2f)/ 2f, w, h);
        createLobbyButton.button.setBounds((Gdx.graphics.getWidth() - w)/ 2f, Gdx.graphics.getHeight() / 2f, w, h);
        joinLobbyButton.button.setBounds((Gdx.graphics.getWidth() - w)/ 2f, (Gdx.graphics.getHeight() - h*1.8f)/ 2f, w, h);
        backButton.button.setBounds((Gdx.graphics.getWidth() - w)/ 2f, (Gdx.graphics.getHeight() - h*3.6f)/ 2f, w, h);
        lobbyCode.setBounds(((Gdx.graphics.getWidth() - width/5f)/ 2f),((Gdx.graphics.getHeight())/ 2f), width/5f, height/15f);
        submit.setBounds(((Gdx.graphics.getWidth() + width/4.5f)/ 2f),((Gdx.graphics.getHeight())/ 2f),width/6f,height/15f);
        back.setBounds(((Gdx.graphics.getWidth() - width/6f)/ 2f),((Gdx.graphics.getHeight() - height/6.5f)/ 2f),width/6f,height/15f);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        titleScreen.dispose();
    }

    @Override
    public void dispose() {
    }

    private void createMenus(){
        stage.addActor(startButton.button);
        stage.addActor(coopButton.button);
        stage.addActor(settingsButton.button);
        stage.addActor(levelEditorButton.button);
        stage.addActor(exitButton.button);
        stage.addActor(createLobbyButton.button);
        stage.addActor(joinLobbyButton.button);
        stage.addActor(backButton.button);
        setCoopButtonsVisible(false);

        startButton.button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                ChooseDifficultyWindow window = new ChooseDifficultyWindow("Choose Difficulty:", skin, titleScreen, MainMenuButtons);
                window.setBounds(((Gdx.graphics.getWidth() - 150)/ 2f), ((Gdx.graphics.getHeight() - 110) / 2f), 150, 110);
                setMainMenuButtonsVisible(false);
                stage.addActor(window);
            }
        });
        coopButton.button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(NameChangeWindow.playerName.length() == 0){
                    PopupWindow error = new PopupWindow("No Username Found!", skin, "Go to settings and create a username");
                    showPopup(error);
                    error.setPosition((Gdx.graphics.getWidth() - error.getWidth())/ 2f, (Gdx.graphics.getHeight() - error.getHeight())/ 2f);
                    return;
                }
                gameScreen= new GameScreen(null, false, myGDxTest);
                client = new SpaceClient(gameScreen, titleScreen);
                if(client.getMasterClient() == null) {
                    PopupWindow error = new PopupWindow("Connection Error", skin,
                            "Error connecting to the server!\nTry Again Later.");
                    showPopup(error);
                    error.setPosition((Gdx.graphics.getWidth() - error.getWidth())/ 2f, (Gdx.graphics.getHeight() - error.getHeight())/ 2f);
                    coopButton.button.addListener(this);
                    return;
                }
                setMainMenuButtonsVisible(false);
                setCoopButtonsVisible(true);
                gameScreen.client = client;
            }
        });

        backButton.button.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                client.getMasterClient().close();
                setCoopButtonsVisible(false);
                setMainMenuButtonsVisible(true);
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
                setCoopButtonsVisible(false);
                if(lobbyCode.getStage() == null) {
                    stage.addActor(lobbyCode);
                    stage.addActor(submit);
                    stage.addActor(back);
                }
                lobbyCode.setVisible(true);


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
                removeJoinLobbyButtons();
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
                    if(lobbyCode.getStage() == null ) {
                        stage.addActor(lobbyCode);
                        stage.addActor(submit);
                        stage.addActor(back);
                    }
                    PopupWindow error = new PopupWindow("Invalid Lobby", skin, client.validLobby.reason);
                    error.setPosition((Gdx.graphics.getWidth() - error.getWidth())/ 2f, (Gdx.graphics.getHeight() - error.getHeight())/ 2f);
                    showPopup(error);
                }
            }
        });
        back.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y) {
                submit.remove();
                back.remove();
                lobbyCode.remove();
                setCoopButtonsVisible(true);

            }
        });
        settingsButton.button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                titleScreen.dispose();
                SettingsMenu settingsMenu = new SettingsMenu("Choose Difficulty:", skin, stage);
                settingsMenu.setPosition(((Gdx.graphics.getWidth() - settingsMenu.getWidth())/ 2f), ((Gdx.graphics.getHeight() - settingsMenu.getHeight()) / 2f));
                setMainMenuButtonsVisible(false);
                stage.addActor(settingsMenu);
            }
        });

        levelEditorButton.button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                titleScreen.dispose();
                myGDxTest.setScreen(new EditorScreen());
            }
        });

        exitButton.button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                System.exit(0);
            }
        });
    }

    public void showPopup(PopupWindow invalid_lobby) {
        stage.addActor(invalid_lobby);
        invalid_lobby.setPosition(
                (stage.getWidth()/2f) - invalid_lobby.getWidth(),
                (stage.getHeight()/2f) - invalid_lobby.getHeight());
    }

    public void removeJoinLobbyButtons(){
        submit.remove();
        back.remove();
        lobbyCode.remove();
    }

    public static void setCoopButtonsVisible(boolean visible){
        for(Button button : CoopButtons)
            button.setVisible(visible);
    }
    public static void setMainMenuButtonsVisible(boolean visible){
        for(Button button : MainMenuButtons)
            button.setVisible(visible);
    }
}
