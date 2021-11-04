package core.game.entities.actions;

import core.game.entities.BaseMonster;
import core.game.entities.Entity;
import core.game.entities.PlayerPawn;
import core.wad.funcs.SoundFuncs;

public class A_Scream implements StateAction{
    @Override
    public void run(Entity caller, Entity target) {

        if (caller instanceof BaseMonster) {
            SoundFuncs.playSound(((BaseMonster) caller).getSound(BaseMonster.DIESOUND));
        } else if (caller instanceof PlayerPawn) {
            SoundFuncs.playSound(((PlayerPawn) caller).getSound(PlayerPawn.DIESOUND));
        }
    }
}
