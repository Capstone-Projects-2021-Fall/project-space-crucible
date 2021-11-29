package core.wad.funcs;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import core.game.entities.*;
import core.game.entities.actions.*;
import core.game.logic.*;
import core.game.logic.tileactions.*;
import core.gdx.wad.RenderFuncs;
import core.level.info.LevelData;
import net.mtrop.doom.WadEntry;
import net.mtrop.doom.WadFile;
import net.mtrop.doom.graphics.PNGPicture;
import org.lwjgl.system.CallbackI;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

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
        GameLogic.effectList.add(new T_ExecuteScript());
        GameLogic.effectList.add(new T_InstantDamage());
        GameLogic.effectList.add(new T_SpawnThing());
    }

    public static void loadScripts(Array<WadFile> wads) {

        for (WadFile w : wads) {
            for (WadEntry we : w) {
                if (we.getName().startsWith("SCRIPT")) {
                    System.out.println("Reading " + we.getName());
                    int scriptnum = Integer.parseInt(we.getName().substring(6));
                    Queue<ScriptCommand> commandQueue = new LinkedList<>();

                    try {
                        String script = w.getTextData(we.getName(), Charset.defaultCharset());
                        Scanner scriptScanner = new Scanner(script);
                        ScriptCommand nextCommand = new ScriptCommand();

                        //Read script text
                        while (scriptScanner.hasNextLine()) {
                            String line = scriptScanner.nextLine();
                            StringTokenizer st = new StringTokenizer(line, " ,()");
                            String command = st.nextToken();

                            if (command.equals("delay")) {
                                nextCommand.delay = Integer.parseInt(st.nextToken());
                                System.out.println("Adding " + nextCommand.action.getClass().getName() + ", (" + nextCommand.arg1 + ", " + nextCommand.arg2 + ") with delay " + nextCommand.delay);
                                commandQueue.add(nextCommand);
                                nextCommand = new ScriptCommand();
                            } else {
                                if (nextCommand.action != null) {
                                    System.out.println("Adding " + nextCommand.action.getClass().getName() + ", (" + nextCommand.arg1 + ", " + nextCommand.arg2 + ") with delay " + nextCommand.delay);
                                    commandQueue.add(nextCommand);
                                    nextCommand = new ScriptCommand();
                                }

                                switch (command) {
                                    case "ChangeLevel":
                                        nextCommand.action = new T_ChangeLevel();
                                        break;

                                    case "SlimeDamage":
                                        nextCommand.action = new T_SlimeDamage();
                                        break;

                                    case "ExecuteScript":
                                        nextCommand.action = new T_ExecuteScript();
                                        break;

                                    case "InstantDamage":
                                        nextCommand.action = new T_InstantDamage();
                                        break;

                                    case "SpawnThing":
                                        nextCommand.action = new T_SpawnThing();
                                        break;

                                }

                                nextCommand.arg1 = Integer.parseInt(st.nextToken());
                                nextCommand.arg2 = Integer.parseInt(st.nextToken());
                                nextCommand.tag = Integer.parseInt(st.nextToken());

                                if (!scriptScanner.hasNextLine()) {
                                    System.out.println("Adding " + nextCommand.action.getClass().getName() + ", (" + nextCommand.arg1 + ", " + nextCommand.arg2 + ") with delay " + nextCommand.delay);
                                    commandQueue.add(nextCommand);
                                }
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    GameLogic.scripts.put(scriptnum, commandQueue);
                }
            }
        }

    }
}