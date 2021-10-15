package core.gdx.wad;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import core.game.logic.GameLogic;
import core.level.info.LevelData;
import core.wad.funcs.WadFuncs;
import net.mtrop.doom.WadFile;

import java.io.IOException;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MyGDxTest extends Game {
    public GameScreen gameScreen;

    Thread gameLoop = new Thread() {
      @Override
      public void run() {
          GameLogic.start();
      }

      @Override
      public void interrupt() {
          GameLogic.stop();
      }
    };

    @Override
    public void create() {

        LevelData level = null;

        try {
            WadFile file = new WadFile(Gdx.files.internal("assets/resource.wad").file());
            Array<WadFile> wads = new Array<>();
            wads.add(file);
            level = new LevelData(file, 1, wads);
            WadFuncs.loadSprites(wads);
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        WadFuncs.loadStates();
        WadFuncs.setEntityTypes();
        GameLogic.loadEntities(level);
        gameScreen = new GameScreen(gameLoop, level);
        setScreen(gameScreen);
        gameLoop.start();
    }
}