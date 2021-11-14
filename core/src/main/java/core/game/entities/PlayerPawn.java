package core.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import core.game.logic.CollisionLogic;
import core.game.logic.GameLogic;
import core.level.info.LevelData;
import core.level.info.LevelObject;
import core.wad.funcs.SoundFuncs;

public class PlayerPawn extends Entity {

    final public static int PAINSOUND = 0;
    final public static int DIESOUND = 1;

    private String[] sounds = new String[2];

    public float velx = 0;
    public float vely = 0;
    public boolean[] controls = new boolean[5];
    public Entity botTarget = null;

    public PlayerPawn(){}

    public PlayerPawn(String name, int health, Position pos, int speed, int width, int height, Integer[] states, int tag, long flags, String[] sounds, int layer) {
        super(name, health, pos, speed, width, height, states, tag, SOLID, layer);
        this.sounds = sounds;
    }

    public void movementUpdate() {

        if(controls == null){
            return;
        }
        //Debug keys- play, pain And death animations
        if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
            if (GameLogic.levels.get(GameLogic.currentLevel.getLevelnumber()+1) != null) {
                GameLogic.readyChangeLevel(getNewLevelData());
            }
        }

        float checkPosX = getPos().x;
        float checkPosY = getPos().y;

        //Is a movement key is CURRENTLY pressed, move player.

        if (getHealth() > 0) {
            if(controls[GameLogic.LEFT])
                velx = velx > -getSpeed() ? velx-10 : velx;
            else if (velx < 0)
                velx += 10;
            if(controls[GameLogic.RIGHT])
                velx = velx < getSpeed() ? velx+10 : velx;
            else if (velx > 0)
                velx -= 10;
            if(controls[GameLogic.UP])
                vely = vely < getSpeed() ? vely+10 : vely;
            else if (vely > 0)
                vely -= 10;
            if(controls[GameLogic.DOWN])
                vely = vely > -getSpeed() ? vely-10 : vely;
            else if (vely < 0)
                vely += 10;
        }

        checkPosX += velx * Gdx.graphics.getDeltaTime();
        checkPosY += vely * Gdx.graphics.getDeltaTime();

        //Check only x first
        Rectangle newBounds = new Rectangle(checkPosX, getPos().y, getWidth(), getHeight());

        if(CollisionLogic.simpleCollisionCheck(newBounds, this)){
            setPos(newBounds);
        }

        //Check y now
        newBounds.set(getPos().x, checkPosY, getWidth(), getHeight());

        if(CollisionLogic.simpleCollisionCheck(newBounds, this)){
            setPos(newBounds);
        }

        if(controls[GameLogic.SHOOT]) {

            if (getHealth() > 0 && GameLogic.ticCounter > 0) {
                setState(getStates()[Entity.MISSILE]);
                hitScanAttack(getPos().angle, 15);
                SoundFuncs.playSound("pistol/shoot");
                GameLogic.alertMonsters(this);
            } else if (getRemainingStateTics() == -1) {

                if (GameLogic.isSinglePlayer) {
                    //GameLogic.readyChangeLevel(GameLogic.currentLevel);
                } else {
                    int tag = this.tag;
                    this.tag = 0; //Stop controlling the corpse

                    //Find map start and respawn
                    for (LevelObject lo : GameLogic.currentLevel.getObjects()) {

                        if (lo.type == 0 && lo.tag == tag) {
                            GameLogic.newEntityQueue.add(GameLogic.mapIDTable.get(0)
                                    .spawnEntity(new Entity.Position(lo.xpos, lo.ypos, lo.angle), tag, lo.layer, false));
                            break;
                        }
                    }
                }
            }
        }

        //If player is IDLE and is hitting a move key, set WALK state
        //Otherwise, if player is NOT MOVING and NOT DOING SOMETHING ELSE, set IDLE state
        if(getCurrentFrame() == 'A' && getRemainingStateTics() == -1
                && (controls[GameLogic.LEFT] || controls[GameLogic.RIGHT] || controls[GameLogic.UP] || controls[GameLogic.DOWN])) {
            setState(getStates()[Entity.WALK]);
        } else if (getCurrentFrame() <= 'D' && getRemainingStateTics() != -1
                && !(controls[GameLogic.LEFT]) && !(controls[GameLogic.RIGHT]) && !(controls[GameLogic.UP]) && !(controls[GameLogic.DOWN])) {
            setState(getStates()[Entity.IDLE]);
        }

        //CollisionLogic.entityCollision(Entity,Entity);
    }

    private LevelData getNewLevelData() {
        int newLevel = GameLogic.currentLevel.getLevelnumber() + 1;
        return GameLogic.levels.get(newLevel);
    }

    public String getSound(int sound) {
        return sounds[sound];
    }
}
