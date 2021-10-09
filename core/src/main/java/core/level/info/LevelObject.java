package core.level.info;

import core.game.logic.Entity;

public class LevelObject {

    private int type;
    private Entity.Position pos;
    private boolean singleplayer;
    private boolean cooperative;
    private boolean[] skill;
    private boolean ambush;
    private int tag;

    public LevelObject(int type, int xpos, int ypos, float angle, boolean singleplayer, boolean cooperative, boolean[] skill,
                       boolean ambush, int tag) {
        this.type = type;
        pos = new Entity.Position(xpos, ypos, angle);
        this.singleplayer = singleplayer;
        this.cooperative = cooperative;
        this.skill = skill;
        this.ambush = ambush;
        this.tag = tag;
    }
}
