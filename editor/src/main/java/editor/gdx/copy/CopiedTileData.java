package editor.gdx.copy;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import core.wad.funcs.WadFuncs;
import net.mtrop.doom.WadFile;

public class CopiedTileData {
    public boolean solid;
    public Texture graphic;
    public String graphicname;
    public int light;
    public int effect;
    public int arg1;
    public int arg2;
    public boolean repeat;
    public int tag;

    public CopiedTileData(boolean solid, String tex, int light, int effect, int arg1, int arg2,
                          boolean repeat, int tag, Array<WadFile> wads) {

        this.solid = solid;
        this.light = light;
        this.effect = effect;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.repeat = repeat;
        this.tag = tag;

        graphicname = tex;
        graphic = WadFuncs.getTexture(wads, tex);
    }
}