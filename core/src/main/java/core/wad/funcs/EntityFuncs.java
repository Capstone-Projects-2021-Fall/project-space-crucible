package core.wad.funcs;

import core.game.entities.BaseMonster;
import core.game.entities.Entity;
import org.lwjgl.system.CallbackI;

import java.text.ParseException;
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

    public static Class<? extends Entity> loadEntityClass() throws ParseException {
        Class<? extends Entity> newClass = null;
        String className;
        String parentClass = "Entity";
        StringTokenizer st = new StringTokenizer(currentLine, DELIMS);
        int mapID = -1;

        //First token should always be "Entity"
        if (!st.nextToken().equals("Entity")) {throw new ParseException();}

        //Second token should be name
        className = st.nextToken();
        System.out.println("Entity " + className);

        //Third token could be parent OR id. Check for ":"
        if (currentLine.contains(":")) {
            parentClass = st.nextToken();
            System.out.println("Inheriting from " + parentClass);
        }

        if (st.countTokens() > 1) {
            mapID = Integer.parseInt(st.nextToken());
            System.out.println("Map ID:\t" + mapID);
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
        p.states = getStates(st);


        return newClass;
    }

    public static int getLine() {return line;}

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


    private static class Properties {
        public int health = -1;
        public int speed = 0;
        public int width = 0;
        public int height = 0;
        Integer[] states = {-1, -1, -1, -1, -1, -1};
        long flags = 0;
        String[] monsterSounds = new String[4];
        int projDamage = 0;
    }

    //Read Properties block from ENTITIES file
    private static Properties getProperties(StringTokenizer st, String parentClass, String className) throws ParseException {

        Properties p = new Properties();

        do {
            st = new StringTokenizer(currentLine, DELIMS);
            System.out.println("Line " + line + ": " + currentLine);

            //If starts with +, read a flag
            if (currentLine.strip().startsWith("+")) {
                switch (currentLine.strip().substring(1)) {
                    case "SOLID":
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
                        if (!parentClass.equals("BaseMonster")) {
                            throw new ParseException("Line " + line + ":\t" + className + " does not extend BaseMonster!");
                        }
                        p.monsterSounds[BaseMonster.PAINSOUND] = value.replaceAll("\"", "");
                        break;

                    case "DieSound":
                        if (!parentClass.equals("BaseMonster")) {
                            throw new ParseException("Line " + line + ":\t" + className + " does not extend BaseMonster!");
                        }
                        p.monsterSounds[BaseMonster.DIESOUND] = value.replaceAll("\"", "");
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

    private static Integer[] getStates(StringTokenizer st) {
        Integer[] states = {-1, -1, -1, -1, -1, -1};

        do {
            System.out.println("Line " + line + ":\t" + currentLine);
        }while (!checkBracket(st, true));

        return states;
    }
}
