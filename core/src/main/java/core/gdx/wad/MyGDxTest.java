package core.gdx.wad;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Array;
import core.game.logic.GameLogic;
import core.wad.funcs.EntityFuncs;
import core.wad.funcs.SoundFuncs;
import core.wad.funcs.WadFuncs;
import net.mtrop.doom.WadFile;
import org.checkerframework.checker.units.qual.A;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MyGDxTest extends Game {

    public TitleScreen titleScreen;
    public GameScreen gameScreen;
    public SettingsScreen settingsScreen;
    public static ArrayList<String> addonPaths = new ArrayList<>();
    public static ArrayList<String> addonList = new ArrayList<>();
    public static ArrayList<String> addonHashes = new ArrayList<>();

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

        loadWADS();

        SoundFuncs.startSequencer();
        SoundFuncs.playMIDI("TITLE");
        titleScreen = new TitleScreen(this, gameLoop);
        setScreen(titleScreen);

    }

    public static void loadWADS() {
        //Throw an exception if you cannot read the .WAD file
        try {

            //Read the default .WAD. "wads" will eventually be used to store any loaded mods as well as the base .WAD.
            //We only read the .WAD once and take all the information that we need.
            WadFile file = new WadFile(Gdx.files.internal("assets/resource.wad").file());
            Array<WadFile> wads = new Array<>();
            wads.add(file);

            for (String s : addonPaths) {

                System.out.println(s.substring(s.lastIndexOf("/")+1));

                try {
                    wads.add(new WadFile(s));
                } catch (IOException e) {System.out.println("Wad " + s + " not found.");}
            }

            SoundFuncs.loadMIDIs(wads);
            SoundFuncs.loadSounds(wads);
            GameLogic.loadLevels(wads);
            WadFuncs.loadSprites(wads);
            WadFuncs.loadTextures(wads);
            WadFuncs.TITLESCREEN = WadFuncs.getTexture(wads, "TITLESCR");
            WadFuncs.SETTINGSSCREEN = WadFuncs.getTexture(wads, "BLANKSCR");
            WadFuncs.LOBBYSCREEN = WadFuncs.getTexture(wads, "LOBBYSCR");

            //Load prepare all Entity and level logic, open game screen and initiate game loop.
            WadFuncs.loadLevelEffects();
            WadFuncs.loadStates();
            WadFuncs.setEntityTypes();

            SoundFuncs.playSound("pistol/shoot");

            //When we add add-on support we will also close other files inside of 'wads"
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (EntityFuncs.ParseException ex) {
            System.out.println("Error parsing ENTITIES on Line " + EntityFuncs.getLine());
            System.exit(1);
        }
    }
}