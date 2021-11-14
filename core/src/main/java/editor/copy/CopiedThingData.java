package editor.copy;


public class CopiedThingData {
    public int type;
    public float angle;
    public boolean singleplayer;
    public boolean cooperative;
    public boolean[] skill;
    public boolean ambush;
    public int tag;
    public int layer;

    public CopiedThingData(int type, float angle, boolean singleplayer,
                           boolean cooperative, boolean[] skill, boolean ambush, int tag, int layer) {
        this.type = type;
        this.angle = angle;
        this.singleplayer = singleplayer;
        this.cooperative = cooperative;
        this.skill = skill;
        this.ambush = ambush;
        this.tag = tag;
        this.layer = layer;
    }
}