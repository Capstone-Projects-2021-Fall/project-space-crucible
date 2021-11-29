package core.game.logic.tileactions;

import core.game.entities.Entity;
import core.game.logic.GameLogic;
import core.level.info.LevelObject;

public class T_SpawnThing implements TileAction {
    @Override
    public void run(Entity activator, int arg1, int arg2, int tag) {
        for (LevelObject o : GameLogic.currentLevel.getObjects()) {
            if (o.type == -1 && o.tag == tag) {
                System.out.println(GameLogic.mapIDTable.get(arg1).getClassName());
                GameLogic.newEntityQueue.add(GameLogic.mapIDTable.get(arg1)
                        .spawnEntity(new Entity.Position(o.xpos, o.ypos, o.angle), o.tag, o.layer, o.ambush));
            }
        }
    }
}
