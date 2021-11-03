package core.wad.funcs;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import core.game.entities.*;
import core.game.entities.actions.*;
import core.game.logic.*;
import core.game.logic.tileactions.T_ChangeLevel;
import core.game.logic.tileactions.T_SlimeDamage;
import core.gdx.wad.RenderFuncs;
import core.level.info.LevelData;
import net.mtrop.doom.WadFile;
import net.mtrop.doom.graphics.PNGPicture;

import java.io.*;

public class WadFuncs {

    public static Texture TITLESCREEN;
    public static Texture SETTINGSSCREEN;
    public static Texture LOBBYSCREEN;

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
        GameLogic.entityType.add(Worm.class); // 1
        GameLogic.entityType.add(Serpentipede.class); //2
        GameLogic.entityType.add(Zombieman.class); //3
    }

    public static void loadStates() {
        GameLogic.stateList.add(new EntityState("PLAY", 'A', -1, 0, null));   //0
        GameLogic.stateList.add(new EntityState("PLAY", 'A', 8, 2, null));    //1
        GameLogic.stateList.add(new EntityState("PLAY", 'B', 8, 3, null));    //2
        GameLogic.stateList.add(new EntityState("PLAY", 'C', 8, 4, null));    //3
        GameLogic.stateList.add(new EntityState("PLAY", 'D', 8, 1, null));    //4
        GameLogic.stateList.add(new EntityState("PLAY", 'E', 12, 0, null));   //5
        GameLogic.stateList.add(new EntityState("PLAY", 'F', 6, 5, null));    //6
        GameLogic.stateList.add(new EntityState("PLAY", 'G', 4, 8, null));    //7
        GameLogic.stateList.add(new EntityState("PLAY", 'G', 4, 0, new A_Pain()));    //8
        GameLogic.stateList.add(new EntityState("PLAY", 'H', 10, 10, null));  //9
        GameLogic.stateList.add(new EntityState("PLAY", 'I', 10, 11, new A_Scream()));  //10
        GameLogic.stateList.add(new EntityState("PLAY", 'J', 10, 12, null));  //11
        GameLogic.stateList.add(new EntityState("PLAY", 'K', 10, 13, null));  //12
        GameLogic.stateList.add(new EntityState("PLAY", 'L', 10, 14, new A_Fall()));  //13
        GameLogic.stateList.add(new EntityState("PLAY", 'M', 10, 15, null));  //14
        GameLogic.stateList.add(new EntityState("PLAY", 'N', -1, 15, null));  //15
        GameLogic.stateList.add(new EntityState("SARG", 'A', 10, 17, new A_Look()));  //16
        GameLogic.stateList.add(new EntityState("SARG", 'B', 10, 16, new A_Look()));  //17
        GameLogic.stateList.add(new EntityState("SARG", 'A', 2, 19, new A_Chase()));   //18
        GameLogic.stateList.add(new EntityState("SARG", 'A', 2, 20, new A_Chase()));   //19
        GameLogic.stateList.add(new EntityState("SARG", 'B', 2, 21, new A_Chase()));   //20
        GameLogic.stateList.add(new EntityState("SARG", 'B', 2, 22, new A_Chase()));   //21
        GameLogic.stateList.add(new EntityState("SARG", 'C', 2, 23, new A_Chase()));   //22
        GameLogic.stateList.add(new EntityState("SARG", 'C', 2, 24, new A_Chase()));   //23
        GameLogic.stateList.add(new EntityState("SARG", 'D', 2, 25, new A_Chase()));   //24
        GameLogic.stateList.add(new EntityState("SARG", 'D', 2, 18, new A_Chase()));   //25
        GameLogic.stateList.add(new EntityState("SARG", 'E', 8, 27, new A_FaceTarget()));   //26
        GameLogic.stateList.add(new EntityState("SARG", 'F', 8, 28, new A_FaceTarget()));   //27
        GameLogic.stateList.add(new EntityState("SARG", 'G', 8, 18, new A_MeleeAttack(25)));   //28
        GameLogic.stateList.add(new EntityState("SARG", 'H', 2, 30, null));   //29
        GameLogic.stateList.add(new EntityState("SARG", 'H', 2, 18, new A_Pain()));   //30
        GameLogic.stateList.add(new EntityState("SARG", 'I', 8, 32, null));   //31
        GameLogic.stateList.add(new EntityState("SARG", 'J', 8, 33, new A_Scream()));   //32
        GameLogic.stateList.add(new EntityState("SARG", 'K', 4, 34, null));   //33
        GameLogic.stateList.add(new EntityState("SARG", 'L', 4, 35, new A_Fall()));   //34
        GameLogic.stateList.add(new EntityState("SARG", 'M', 4, 36, null));   //35
        GameLogic.stateList.add(new EntityState("SARG", 'N', -1, 36, null));  //36
        GameLogic.stateList.add(new EntityState("BAL1", 'A', 4, 38, null));  //37
        GameLogic.stateList.add(new EntityState("BAL1", 'B', 4, 37, null));  //38
        GameLogic.stateList.add(new EntityState("BAL1", 'C', 4, 40, null));  //39
        GameLogic.stateList.add(new EntityState("BAL1", 'D', 4, 41, null));  //40
        GameLogic.stateList.add(new EntityState("BAL1", 'E', 4, -1, null));  //41
        GameLogic.stateList.add(new EntityState("BLUD", 'C', 6, 43, null));  //42
        GameLogic.stateList.add(new EntityState("BLUD", 'B', 6, 44, null));  //43
        GameLogic.stateList.add(new EntityState("BLUD", 'A', 6, -1, null));  //44
        GameLogic.stateList.add(new EntityState("PUFF", 'A', 4, 46, null));  //45
        GameLogic.stateList.add(new EntityState("PUFF", 'B', 4, 47, null));  //46
        GameLogic.stateList.add(new EntityState("PUFF", 'C', 4, 48, null));  //47
        GameLogic.stateList.add(new EntityState("PUFF", 'D', 4, -1, null));  //48
        GameLogic.stateList.add(new EntityState("TROO", 'A', 10, 50, new A_Look()));  //49
        GameLogic.stateList.add(new EntityState("TROO", 'B', 10, 49, new A_Look()));  //50
        GameLogic.stateList.add(new EntityState("TROO", 'A', 3, 52, new A_Chase()));   //51
        GameLogic.stateList.add(new EntityState("TROO", 'A', 3, 53, new A_Chase()));   //52
        GameLogic.stateList.add(new EntityState("TROO", 'B', 3, 54, new A_Chase()));   //53
        GameLogic.stateList.add(new EntityState("TROO", 'B', 3, 55, new A_Chase()));   //54
        GameLogic.stateList.add(new EntityState("TROO", 'C', 3, 56, new A_Chase()));   //55
        GameLogic.stateList.add(new EntityState("TROO", 'C', 3, 57, new A_Chase()));   //56
        GameLogic.stateList.add(new EntityState("TROO", 'D', 3, 58, new A_Chase()));   //57
        GameLogic.stateList.add(new EntityState("TROO", 'D', 3, 51, new A_Chase()));   //58
        GameLogic.stateList.add(new EntityState("TROO", 'E', 8, 60, new A_FaceTarget()));   //59
        GameLogic.stateList.add(new EntityState("TROO", 'F', 8, 61, new A_FaceTarget()));   //60
        GameLogic.stateList.add(new EntityState("TROO", 'G', 8, 51, new A_Projectile(Fireball.class)));   //61
        GameLogic.stateList.add(new EntityState("TROO", 'H', 2, 63, null));   //62
        GameLogic.stateList.add(new EntityState("TROO", 'H', 2, 51, new A_Pain()));   //63
        GameLogic.stateList.add(new EntityState("TROO", 'I', 8, 65, null));   //64
        GameLogic.stateList.add(new EntityState("TROO", 'J', 8, 66, new A_Scream()));   //65
        GameLogic.stateList.add(new EntityState("TROO", 'K', 6, 67, null));   //66
        GameLogic.stateList.add(new EntityState("TROO", 'L', 6, 68, new A_Fall()));   //67
        GameLogic.stateList.add(new EntityState("TROO", 'M', -1, 68, null));   //68
        GameLogic.stateList.add(new EntityState("POSS", 'A', 10, 70, new A_Look()));  //69
        GameLogic.stateList.add(new EntityState("POSS", 'B', 10, 69, new A_Look()));  //70
        GameLogic.stateList.add(new EntityState("POSS", 'A', 4, 72, new A_Chase()));   //71
        GameLogic.stateList.add(new EntityState("POSS", 'A', 4, 73, new A_Chase()));   //72
        GameLogic.stateList.add(new EntityState("POSS", 'B', 4, 74, new A_Chase()));   //73
        GameLogic.stateList.add(new EntityState("POSS", 'B', 4, 75, new A_Chase()));   //74
        GameLogic.stateList.add(new EntityState("POSS", 'C', 4, 76, new A_Chase()));   //75
        GameLogic.stateList.add(new EntityState("POSS", 'C', 4, 77, new A_Chase()));   //76
        GameLogic.stateList.add(new EntityState("POSS", 'D', 4, 78, new A_Chase()));   //77
        GameLogic.stateList.add(new EntityState("POSS", 'D', 4, 71, new A_Chase()));   //78
        GameLogic.stateList.add(new EntityState("POSS", 'E', 10, 80, new A_FaceTarget())); // 79
        GameLogic.stateList.add(new EntityState("POSS", 'F', 8, 81, new A_BulletAttack(15, 15f, "pistol/shoot"))); // 80
        GameLogic.stateList.add(new EntityState("POSS", 'E', 8, 71, new A_FaceTarget())); // 81
        GameLogic.stateList.add(new EntityState("POSS", 'G', 3, 83, null));   //82
        GameLogic.stateList.add(new EntityState("POSS", 'G', 3, 71, new A_Pain()));   //83
        GameLogic.stateList.add(new EntityState("POSS", 'H', 5, 85, null));   //84
        GameLogic.stateList.add(new EntityState("POSS", 'I', 5, 86, new A_Scream()));   //85
        GameLogic.stateList.add(new EntityState("POSS", 'J', 5, 87, new A_Fall()));   //86
        GameLogic.stateList.add(new EntityState("POSS", 'K', 5, 88, null));   //87
        GameLogic.stateList.add(new EntityState("POSS", 'L', -1, 88, null));   //88

    }

    public static void loadSprites(Array<WadFile> wads) {
        //For now, just load player sprites. I'll generalize this later.
        RenderFuncs.spriteMap.put("PLAY", new GameSprite(wads, "PLAY"));
        RenderFuncs.spriteMap.put("SARG", new GameSprite(wads, "SARG"));
        RenderFuncs.spriteMap.put("BAL1", new GameSprite(wads, "BAL1"));
        RenderFuncs.spriteMap.put("PUFF", new GameSprite(wads, "PUFF"));
        RenderFuncs.spriteMap.put("BLUD", new GameSprite(wads, "BLUD"));
        RenderFuncs.spriteMap.put("TROO", new GameSprite(wads, "TROO"));
        RenderFuncs.spriteMap.put("POSS", new GameSprite(wads, "POSS"));
    }

    public static void loadTextures(Array<WadFile> wads) {

        for (WadFile w : wads) {

            if (!w.contains("G_START") || !w.contains("G_END")) {continue;}

            int start = w.lastIndexOf("G_START") + 1;
            int end = w.lastIndexOf("G_END");

            for (int i = start; i < end; i++) {
                 RenderFuncs.textureMap.put(w.getEntry(i).getName(), getTexture(w, w.getEntry(i).getName()));
            }
        }

    }

    public static void loadLevelEffects() {
        GameLogic.effectList.add(new T_ChangeLevel());
        GameLogic.effectList.add(new T_SlimeDamage());
    }
}