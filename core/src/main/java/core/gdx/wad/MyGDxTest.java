package core.gdx.wad;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import core.game.entities.actions.StateAction;
import core.game.logic.EntityState;
import core.game.logic.GameLogic;
import core.wad.funcs.EntityFuncs;
import core.wad.funcs.GameSprite;
import core.wad.funcs.SoundFuncs;
import core.wad.funcs.WadFuncs;
import net.mtrop.doom.WadFile;
import org.checkerframework.checker.units.qual.A;
import org.lwjgl.system.CallbackI;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MyGDxTest extends Game {

    public TitleScreen titleScreen;
    public static ArrayList<File> addons = new ArrayList<>(); //Addons has all the files host added
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
        titleScreen = new TitleScreen(this);
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

            for (File f : addons) {

                System.out.println(f.getName());

                try {
                    wads.add(new WadFile(f));
                } catch (IOException e) {System.out.println("Wad " + f.getAbsolutePath() + " not found.");}
            }

            SoundFuncs.loadMIDIs(wads);
            SoundFuncs.loadSounds(wads);
            GameLogic.loadLevels(wads);
            WadFuncs.loadTextures(wads);
            WadFuncs.TITLESCREEN = WadFuncs.getTexture(wads, "TITLESCR");
            WadFuncs.SETTINGSSCREEN = WadFuncs.getTexture(wads, "BLANKSCR");
            WadFuncs.LOBBYSCREEN = WadFuncs.getTexture(wads, "LOBBYSCR");

            //Load prepare all Entity and level logic, open game screen and initiate game loop.
            WadFuncs.loadLevelEffects();
            WadFuncs.loadScripts(wads);

            //Blank state reserved at index 0 for map spot- visible in editor only
            GameLogic.stateList.add(new EntityState("UNKN", 'A', -1, -1, null));

            wads.forEach(w -> {
                if (w.contains("ENTITIES")) {
                    try {
                        EntityFuncs.loadEntityClasses(w.getTextData("ENTITIES", Charset.defaultCharset()));
                    } catch (IOException | EntityFuncs.ParseException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }});

            GameLogic.stateList.forEach(s -> {
                if (!RenderFuncs.spriteMap.containsKey(s.getSprite())) {
                    RenderFuncs.spriteMap.put(s.getSprite(), new GameSprite(wads, s.getSprite()));
                }
            });

            SoundFuncs.playSound("pistol/shoot");

            //When we add add-on support we will also close other files inside of 'wads"
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}