package core.wad.funcs;

import core.game.entities.Entity;
import java.util.Scanner;
import java.util.StringTokenizer;

public class EntityFuncs {
    final private static String DELIMS = ": \t";
    private static int line = -1;
    private static Scanner stringReader;
    private static String currentLine;

    public static void loadEntityClasses(String rawData) throws ParseException {
        stringReader = new Scanner(rawData);

        while (stringReader.hasNextLine()) {
            nextLine();
        }

    }

    public static Class<? extends Entity> loadEntityClass() throws ParseException {
        Class<? extends Entity> newClass = null;
        String className;
        String parentClass;
        StringTokenizer st = new StringTokenizer(currentLine, DELIMS);
        int mapID = -1;

        try {
            //First token should always be "Entity"
            if (!st.nextToken().equals("Entity")) {throw new ParseException();}

            //Second token should be name
            className = st.nextToken();

            //Third token could be parent OR id. Check for ":"
            if (currentLine.contains(":")) {
                parentClass = st.nextToken();
            }

            mapID = Integer.parseInt(st.nextToken());

            //If there is another token and it's NOT an opening bracket or comment, throw.
            checkBracket(st);

            //Now we are reading the Entity definition
            nextLine();
            st = new StringTokenizer(currentLine, DELIMS);
            if (!st.nextToken().equals("Properties")) {throw new ParseException();}
            checkBracket(st);


        } catch (Exception e) {throw new ParseException();}



        return newClass;
    }

    public static int getLine() {return line;}

    public static class ParseException extends Exception {}

    private static class Properties {
        public int health = -1;
        public int speed = 0;
        public int width = 0;
        public int height = 0;
        Integer[] states = {0, 0, 0, 0, 0, 0};
        long flags = 0;
        String[] monsterSounds = new String[4];
        int projDamage = 0;
    }

    private static void nextLine() {
        while ((currentLine.strip().startsWith("//") || currentLine.isBlank()) && stringReader.hasNextLine()) {
            line++;
            currentLine = stringReader.nextLine();
        }
    }

    private static void checkBracket(StringTokenizer st) throws ParseException {

        if (!st.hasMoreTokens()) {
            nextLine();
            st = new StringTokenizer(currentLine, DELIMS);
        }

        String next = st.nextToken();
        if (!next.equals("{") && !next.startsWith("//"))  {throw new ParseException();}
        if (!next.startsWith("//") && st.hasMoreTokens()) {throw new ParseException();}
    }
}
