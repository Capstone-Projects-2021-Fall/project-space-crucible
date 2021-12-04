package integration;

import com.badlogic.gdx.utils.Array;
import core.game.logic.GameLogic;
import core.game.entities.Entity;
import core.game.logic.EntityState;
import core.wad.funcs.EntityFuncs;
import core.gdx.wad.MyGDxTest;
import core.gdx.wad.RenderFuncs;
import core.wad.funcs.GameSprite;
import core.wad.funcs.WadFuncs;
import net.mtrop.doom.WadFile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.*;

public class WadFuncsTest {

    @Test
    @DisplayName("Test Wad Functions")
    void wadFuncsTest() {

        WadFile file;
        //First, get WadFile object in the first place
        try {
            file = new WadFile("../assets/resource.wad");
        } catch (IOException io) {
            fail();
            return;
        }

        //Load all levels and entities
        System.out.println(file.getFileName());
        Array<WadFile> wads = new Array<>();
        wads.add(file);

        //Levels
        GameLogic.loadLevels(wads);
        WadFuncs.loadLevelEffects();
        WadFuncs.loadScripts(wads);

        //Make sure 4 levels are read
        assertEquals(4, GameLogic.levels.size(), "Should load 5 levels from resource.wad");

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

        //Make sure 7 entities are read
        assertEquals(7, GameLogic.entityTable.size(), "Should load 7 entity definitions from resource.wad");

        //Make sure EntitySpawners are loaded
        assertEquals("CruciblePlayer", GameLogic.entityTable.get("CruciblePlayer").getClassName());
        Entity worm = GameLogic.entityTable.get("Worm").spawnEntity(new Entity.Position(0, 0, 0), 0, 0, false);
        assertEquals(150, worm.getHealth());

        try {
            file.close();
        } catch (IOException io) {
            fail();
        }
    }
}
