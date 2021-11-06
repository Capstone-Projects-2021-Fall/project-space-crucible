package core.level.info;

import core.game.entities.Entity;

public class LevelObject {

    public int type;
    public float xpos;
    public float ypos;
    public float angle;
    public boolean singleplayer;
    public boolean cooperative;
    public boolean[] skill;
    public boolean ambush;
    public int tag;
    public int layer;

    public LevelObject(){}

    public LevelObject(int type, float xpos, float ypos, float angle, boolean singleplayer, boolean cooperative, boolean[] skill,
                       boolean ambush, int tag, int layer) {
        this.type = type;
        this.xpos = xpos;
        this.ypos = ypos;
        this.angle = angle;
        this.singleplayer = singleplayer;
        this.cooperative = cooperative;
        this.skill = skill;
        this.ambush = ambush;
        this.tag = tag;
        this.layer = layer;
    }

    public String toString() {
        return "Type: " + type + "\n"
                + "Position: " + xpos + ", " + ypos + "\n"
                + "Singleplayer: " + singleplayer + "\n"
                + "Cooperative: " + cooperative + "\n"
                + "Skills: " + skill[0] + ", " + skill[1] + ", " + skill[2] + ", " + skill[3] + ", " + skill[4] + "\n"
                + "Ambush: " + ambush + "\n"
                + "Tag: " + tag + "\n"
                + "Layer: " + layer + "\n";
    }
}
