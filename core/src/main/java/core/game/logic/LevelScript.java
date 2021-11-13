package core.game.logic;

import core.game.entities.Entity;

import java.util.PriorityQueue;
import java.util.Queue;

public class LevelScript {

    final private Queue<ScriptCommand> commandQueue;
    final private Entity activator;

    public LevelScript() {
        activator = null;
        commandQueue = new PriorityQueue<>();
    }

    public LevelScript(Entity activator, Queue<ScriptCommand> commandQueue) {
        this.activator = activator;
        this.commandQueue = new PriorityQueue<>();
        this.commandQueue.addAll(commandQueue);
    }

    public boolean run() {

        if (commandQueue.peek() == null) {return true;}

        if (commandQueue.peek().delay > 0) {
            commandQueue.peek().delay--;
        }

        while (commandQueue.peek() != null && commandQueue.peek().delay == 0) {
            commandQueue.remove().run(activator);
        }

        return commandQueue.peek() == null;
    }
}
