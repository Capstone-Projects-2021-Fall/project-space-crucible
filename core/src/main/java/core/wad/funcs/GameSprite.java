package core.wad.funcs;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import net.mtrop.doom.WadEntry;
import net.mtrop.doom.WadFile;

import java.util.HashMap;
import java.util.Map;

public class GameSprite {

    //Static map of every possible sprite in the game to their 4-character name
    //Since all EntityStates reference the global spriteMap, each sprite is stored only once.

    final private HashMap<Character, GameSpriteFrame> frames;

    //One frame of an animation, with 8 different angles
    public static class GameSpriteFrame {

        final private Sprite[] sprites;

        protected GameSpriteFrame() {
            sprites = new Sprite[8];
        }

        protected void setSprite(int index, Sprite sprite) {
            sprites[index] = sprite;
        }

        //If no angle is provided, it is angle 0- always facing the same way.
        protected void setSprite(Sprite sprite) {
            for (int i = 0; i < 8; i++) {
                sprites[i] = sprite;
            }
        }
    }

    public GameSprite(Array<WadFile> wads, String name) {
        frames = new HashMap<>();

        //Go through all WADs
        for (WadFile file : wads) {
            //Go through all sprites in the WAD (they are between the above markers)
            for (WadEntry entry : file) {

                //Find all of them with this sprite's name
                if (entry.getName().startsWith(name)) {

                    String entryName = entry.getName();

                    //Get frame letter and angle from name
                    Character frame = entryName.charAt(4);
                    int angle = Integer.parseInt(entryName.substring(5, 6));

                    if (!frames.containsKey(frame)) {
                        frames.put(frame, new GameSpriteFrame());
                    }

                    //Angle 0- same direction always
                    if (angle == 0) {
                        frames.get(frame).setSprite(WadFuncs.getSprite(file, entryName));
                    } else {
                        frames.get(frame).setSprite(angle - 1, WadFuncs.getSprite(file, entryName));
                    }

                    //If the name is 8 long, also produce a flipped sprite for a different frame
                    if (entryName.length() > 6) {
                        Character frame2 = entryName.charAt(6);
                        int angle2 = Integer.parseInt(entryName.substring(7));

                        if (!frames.containsKey(frame2)) {
                            frames.put(frame2, new GameSpriteFrame());
                        }
                        Sprite sprite = WadFuncs.getSprite(file, entryName);
                        sprite.flip(true, false);

                        //Again, check for angle 0
                        if (angle2 == 0) {
                            frames.get(frame2).setSprite(sprite);
                        } else
                            frames.get(frame2).setSprite(angle2 - 1, sprite);
                    }
                }
            }

            //Make sure all frames have graphics for all angles
            frames.forEach((k, v) -> {
                for (int i = 0; i < 8; i++) {
                    if (v.sprites[i] == null) {
                        System.out.println("Sprite " + name + " has incomplete angles for frame " + k + ".");
                        throw new NullPointerException();
                    }
                }
            });
        }
    }

    public Sprite getFrame(Character frame, float angle) {

        int spriteAngle = -1;

        if (angle < 22.5 || angle >= 337.5) {
            spriteAngle = 7;
        } else if (angle < 337.5 && angle >= 292.5) {
            spriteAngle = 8;
        } else if (angle < 292.5 && angle >= 247.5) {
            spriteAngle = 1;
        } else if (angle < 247.5 && angle >= 202.5) {
            spriteAngle = 2;
        } else if (angle < 202.5 && angle >= 157.5) {
            spriteAngle = 3;
        } else if (angle < 157.5 && angle >= 112.5) {
            spriteAngle = 4;
        } else if (angle < 112.5 && angle >= 67.5) {
            spriteAngle = 5;
        } else if (angle < 67.5 && angle >= 22.5) {
            spriteAngle = 6;
        }

        if (spriteAngle == -1) {
            System.out.println("Angle " + angle + " is impossible.");
            return null;
        }

        return frames.get(frame).sprites[spriteAngle-1];
    }
}
