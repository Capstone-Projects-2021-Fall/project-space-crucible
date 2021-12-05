package integration;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.Array;
import core.game.logic.GameLogic;
import core.game.logic.EntityState;
import core.wad.funcs.EntityFuncs;
import core.wad.funcs.WadFuncs;
import net.mtrop.doom.WadFile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class GameLogicTest {

    @Test
    @DisplayName("Testing Game Logic")
    void gameLogicTest() {

        WadFile file, addon;
        Array<WadFile> wads = new Array<>();


        //Open files. We need our two test add-on files and the resource.
        try {
            file = new WadFile("../assets/resource.wad");
            addon = new WadFile("../core/src/test/resources/superbad.wad");
            wads.add(file);
            wads.add(addon);
        } catch (IOException io) {
            fail();
            return;
        }

        //Load Levels
        GameLogic.loadLevels(wads);
        WadFuncs.loadLevelEffects();
        WadFuncs.loadScripts(wads);

        assertEquals(4, GameLogic.levels.size());
        assertEquals(1, GameLogic.scripts.size());

        //Load entities and states
        GameLogic.stateList.add(new EntityState("UNKN", 'A', -1, -1, null));

        wads.forEach(w -> {
            if (w.contains("ENTITIES")) {
                try {
                    EntityFuncs.loadEntityClasses(w.getTextData("ENTITIES", Charset.defaultCharset()));
                } catch (IOException | EntityFuncs.ParseException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        });

        //7 from default + 1 from add-on
        assertEquals(8, GameLogic.entityTable.size());

        GameLogic.currentLevel = GameLogic.levels.get(1);
        GameLogic.loadEntities(GameLogic.currentLevel, false);
        GameLogic.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            fail();
        }

        //Is single player, there should be a player pawn.
        assertEquals(true, GameLogic.isSinglePlayer);
        assertEquals(true, GameLogic.getPlayer(1) != null);

        //Tic counter should be incrementing.
        int tics = GameLogic.ticCounter;
        assertNotEquals(0, tics);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            fail();
        }
        assertNotEquals(tics, GameLogic.ticCounter);

        float x = GameLogic.getPlayer(1).getPos().x;
        float y = GameLogic.getPlayer(1).getPos().y;

        //Move player
        GameLogic.getPlayer(1).controls = new boolean[]{true, false, true, false, false};

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            fail();
        }

        //Position should be different- player should have moved.
        assertNotEquals(x, GameLogic.getPlayer(1).getPos().x);
        assertNotEquals(y, GameLogic.getPlayer(1).getPos().y);

        //Transition to level 2
        GameLogic.readyChangeLevel(GameLogic.levels.get(2));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            fail();
        }

        assertEquals(GameLogic.currentLevel.getLevelnumber(), GameLogic.levels.get(2).getLevelnumber());
        assertEquals(true, GameLogic.isSinglePlayer);
        assertEquals(true, GameLogic.getPlayer(1) != null);

        GameLogic.stop();

        try {
            file.close();
            addon.close();
        } catch (IOException io) {
            fail();
        }
    }
}
