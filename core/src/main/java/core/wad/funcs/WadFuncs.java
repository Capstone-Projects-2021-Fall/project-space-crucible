package core.wad.funcs;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.ByteArray;
import core.level.info.LevelData;
import net.mtrop.doom.WadFile;
import net.mtrop.doom.graphics.PNGPicture;

import javax.imageio.ImageIO;
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
            return new Pixmap(new Gdx2DPixmap(is, Gdx2DPixmap.GDX2D_FORMAT_RGB565));

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
}