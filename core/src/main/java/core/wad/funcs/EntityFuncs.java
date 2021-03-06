package core.wad.funcs;

import core.game.entities.BaseMonster;
import core.game.entities.Entity;
import core.game.entities.PlayerPawn;
import core.game.entities.actions.*;
import core.game.logic.EntitySpawner;
import core.game.logic.EntityState;
import core.game.logic.GameLogic;
import core.game.logic.Properties;

import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.StringTokenizer;

public class EntityFuncs {
    final private static String DELIMS = ": \t";
    private static int line = 0;
    private static Scanner stringReader;
    private static String currentLine;

    public static void loadEntityClasses(String rawData) throws ParseException {

        stringReader = new Scanner(rawData);

        while (stringReader.hasNextLine()) {
            nextLine();
            if (currentLine.strip().startsWith("Entity")) {
                loadEntityClass();
            }
        }

    }

    public static void loadEntityClass() throws ParseException {
        String className;
        String parentClass = "Entity";
        StringTokenizer st = new StringTokenizer(currentLine, DELIMS);
        int mapID = -1;

        //First token should always be "Entity"
        if (!st.nextToken().equals("Entity")) {throw new ParseException();}

        //Second token should be name
        className = st.nextToken();

        //Third token could be parent OR id. Check for ":"
        if (currentLine.contains(":")) {
            parentClass = st.nextToken();
        }

        if (st.countTokens() > 1) {
            mapID = Integer.parseInt(st.nextToken());
        }

        //If there is another token and it's NOT an opening bracket or comment, throw.
        if (!checkBracket(st, false)) {throw new ParseException();}

        //Now we are reading the Entity definition
        nextLine();
        st = new StringTokenizer(currentLine, DELIMS);
        if (!st.nextToken().equals("Properties")) {throw new ParseException();}
        if (!checkBracket(st, false)) {throw new ParseException();}

        //Reading properties
        nextLine();
        Properties p = getProperties(st, parentClass, className);

        nextLine();
        st = new StringTokenizer(currentLine, DELIMS);
        if (!st.nextToken().equals("States")) {throw new ParseException();}
        if (!checkBracket(st, false)) {throw new ParseException();}
        nextLine();
        p.states = getStates(st);

        EntitySpawner newEntity = new EntitySpawner(parentClass, p);
        GameLogic.entityTable.put(className, newEntity);
        GameLogic.mapIDTable.put(mapID, newEntity);
    }

    public static class ParseException extends Exception {
        public ParseException(){super("Line " + line + ":\tInvalid token.");}
        public ParseException(String s){super(s);}
    }

    private static void nextLine() {
        line++;
        currentLine = stringReader.nextLine();
        while ((currentLine.strip().startsWith("//") || currentLine.isBlank()) && stringReader.hasNextLine()) {
            line++;
            currentLine = stringReader.nextLine();
        }
    }

    private static boolean checkBracket(StringTokenizer st, boolean close) {

        final String BRACKET = close ? "}" : "{";

        if (!st.hasMoreTokens()) {
            nextLine();
            st = new StringTokenizer(currentLine, DELIMS);
        }

        String next = st.nextToken();
        if (!next.equals(BRACKET) && !next.startsWith("//"))  {return false;}
        if (!next.startsWith("//") && st.hasMoreTokens()) {return false;}

        return true;
    }

    //Read Properties block from ENTITIES file
    private static Properties getProperties(StringTokenizer st, String parentClass, String className) throws ParseException {

        Properties p = new Properties();
        p.name = className;

        do {
            st = new StringTokenizer(currentLine, DELIMS);

            //If starts with +, read a flag
            if (currentLine.strip().startsWith("+")) {
                switch (currentLine.strip().substring(1)) {
                    case "SOLID":
                        p.flags |= Entity.SOLID;
                        break;

                    default:
                        throw new ParseException();
                }
                nextLine();
            }

            //Else, read a property
            else {
                String prop = st.nextToken();
                String value = st.nextToken();

                switch (prop) {
                    case "Health":
                        p.health = Integer.parseInt(value);
                        break;

                    case "Speed":
                        p.speed = Integer.parseInt(value);
                        break;

                    case "Width":
                        p.width = Integer.parseInt(value);
                        break;

                    case "Height":
                        p.height = Integer.parseInt(value);
                        break;

                    case "SeeSound":
                        if (!parentClass.equals("BaseMonster")) {
                            throw new ParseException("Line " + line + ":\t" + className + " does not extend BaseMonster!");
                        }
                        p.monsterSounds[BaseMonster.SEESOUND] = value.replaceAll("\"", "");
                        break;

                    case "PainSound":
                        if (parentClass.equals("BaseMonster")) {
                            p.monsterSounds[BaseMonster.PAINSOUND] = value.replaceAll("\"", "");
                        } else if(parentClass.equals("PlayerPawn")) {
                            p.playerSounds[PlayerPawn.PAINSOUND] = value.replaceAll("\"", "");
                        } else {
                            throw new ParseException("Line " + line + ":\t" + className + " does not extend BaseMonster or PlayerPawn!");
                        }
                        break;

                    case "DieSound":
                        if (parentClass.equals("BaseMonster")) {
                            p.monsterSounds[BaseMonster.DIESOUND] = value.replaceAll("\"", "");
                        } else if(parentClass.equals("PlayerPawn")) {
                            p.playerSounds[PlayerPawn.DIESOUND] = value.replaceAll("\"", "");
                        } else {
                            throw new ParseException("Line " + line + ":\t" + className + " does not extend BaseMonster or PlayerPawn!");
                        }
                        break;

                    case "ActiveSound":
                        if (!parentClass.equals("BaseMonster")) {
                            throw new ParseException("Line " + line + ":\t" + className + " does not extend BaseMonster!");
                        }
                        p.monsterSounds[BaseMonster.ACTIVESOUND] = value.replaceAll("\"", "");
                        break;

                    case "Damage":
                        if (!parentClass.equals("Projectile")) {
                            throw new ParseException("Line " + line + ":\t" + className + " does not extend Projectile!");
                        }
                        p.projDamage = Integer.parseInt(value);
                        break;

                    default:
                        throw new ParseException();
                }
            }
        } while (!checkBracket(st, true));

        return p;
    }

