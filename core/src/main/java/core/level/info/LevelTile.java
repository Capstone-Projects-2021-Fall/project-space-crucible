package core.level.info;

import com.badlogic.gdx.graphics.Texture;
import core.wad.funcs.WadFuncs;
import net.mtrop.doom.WadFile;

public class LevelTile {

    //We could change this later to make tiles smaller, potentially allowing more map detail.
    public static final int TILE_SIZE = 64;

    //Different from entity positions- they are constant and multiplied by tile size when map is drawn
    public static class TilePosition {
        public int xpos;
        public int ypos;

        public TilePosition (int x, int y) {
            xpos = x;
            ypos = y;
        }
    }

    final private TilePosition pos;
    private boolean solid;
    private Texture graphic;
    private int light;
    private int effect;
    private int arg1;
    private int arg2;
    private boolean repeat;
    private int tag;

    public LevelTile(TilePosition pos, boolean solid, String tex, int light, int effect, int arg1, int arg2,
                     boolean repeat, int tag, WadFile file) {
        this.pos = pos;
        this.solid = solid;
        this.light = light;
        this.effect = effect;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.repeat = repeat;
        this.tag = tag;

        graphic = WadFuncs.getTexture(file, tex);
    }

    public Texture getTileTexture() {
        return graphic;
    }

}
