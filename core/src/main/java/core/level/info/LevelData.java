package core.level.info;

import com.badlogic.gdx.utils.Array;
import net.mtrop.doom.WadFile;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;

public class LevelData {
    private String name = "";
    int levelnumber;
    private ArrayList<LevelTile> tiles = new ArrayList<>();
    private ArrayList<LevelObject> objects = new ArrayList<>();
    private Array<WadFile> wads;

    //New level from scratch
    public LevelData(int levelnumber) {
        name = "Level " + levelnumber;
    }

    //New level from scratch, with a name
    public LevelData(String name, int levelnumber) {
        this.name = name;
        this.levelnumber = levelnumber;
    }

    public int getLevelnumber() {
        return this.levelnumber;
    }

    public LevelData(WadFile file, int levelnumber, Array<WadFile> wads) throws IOException {
        this.levelnumber = levelnumber;
        this.wads = wads;
        String entry = "LEVEL" + levelnumber;
        String leveldata = "";

        try {
            leveldata = file.getTextData(entry, Charset.defaultCharset());
        } catch (IOException e) {
            System.out.println("Could not read entry \"" + entry + "\" from " + file.getFileName() + "!");
            e.printStackTrace();
        }

        Scanner stringReader = new Scanner(leveldata);

        while (stringReader.hasNextLine()) {
            String line = stringReader.nextLine();

            if (line.startsWith("name")) {
                this.name = line.substring(line.indexOf("= ") + 2);
            } else if (line.equals("floortile {")) {
                readTile(stringReader, file);
            } else if (line.equals("object {")) {
                readObject(stringReader);
            } else if (!line.isEmpty()){
                System.out.println("Error: unrecognized level data!");
                throw new IOException();
            }
        }
    }

    public String getName() {
        return name;
    }

    public ArrayList<LevelTile> getTiles() {
        return tiles;
    }

    public LevelTile getTile(int x, int y) {
        for (LevelTile tile : tiles) {
            if (tile.pos.x == x && tile.pos.y == y) {
                return tile;
            }
        }

        return null;
    }

    public ArrayList<LevelObject> getObjects() {
        return objects;
    }

    //Reads a map object from level data
    private void readObject(Scanner stringReader) throws IOException {

        int type = 0, tag = 0;
        float xpos = 0, ypos = 0;
        float angle = 0;
        boolean singleplayer = false, cooperative = false, ambush = false;
        boolean[] skill = {false, false, false, false, false};

        boolean[] objectdata = {false, false, false, false, false, false, false, false,
                false, false, false, false, false};

        String line = stringReader.nextLine();
        String rawvalue = line.substring(line.indexOf("= ") + 2);

        while(!line.equals("}")) {

            if (line.startsWith("type")) {
                type = Integer.parseInt(rawvalue);
                objectdata[0] = true;
            } else if (line.startsWith("xpos")) {
                xpos = Float.parseFloat(rawvalue);
                objectdata[1] = true;
            } else if (line.startsWith("ypos")) {
                ypos = Float.parseFloat(rawvalue);
                objectdata[2] = true;
            } else if (line.startsWith("angle")) {
                angle = Float.parseFloat(rawvalue);
                objectdata[3] = true;
            } else if (line.startsWith("single")) {
                singleplayer = Boolean.parseBoolean(rawvalue);
                objectdata[4] = true;
            } else if (line.startsWith("coop")) {
                cooperative = Boolean.parseBoolean(rawvalue);
                objectdata[5] = true;
            } else if (line.startsWith("skill1")) {
                skill[0] = Boolean.parseBoolean(rawvalue);
                objectdata[6] = true;
            } else if (line.startsWith("skill2")) {
                skill[1] = Boolean.parseBoolean(rawvalue);
                objectdata[7] = true;
            } else if (line.startsWith("skill3")) {
                skill[2] = Boolean.parseBoolean(rawvalue);
                objectdata[8] = true;
            } else if (line.startsWith("skill4")) {
                skill[3] = Boolean.parseBoolean(rawvalue);
                objectdata[9] = true;
            } else if (line.startsWith("skill5")) {
                skill[4] = Boolean.parseBoolean(rawvalue);
                objectdata[10] = true;
            } else if (line.startsWith("ambush")) {
                ambush = Boolean.parseBoolean(rawvalue);
                objectdata[11] = true;
            } else if (line.startsWith("tag")) {
                tag = Integer.parseInt(rawvalue);
                objectdata[12] = true;
            } else {
                System.out.println("Error: unrecognized level object data!");
                throw new IOException();
            }

            line = stringReader.nextLine();
            rawvalue = line.substring(line.indexOf("= ") + 2);
        }

        //Make sure everything necessary was read.
        for (boolean objectdatum : objectdata) {
            if (!objectdatum) {
                System.out.println("Error: incomplete level data!");
                throw new IOException();
            }
        }

        LevelObject obj = new LevelObject(type, xpos, ypos, angle, singleplayer, cooperative, skill, ambush, tag);
        objects.add(obj);
    }

