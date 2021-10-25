package core.game.entities.actions;

import core.game.entities.BaseMonster;
import core.game.entities.Entity;
import core.game.entities.PlayerPawn;
import core.wad.funcs.SoundFuncs;

public class A_Pain implements StateAction{
    @Override
    public void run(Entity caller, Entity target) {

        if (caller instanceof BaseMonster) {
            SoundFuncs.playSound(((BaseMonster) caller).getSound(BaseMonster.PAINSOUND));
        } else if (caller instanceof PlayerPawn) {
            SoundFuncs.playSound(PlayerPawn.PAINSOUND);
        }
    }
}
