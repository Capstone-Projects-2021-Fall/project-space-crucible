package editor.write;

import core.level.info.LevelData;
import core.level.info.LevelObject;
import core.level.info.LevelTile;
import net.mtrop.doom.WadFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;

public class LevelWriter {

    private static String writeLevel(LevelData data) {
        String rawLevelData = "";

        rawLevelData += "name = " + data.getName() + "\n";
        rawLevelData += "midi = " + data.getMIDI() + "\n";

        for (LevelTile tile : data.getTiles()) {
            rawLevelData += "floortile {\n"
                    + "xpos = " + tile.pos.x + "\n"
                    + "ypos = " + tile.pos.y + "\n"
                    + "solid = " + tile.solid + "\n"
                    + "graphic = " + tile.graphicname + "\n"
                    + "light = " + tile.light + "\n"
                    + "effect = " + tile.effect + "\n"
                    + "arg1 = " + tile.arg1 + "\n"
                    + "arg2 = " + tile.arg2 + "\n"
                    + "repeat = " + tile.repeat + "\n"
                    + "tag = " + tile.tag + "\n"
                    + "layer = " + tile.pos.layer + "\n"
                    + "bridge = " + tile.bridge + "\n"
                    + "}\n\n";
        }

        for (LevelObject obj : data.getObjects()) {
            rawLevelData += "object {\n"
                    + "type = " + obj.type + "\n"
                    + "xpos = " + obj.xpos + "\n"
                    + "ypos = " + obj.ypos + "\n"
                    + "angle = " + obj.angle + "\n"
                    + "single = " + obj.singleplayer + "\n"
                    + "coop = " + obj.cooperative + "\n"
                    + "skill1 = " + obj.skill[0] + "\n"
                    + "skill2 = " + obj.skill[1] + "\n"
                    + "skill3 = " + obj.skill[2] + "\n"
                    + "skill4 = " + obj.skill[3] + "\n"
                    + "skill5 = " + obj.skill[4] + "\n"
                    + "ambush = " + obj.ambush + "\n"
                    + "tag = " + obj.tag + "\n"
                    + "}\n\n";
        }

        return rawLevelData;
    }

    public static void write(WadFile file, LevelData data, Integer levelnum) throws IOException {

        String rawLevelData = writeLevel(data);

        String levelentry = "LEVEL" + levelnum;

        if (file.getEntry(levelentry) != null) {
            file.replaceEntry(file.lastIndexOf(levelentry), rawLevelData.getBytes(Charset.defaultCharset()));
        } else {
            file.addData(levelentry, rawLevelData.getBytes(Charset.defaultCharset()));
        }
    }

    public static void write(File file, LevelData data) throws IOException {

        String rawLevelData = writeLevel(data);

        Files.writeString(file.toPath(), rawLevelData, StandardOpenOption.WRITE);
    }

}
