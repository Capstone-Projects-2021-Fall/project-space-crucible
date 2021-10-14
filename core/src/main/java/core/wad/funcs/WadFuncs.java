package core.wad.funcs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.ByteArray;
import core.game.logic.Entity;
import core.game.logic.EntityState;
import core.game.logic.GameLogic;
import core.game.logic.PlayerPawn;
import core.level.info.LevelData;
import net.mtrop.doom.WadFile;
import net.mtrop.doom.graphics.PNGPicture;
import net.mtrop.doom.sound.DMXSound;
import net.mtrop.doom.util.SoundUtils;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.io.*;

import static com.badlogic.gdx.Gdx.audio;

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

    public static Sprite getSprite(WadFile file, String name) {
        return new Sprite(getTexture(file, name));
    }

    public static LevelData loadLevel(WadFile file, int levelnum) {
        try {
            return new LevelData(file, levelnum);
        } catch (IOException e) {
            System.out.println("Could not load LEVEL" + levelnum);
            e.printStackTrace();
            return null;
        }
    }

    public static void setEntityTypes() {
        GameLogic.entityType.add(PlayerPawn.class); // 0
    }

    public static void loadStates() {
        GameLogic.stateList.add(new EntityState("PLAY", 'A', -1, 0)); //0
        GameLogic.stateList.add(new EntityState("PLAY", 'A', 8, 2));  //1
        GameLogic.stateList.add(new EntityState("PLAY", 'B', 8, 3));  //2
        GameLogic.stateList.add(new EntityState("PLAY", 'C', 8, 4));  //3
        GameLogic.stateList.add(new EntityState("PLAY", 'D', 8, 1));  //4
        GameLogic.stateList.add(new EntityState("PLAY", 'E', 12, 0)); //5
        GameLogic.stateList.add(new EntityState("PLAY", 'F', 6, 5));  //6
        GameLogic.stateList.add(new EntityState("PLAY", 'G', 4, 8));  //7
        GameLogic.stateList.add(new EntityState("PLAY", 'G', 4, 0));  //8
        GameLogic.stateList.add(new EntityState("PLAY", 'H', 10, 10));//9
        GameLogic.stateList.add(new EntityState("PLAY", 'I', 10, 11));//10
        GameLogic.stateList.add(new EntityState("PLAY", 'J', 10, 12));//11
        GameLogic.stateList.add(new EntityState("PLAY", 'K', 10, 13));//12
        GameLogic.stateList.add(new EntityState("PLAY", 'L', 10, 14));//13
        GameLogic.stateList.add(new EntityState("PLAY", 'M', 10, 15));//14
        GameLogic.stateList.add(new EntityState("PLAY", 'N', -1, 15));//15
    }

    public static void loadSprites(WadFile file) {
        //For now, just load player sprites. I'll generalize this later.
        GameLogic.spriteMap.put("PLAY", new GameSprite(file, "PLAY"));
    }
}