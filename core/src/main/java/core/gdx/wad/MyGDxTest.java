package core.gdx.wad;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import static com.badlogic.gdx.Gdx.audio;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MyGDxTest extends Game {
    public TitleScreen titleScreen;

    @Override
    public void create() {


        titleScreen = new TitleScreen(this);
        setScreen(titleScreen);
    }
}