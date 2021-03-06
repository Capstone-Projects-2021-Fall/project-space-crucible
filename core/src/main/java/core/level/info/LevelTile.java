package core.level.info;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import core.wad.funcs.WadFuncs;
import net.mtrop.doom.WadFile;

public class LevelTile {

    //We could change this later to make tiles smaller, potentially allowing more map detail.
    public static final int TILE_SIZE = 64;

    //Different from entity positions- they are constant and multiplied by tile size when map is drawn
    public static class TilePosition {
        public int x;
        public int y;
        public int layer;

        public TilePosition(){
        }
        public TilePosition (int x, int y, int layer) {
            this.x = x;
            this.y = y;
            this.layer = layer;
        }
    }

    final public TilePosition pos;
    public boolean solid;
    public String graphicname;
    public int light;
    public int effect;
    public int arg1;
    public int arg2;
    public boolean repeat;
    public int tag;
    public int bridge;

    public LevelTile(){
        pos = new LevelTile.TilePosition();
    }

    public LevelTile(TilePosition pos, boolean solid, String tex, int light, int effect, int arg1, int arg2,
                     boolean repeat, int tag, int bridge) {
        this.pos = pos;
        this.solid = solid;
        this.light = light;
        this.effect = effect;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.repeat = repeat;
        this.tag = tag;
        this.bridge = bridge;

        graphicname = tex;
    }

    public static Rectangle getTileBounds(LevelTile levelTile) {
        Rectangle tileBounds
                = new Rectangle(levelTile.pos.x * LevelTile.TILE_SIZE,
                levelTile.pos.y * LevelTile.TILE_SIZE,
                LevelTile.TILE_SIZE, LevelTile.TILE_SIZE);
        return  tileBounds;
    }

    public String toString() {
        return "Position: " + pos.x + ", " + pos.y + "\n"
                + "Solid: " + solid + "\n"
                + "Texture: " + graphicname + "\n"
                + "Light Level: " + light + "\n"
                + "Effect: " + effect + " (" + arg1 + ", " + arg2 + ")\n"
                + "Repeat: " + repeat + "\n"
                + "Tag: " + tag;
    }
}