    //Reads a map tile from level data
    private void readTile(Scanner stringReader, WadFile file) throws IOException {

        int xpos = 0, ypos = 0;
        boolean solid = false;
        String graphic = "";
        int light = 0, effect = 0, arg1 = 0, arg2 = 0, tag = 0;
        boolean repeat = false;

        boolean[] leveldata = {false, false, false, false, false, false, false, false, false, false};


        String line = stringReader.nextLine();
        String rawvalue = line.substring(line.indexOf("= ") + 2);

        while(!line.equals("}")) {

            if (line.startsWith("xpos")) {
                xpos = Integer.parseInt(rawvalue);
                leveldata[0] = true;
            } else if (line.startsWith("ypos")) {
                ypos = Integer.parseInt(rawvalue);
                leveldata[1] = true;
            } else if (line.startsWith("solid")) {
                solid = Boolean.parseBoolean(rawvalue);
                leveldata[2] = true;
            } else if (line.startsWith("graphic")) {
                if (rawvalue.length() > 8) {
                    System.out.println("Error: lumpnames cannot exceed 8 characters!");
                    throw new IOException();
                }
                graphic = rawvalue;
                leveldata[3] = true;
            } else if (line.startsWith("light")) {
                light = Integer.parseInt(rawvalue);
                leveldata[4] = true;
            } else if (line.startsWith("effect")) {
                effect = Integer.parseInt(rawvalue);
                leveldata[5] = true;
            } else if (line.startsWith("arg1")) {
                arg1 = Integer.parseInt(rawvalue);
                leveldata[6] = true;
            } else if (line.startsWith("arg2")) {
                arg2 = Integer.parseInt(rawvalue);
                leveldata[7] = true;
            } else if (line.startsWith("repeat")) {
                repeat = Boolean.parseBoolean(rawvalue);
                leveldata[8] = true;
            } else if (line.startsWith("tag")) {
                tag = Integer.parseInt(rawvalue);
                leveldata[9] = true;
            } else {
                System.out.println("Error: unrecognized level tile data!");
                throw new IOException();
            }

            line = stringReader.nextLine();
            rawvalue = line.substring(line.indexOf("= ") + 2);
        }

        //Make sure everything necessary was read.
        for (boolean leveldatum : leveldata) {
            if (!leveldatum) {
                System.out.println("Error: incomplete level data!");
                throw new IOException();
            }
        }

        LevelTile.TilePosition pos = new LevelTile.TilePosition(xpos, ypos);
        LevelTile tile = new LevelTile(pos, solid, graphic, light, effect, arg1, arg2, repeat, tag, wads);
        tiles.add(tile);
    }

    public void setName(String newname) {
        name = newname;
    }

    public String toString() {

        String ret = "";

        for (LevelTile tile : tiles) {
            ret = ret.concat(tile.toString() + "\n");
        }

        for (LevelObject obj : objects) {
            ret = ret.concat(obj.toString() + "\n");
        }

        return ret;
    }
}

