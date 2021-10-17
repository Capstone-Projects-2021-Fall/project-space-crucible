package core.gdx.wad;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import core.game.logic.GameLogic;
import core.level.info.LevelData;
import core.wad.funcs.MIDIFuncs;
import core.wad.funcs.WadFuncs;
import net.mtrop.doom.WadFile;

import java.io.IOException;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MyGDxTest extends Game {

    public TitleScreen titleScreen;
    public GameScreen gameScreen;

    //This is the thread that runs the Game Logic. It is separate from the rendering code.
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

        //Throw an exception if you cannot read the .WAD file
        try {

            //Read the default .WAD. "wads" will eventually be used to store any loaded mods as well as the base .WAD.
            //We only read the .WAD once and take all the information that we need.
            WadFile file = new WadFile(Gdx.files.internal("assets/resource.wad").file());
            Array<WadFile> wads = new Array<>();
            wads.add(file);

            //Load all of the level data and the graphics before closing the .WAD
            MIDIFuncs.startSequencer();
            MIDIFuncs.loadMIDIs(wads);
            GameLogic.loadLevels(file, wads);
            WadFuncs.loadSprites(wads);

            //When we add add-on support we will also close other files inside of 'wads"
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        //Load prepare all Entity logic, open game screen and initiate game loop.
        WadFuncs.loadStates();
        WadFuncs.setEntityTypes();

        titleScreen = new TitleScreen(this, gameLoop);
        setScreen(titleScreen);
//        gameScreen = new GameScreen(gameLoop);
//        setScreen(gameScreen);
        gameLoop.start();
    }
}