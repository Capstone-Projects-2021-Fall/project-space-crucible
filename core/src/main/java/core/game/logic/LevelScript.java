package core.game.logic;

import core.game.entities.Entity;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class LevelScript {

    final private Queue<ScriptCommand> commandQueue;
    final private Entity activator;
    private boolean newCommand = true;

    public LevelScript() {
        activator = null;
        commandQueue = new LinkedList<>();
    }

    public LevelScript(Entity activator, Queue<ScriptCommand> commandQueue) {
        this.activator = activator;
        this.commandQueue = new LinkedList<>();
        this.commandQueue.addAll(commandQueue);
    }

    public boolean run() {

        if (commandQueue.peek() == null) {return true;}

        while (commandQueue.peek() != null && newCommand) {
            commandQueue.peek().run(activator);

            if (commandQueue.peek() != null && commandQueue.peek().delay == 0) {
                commandQueue.remove();
            } else {
                break;
            }
        }

        if (commandQueue.peek() != null && commandQueue.peek().delay > 0) {
            commandQueue.peek().delay--;
            System.out.println(commandQueue.peek().delay);

            if (commandQueue.peek() != null && commandQueue.peek().delay > 0) {
                newCommand = false;
            } else {
                commandQueue.remove();
                newCommand = true;
            }
        }

        return commandQueue.peek() == null;
    }
}
