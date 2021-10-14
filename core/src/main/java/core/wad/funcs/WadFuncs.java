package core.wad.funcs;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import core.game.entities.PlayerPawn;
import core.game.entities.Worm;
import core.game.logic.*;
import core.level.info.LevelData;
import net.mtrop.doom.WadFile;
import net.mtrop.doom.graphics.PNGPicture;

import java.io.*;

public class WadFuncs {

    //Gets output from WAD graphics and turns it into input for Pixmap
    public static Pixmap lumpToPixmap(WadFile file, String name) {
        //PNGPicture is a type defined by Doomstruct
        PNGPicture p;

        try {
            p = file.getDataAs(name, PNGPicture.class);
        } catch (IOException e) {
            System.out.println("Lump " + name + " does not exist.");
            e.printStackTrace();
            return null;
        }

        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            p.writeBytes(os);
            ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
            return new Pixmap(new Gdx2DPixmap(is, Gdx2DPixmap.GDX2D_FORMAT_RGBA8888));

        } catch (IOException e) {
            System.out.println("Could not convert PNG lump to GDX Pixmap.");
            e.printStackTrace();
            return null;
        }

    }

    public static Texture getTexture(WadFile file, String name) {

        //Picture is a type defined by Doomstruct
        Pixmap pix = lumpToPixmap(file, name);
        return new Texture(pix);
    }

    public static Texture getTexture(Array<WadFile> wads, String name) {
        WadFile file = null;

        for (WadFile w : wads) {
            if (w.contains(name)) {
                file = w;
            }
        }

        return getTexture(file, name);
    }

    public static Sprite getSprite(WadFile file, String name) {
        return new Sprite(getTexture(file, name));
    }

    public static Sprite getSprite(Array<WadFile> wads, String name) {
        return new Sprite(getTexture(wads, name));
    }

    public static LevelData loadLevel(WadFile file, int levelnum, Array<WadFile> wads) {
        try {
            return new LevelData(file, levelnum, wads);
        } catch (IOException e) {
            System.out.println("Could not load LEVEL" + levelnum);
            e.printStackTrace();
            return null;
        }
    }

    public static void setEntityTypes() {
        GameLogic.entityType.add(PlayerPawn.class); // 0
        GameLogic.entityType.add(Worm.class);
    }

    public static void loadStates() {
        GameLogic.stateList.add(new EntityState("PLAY", 'A', -1, 0));   //0
        GameLogic.stateList.add(new EntityState("PLAY", 'A', 8, 2));    //1
        GameLogic.stateList.add(new EntityState("PLAY", 'B', 8, 3));    //2
        GameLogic.stateList.add(new EntityState("PLAY", 'C', 8, 4));    //3
        GameLogic.stateList.add(new EntityState("PLAY", 'D', 8, 1));    //4
        GameLogic.stateList.add(new EntityState("PLAY", 'E', 12, 0));   //5
        GameLogic.stateList.add(new EntityState("PLAY", 'F', 6, 5));    //6
        GameLogic.stateList.add(new EntityState("PLAY", 'G', 4, 8));    //7
        GameLogic.stateList.add(new EntityState("PLAY", 'G', 4, 0));    //8
        GameLogic.stateList.add(new EntityState("PLAY", 'H', 10, 10));  //9
        GameLogic.stateList.add(new EntityState("PLAY", 'I', 10, 11));  //10
        GameLogic.stateList.add(new EntityState("PLAY", 'J', 10, 12));  //11
        GameLogic.stateList.add(new EntityState("PLAY", 'K', 10, 13));  //12
        GameLogic.stateList.add(new EntityState("PLAY", 'L', 10, 14));  //13
        GameLogic.stateList.add(new EntityState("PLAY", 'M', 10, 15));  //14
        GameLogic.stateList.add(new EntityState("PLAY", 'N', -1, 15));  //15
        GameLogic.stateList.add(new EntityState("SARG", 'A', 10, 17));  //16
        GameLogic.stateList.add(new EntityState("SARG", 'B', 10, 16));  //17
        GameLogic.stateList.add(new EntityState("SARG", 'A', 2, 19));   //18
        GameLogic.stateList.add(new EntityState("SARG", 'A', 2, 20));   //19
        GameLogic.stateList.add(new EntityState("SARG", 'B', 2, 21));   //20
        GameLogic.stateList.add(new EntityState("SARG", 'B', 2, 22));   //21
        GameLogic.stateList.add(new EntityState("SARG", 'C', 2, 23));   //22
        GameLogic.stateList.add(new EntityState("SARG", 'C', 2, 24));   //23
        GameLogic.stateList.add(new EntityState("SARG", 'D', 2, 25));   //24
        GameLogic.stateList.add(new EntityState("SARG", 'D', 2, 18));   //25
        GameLogic.stateList.add(new EntityState("SARG", 'E', 8, 27));   //26
        GameLogic.stateList.add(new EntityState("SARG", 'F', 8, 28));   //27
        GameLogic.stateList.add(new EntityState("SARG", 'G', 8, 18));   //28
        GameLogic.stateList.add(new EntityState("SARG", 'H', 2, 30));   //29
        GameLogic.stateList.add(new EntityState("SARG", 'H', 2, 18));   //30
        GameLogic.stateList.add(new EntityState("SARG", 'I', 8, 32));   //31
        GameLogic.stateList.add(new EntityState("SARG", 'J', 8, 33));   //32
        GameLogic.stateList.add(new EntityState("SARG", 'K', 4, 34));   //33
        GameLogic.stateList.add(new EntityState("SARG", 'L', 4, 35));   //34
        GameLogic.stateList.add(new EntityState("SARG", 'M', 4, 36));   //35
        GameLogic.stateList.add(new EntityState("SARG", 'N', -1, 36));
    }

    public static void loadSprites(Array<WadFile> wads) {
        //For now, just load player sprites. I'll generalize this later.
        GameLogic.spriteMap.put("PLAY", new GameSprite(wads, "PLAY"));
        GameLogic.spriteMap.put("SARG", new GameSprite(wads, "SARG"));
    }
}