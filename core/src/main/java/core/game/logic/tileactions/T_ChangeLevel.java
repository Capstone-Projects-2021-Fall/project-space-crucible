package core.game.logic.tileactions;

import core.game.logic.GameLogic;

public class T_ChangeLevel implements TileAction {

    //Change level to next if arg1 is 0, or to that level number if it's above 0.
    @Override
    public void run(int arg1, int arg2) {

        if (arg1 > 0) {
            GameLogic.readyChangeLevel(GameLogic.levels.get(arg1));
        } else {
            GameLogic.readyChangeLevel(GameLogic.levels.get(GameLogic.currentLevel.getLevelnumber()+1));
        }
    }
}
