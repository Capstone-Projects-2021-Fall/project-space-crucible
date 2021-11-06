package core.rcon;

import com.badlogic.gdx.Game;

public class RCON extends Game {

    @Override
    public void create() {
        setScreen(new RCONScreen());
    }
}
