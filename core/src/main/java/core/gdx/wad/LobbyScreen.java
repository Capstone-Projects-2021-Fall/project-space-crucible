package core.gdx.wad;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import core.server.Network;
import core.server.SpaceServer;
import core.server.Network.RenderData;

import java.awt.font.TextLayout;

import static core.game.logic.GameLogic.isSinglePlayer;

public class LobbyScreen implements Screen {

    public MyGDxTest game;
    Stage stage;
    Thread gameLoop;
    Texture background;
    TitleScreen titleScreen;
    GlyphLayout lobbyIp;
    BitmapFont font;
    RenderData renderData = new RenderData();
    GameScreen gameScreen;

    public LobbyScreen(GameScreen gameScreen){
        this.gameScreen= gameScreen;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        background = new Texture("spaceBackground.png");
        font = new BitmapFont();
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
        stage.getBatch().draw(background, 0,0, stage.getWidth(),stage.getHeight());
//        font.draw(stage.getBatch(), lobbyIp, 550, 40);
        if(renderData != null){
            game.setScreen(gameScreen);
        }
        //If all players have connected set the screen to game screen.
        System.out.println(SpaceServer.connected.size());
        stage.getBatch().end();
        stage.draw(); //Draw the ui
    }
    public void createPlayerButton(){

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
