package core.game.entities;


import core.game.logic.GameLogic;

//The parent class of all AI-controlled enemies.
//The hold a target pointer for whichever Entity is drawing their aggression. This need not be a PlayerPawn.
//If target is null, the monster goes idle.
public class BaseMonster extends Entity {

    private int target = -1;
    final private String[] sounds = new String[4];
    public boolean ambush = false;

    final public static int SEESOUND = 0;
    final public static int PAINSOUND = 1;
    final public static int DIESOUND = 2;
    final public static int ACTIVESOUND = 3;

    public BaseMonster(){}

    public BaseMonster(String name, int health, Position pos, int speed, int width, int height, Integer[] states, int tag, long flags, String[] sounds, int layer, boolean ambush) {
        super(name, health, pos, speed, width, height, states, tag, flags, layer);
        this.sounds[SEESOUND] = sounds[SEESOUND];
        this.sounds[PAINSOUND] = sounds[PAINSOUND];
        this.sounds[DIESOUND] = sounds[DIESOUND];
        this.sounds[ACTIVESOUND] = sounds[ACTIVESOUND];
        this.ambush = ambush;
    }

    public String getSound(int index) {return sounds[index];}

    public int getTarget() {return this.target;}
    public void setTarget(int target) {
        this.target = target;
    }

    @Override
    public void setState(Integer state) {
        try {
            currentState = GameLogic.stateList.get(state);
            currentStateIndex = state;
            remainingStateTics = currentState.getDuration();
            if (currentState.getAction() != null) {
                currentState.getAction().run(this,
                        target > -1 ? GameLogic.entityList.get(target) : null);
            }
        } catch (IndexOutOfBoundsException ignored){

            if (currentState.getAction() != null) {
                currentState.getAction().run(this, null);
            }
        }
    }

    @Override
    public void takeDamage(Entity cause, int damage) {
        super.takeDamage(cause, damage);
        target = GameLogic.entityList.indexOf(cause);
    }
}
