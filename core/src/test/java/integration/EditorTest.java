package integration;

import com.badlogic.gdx.utils.Array;
import net.mtrop.doom.WadFile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import core.level.info.LevelData;
import core.level.info.LevelTile;
import core.level.info.LevelObject;
import core.level.info.LevelTile.TilePosition;
import editor.copy.CopiedThingData;
import editor.copy.CopiedTileData;
import editor.write.LevelWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class EditorTest {

    @Test
    @DisplayName("Test Editor Functions")
    void editorFuncsTest() {
        WadFile resource;
        WadFile file;
        WadFile newFile;
        Array<WadFile> resources = new Array<>();
        File soloFile;
        LevelData wadData, lmpData;

        //Open files. We need our two test add-on files and the resource.
        try {
            resource = new WadFile("../assets/resource.wad");
            file = new WadFile("../core/src/test/resources/superbad.wad");
            soloFile = new File("../core/src/test/resources/LEVEL3.lmp");
            newFile = WadFile.createWadFile("../core/src/test/resources/newtest.wad");
            assertTrue(soloFile.exists());
        } catch (IOException io) {
            fail();
            return;
        }

        resources.add(resource);
        resources.add(file);

        try {
            wadData = new LevelData(file, 1);
            lmpData = new LevelData(Files.readString(soloFile.toPath()));
        } catch (IOException e) {
            fail();
            return;
        }

        //Make sure level data is accurate.
        assertEquals("SUPERBAD", wadData.name);
        assertEquals(406, wadData.getTiles().size());


        //Make sure level data is accurate.
        assertEquals("THE THIRD IMPACT", lmpData.name);
        assertEquals(68, lmpData.getTiles().size());

        //Add tiles and things to map
        wadData.getTiles().add(new LevelTile(new TilePosition(2, 2, 0), true, "NUKAGE1", 255, 0, 0, 0, false, 0, 0));
        wadData.getObjects().add(new LevelObject(1, 3f, 3f, 90f, true, true, new boolean[]{true, true, true, true, true}, false, 0, 0));

        //Add levels to new WAD, including from LMP
        try {
            LevelWriter.write(newFile, wadData, 1);
            LevelWriter.write(newFile, lmpData, 2); //Copy standalone level to .WAD;
        } catch (IOException io) {
            fail();
            return;
        }

        //Level 1 should be modified, but level 2 should be the same.
        try {
            assertNotEquals(new LevelData(file, 1).toString(), new LevelData(newFile, 1).toString());
            assertEquals(new LevelData(Files.readString(soloFile.toPath())).toString(), new LevelData(newFile, 2).toString());
        } catch(IOException io) {
            fail();
            return;
        }

        try {
            resource.close();
            file.close();
            newFile.close();
        } catch (IOException io) {
            fail();
        }
    }

}
