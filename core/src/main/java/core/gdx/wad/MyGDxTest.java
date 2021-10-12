package core.gdx.wad;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import core.game.logic.Entity;
import core.game.logic.GameLogic;
import core.game.logic.PlayerPawn;

import static com.badlogic.gdx.Gdx.audio;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MyGDxTest extends Game {
    public GameScreen gameScreen;

    @Override
    public void create() {

        gameScreen = new GameScreen();
        setScreen(gameScreen);
    }
}