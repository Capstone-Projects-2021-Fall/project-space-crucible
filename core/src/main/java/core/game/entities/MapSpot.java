package core.game.entities;

import core.game.logic.Properties;

public class MapSpot extends Entity {

    public MapSpot(Entity.Position pos, int tag, int layer) {
        super(
                "Spot",
                -1,
                pos,
                0,
                8,
                8,
                new Integer[]{0, -1, -1, -1, -1, -1},
                tag,
                0,
                layer
        );
    }
}
