package core.rcon;

import com.badlogic.gdx.Game;
import com.esotericsoftware.kryonet.Client;

public class RCON extends Game {

    final public static Client client = new Client(8192, 8192);

    @Override
    public void create() {
        setScreen(new RCONScreen());
    }
}
