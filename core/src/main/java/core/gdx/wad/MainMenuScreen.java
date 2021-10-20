package core.gdx.wad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.removeActor;

public class MainMenuScreen implements Screen {

    public MyGDxTest game;
    SpriteBatch batch;
    OrthographicCamera camera;
    Stage stage;

    ImageButton play;
    ImageButton exit;
    Texture background;
    LobbyServer lobbyServer = new LobbyServer();
    Client client;
    final TextField username;

    Skin uiSkin = new Skin(Gdx.files.internal("uiSkin.json"));

    public MainMenuScreen(MyGDxTest game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        background = new Texture("spaceBackground.png");
        play = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture("play.png"))));
        exit = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture("exit.png"))));
        username = new TextField("", uiSkin);
        username.setMessageText("Enter Username");
        username.setSize(100,25);
        play.setSize(300, 100);
        exit.setSize(300, 100);
        System.out.println(stage.getWidth()+ " " + stage.getHeight());
        play.setPosition(stage.getWidth()/2 - play.getWidth()/2, 250);
        exit.setPosition(stage.getWidth()/2 - exit.getWidth()/2,100);
        username.setPosition(stage.getWidth()/2 - username.getWidth()/2, 350 );
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1F);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Gdx.graphics.getDeltaTime()); //Perform ui logic

        stage.getBatch().begin();
        stage.getBatch().draw(background, 0,0, stage.getWidth(),stage.getHeight  ());
        stage.getBatch().end();
        stage.addActor(play);
        stage.addActor(exit);
        stage.addActor(username);
        stage.draw(); //Draw the ui

        play.addListener(new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
                System.out.println("CLicked");
                Window lobby = new Window("Lobby", uiSkin);
                lobby.setMovable(false);
                TextButton joinLobby = new TextButton("Join Lobby", uiSkin);
                TextButton createLobby = new TextButton("Create Lobby", uiSkin);
                TextButton back = new TextButton("Back", uiSkin);
                lobby.add(createLobby).row();
                lobby.add(joinLobby).row();
                lobby.add(back);
                lobby.setBounds((Gdx.graphics.getWidth() - 400)/ 2, (Gdx.graphics.getHeight() - 200) / 2, 400, 200);
                stage.addActor(lobby);
                back.addListener(new ClickListener(){
                    public void clicked(InputEvent event, float x, float y) {
                        game.setScreen(new MainMenuScreen(game));
                    }
                });
                createLobby.addListener(new ClickListener(){
                    public void clicked(InputEvent event, float x, float y) {

                    }
                });
                joinLobby.addListener(new ClickListener(){
                    public void clicked(InputEvent event, float x, float y) {
                    }
                });
            }
        });

        exit.addListener(new InputListener() {
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.exit();
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
    }

    @Override
    public void resize(int width, int height) {

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