    private static Integer[] getStates(StringTokenizer st) throws ParseException {
        Integer[] states = {-1, -1, -1, -1, -1, -1};
        Boolean[] isState = {false, false, false, false, false, false};
        int lastStateIndex = -1;
        String[] defaultStates = {"Spawn", "See", "Melee", "Missile", "Pain", "Death"};
        String[] keyWords = {"loop", "stop", "goto"};

        try {
            do {
                st = new StringTokenizer(currentLine, DELIMS);

                String firstToken = st.nextToken();

                //If is a state label
                int stateIndex = isKeyWord(firstToken, defaultStates);
                if (stateIndex > -1) {
                    isState[stateIndex] = true;
                    lastStateIndex = stateIndex;
                    continue;
                }

                //If is a keyword
                int keyWordIndex = isKeyWord(firstToken, keyWords);
                if (keyWordIndex > -1) {
                    switch (keyWords[keyWordIndex]) {
                        case "loop":
                            GameLogic.stateList.getLast().setNextState(states[lastStateIndex]);
                            break;

                        case "stop":
                            GameLogic.stateList.getLast().setNextState(-1);
                            break;

                        case "goto":
                            try {
                                String next = st.nextToken();
                                int nextState = isKeyWord(next, defaultStates);
                                GameLogic.stateList.getLast().setNextState(states[nextState]);
                            } catch (Exception e) {
                                throw new ParseException();
                            }
                            break;

                        default:
                            throw new ParseException();
                    }
                    //nextLine();
                    continue;
                }

                //Otherwise, read the states.
                String frames = st.nextToken();
                Integer duration = Integer.parseInt(st.nextToken());
                StateAction action = null;

                //If there's any more, there should be a code pointer.
                if (st.hasMoreTokens()) {
                    if (!currentLine.contains("A_")) {
                        throw new ParseException();
                    }
                    String actionDef = currentLine.substring(currentLine.indexOf("A_"));
                    action = readAction(actionDef);
                    nextLine();
                }

                //You can define multiple frames with the same duration, sprite, and action on one line
                for (int i = 0; i < frames.length(); i++) {
                    GameLogic.stateList.add(new EntityState(firstToken, frames.charAt(i), duration,
                            GameLogic.stateList.size() + 1, action));

                    //If it's under any state labels, set those states to this one.
                    for (int j = 0; j < isState.length; j++) {
                        if (isState[j]) {
                            states[j] = GameLogic.stateList.size() - 1;
                            isState[j] = false;
                        }
                    }
                }

            } while (!checkBracket(st, true));

            return states;
        } catch (NoSuchElementException nse) {
            nse.printStackTrace();
            throw new ParseException();
        }
    }

    private static int isKeyWord(String string, String[] keyWords) {
        for (int i = 0; i < keyWords.length; i++) {
            if (string.equals(keyWords[i])) return i;
        }
        return -1;
    }

    private static StateAction readAction(String actionDef) throws ParseException {
        StateAction ret = null;
        try {
            StringTokenizer actionST = new StringTokenizer(actionDef, " ,()\"");
            String action = actionST.nextToken();

            switch (action) {
                case "A_BulletAttack":
                    int bulletdamage = Integer.parseInt(actionST.nextToken());
                    float angle = Float.parseFloat(actionST.nextToken());
                    String sound = actionST.nextToken();
                    ret = new A_BulletAttack(bulletdamage, angle, sound);
                    break;

                case "A_Chase":
                    ret = new A_Chase();
                    break;

                case "A_FaceTarget":
                    ret = new A_FaceTarget();
                    break;

                case "A_Fall":
                    ret = new A_Fall();
                    break;

                case "A_Look":
                    ret = new A_Look();
                    break;

                case "A_MeleeAttack":
                    int meleedamage = Integer.parseInt(actionST.nextToken());
                    ret = new A_MeleeAttack(meleedamage);
                    break;

                case "A_Pain":
                    ret = new A_Pain();
                    break;

                case "A_PrintMessage":
                    String message = actionST.nextToken();
                    ret = new A_PrintMessage(message);
                    break;

                case "A_Projectile":
                    String projClass = actionST.nextToken();
                    ret = new A_Projectile(projClass);
                    break;

                case "A_Scream":
                    ret = new A_Scream();
                    break;

                default:
                    throw new ParseException();

            }
        } catch (Exception e) {throw new ParseException();}

        return ret;
    }
}
