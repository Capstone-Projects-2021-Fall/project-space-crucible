package core.game.entities.actions;

import core.game.entities.Entity;

public class A_PrintMessage implements StateAction{

    private String message;

    public A_PrintMessage(){message = "";}
    public A_PrintMessage(String message) {
        this.message = message;
    }

    @Override
    public void run(Entity caller, Entity target) {
        System.out.println(message + " - " + caller.getClass().getName());
    }
}
