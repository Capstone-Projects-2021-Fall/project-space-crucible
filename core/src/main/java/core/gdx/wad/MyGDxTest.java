package core.gdx.wad;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import core.game.logic.GameLogic;
import core.wad.funcs.SoundFuncs;
import core.wad.funcs.WadFuncs;
import net.mtrop.doom.WadFile;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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

            SoundFuncs.startSequencer();
            SoundFuncs.loadMIDIs(wads);
            SoundFuncs.loadSounds(wads);
            GameLogic.loadLevels(file, wads);
            WadFuncs.loadSprites(wads);
            WadFuncs.loadTextures(wads);
            SoundFuncs.playSound("pistol/shoot");

            //When we add add-on support we will also close other files inside of 'wads"
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        //Load prepare all Entity logic, open game screen and initiate game loop.
        WadFuncs.loadStates();
        WadFuncs.setEntityTypes();

        SoundFuncs.playMIDI("TITLE");
        titleScreen = new TitleScreen(this, gameLoop);
        setScreen(titleScreen);

    }
}