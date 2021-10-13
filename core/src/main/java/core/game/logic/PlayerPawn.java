package core.game.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import net.mtrop.doom.WadFile;

public class PlayerPawn extends Entity {

    public PlayerPawn(int health, Position pos, int speed, int width, int height) {
        super(health, pos, speed, width, height, new Integer[]{0, 1, 5, 6, 7, 9});
    }


    public void movementUpdate(){
        //Input handling with polling method
        //This handles all the keys pressed with the keyboard.

        //Debug keys- play pain and death animations
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            setState(getStates()[Entity.PAIN]);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            setState(getStates()[Entity.DIE]);
        }

        //Is a movement key is CURRENTLY pressed, move player.
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))
            getPos().x -= getSpeed() * Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))
            getPos().x += getSpeed() * Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W))
            getPos().y += getSpeed() * Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S))
            getPos().y -= getSpeed() * Gdx.graphics.getDeltaTime();

        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            setState(getStates()[Entity.MISSILE]);
        }

        //If player is IDLE and is hitting a move key, set WALK state
        //Otherwise, if player is NOT MOVING and NOT DOING SOMETHING ELSE, set IDLE state
        if(getCurrentFrame() == 'A' && getRemainingStateTics() == -1
                && (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)
                || Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)
                || Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)
                || Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S))) {
            setState(getStates()[Entity.WALK]);
        } else if (getCurrentFrame() <= 'D'
                && !(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))
                && !(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))
                && !(Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W))
                && !(Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S))) {
            setState(getStates()[Entity.IDLE]);
        }
    }
}
