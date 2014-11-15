package players.MorningBirds;

import java.io.*;
import java.util.*;

/*
 * Java 7
 * 
 * Compile with "javac MorningBirds.java"
 * Run with "java players.MorningBirds.MorningBirds"
 * 
 * Servants find treasure from morning until noon.
 * At noon they go to bed to prepare for next day.
 * 
 * According to Benjamin Franklin, "Early to bed, early to rise, keeps a 
 *      man healthy, WEALTHY, and wise."
*/
public class MorningBirds {

    private final static String STARTDAY = "START_DAY";

    private final static String STARTTURN = "START_TURN";

    private final static String ENDTURN = "END_TURN";

    private final static String ENDDAY = "END_DAY";

    private final static String MOVERETURN = "R";

    private final static String MOVESEARCH = "S";

    private final static String MOVENOTHING = "N";

    private final static String RETURNED = "R";

    private final static String FAILEDRETURN = "r";

    private final static String SEARCHING = "S";

    private final static String DEAD = "D";

    private final static String SLEEPING = "N";

    private final static String EXIT = "EXIT";

    private final static String ALIVE = "A";

    private final static int NOON = 15;

    private enum Status{RETURNING, SEARCHING, DEAD, SLEEPING}

    private enum Move{RETURN, SEARCH, NOTHING}

    private int index = -1;

    private int day;

    private int time;

    private Status[] statuses = new Status[5];

    private BufferedReader in = new BufferedReader(
            new InputStreamReader(System.in));



    public static void main (String args[]) throws Exception{
        MorningBirds program = new MorningBirds();
    }

    public MorningBirds() throws Exception{

        index = Integer.parseInt(in.readLine().split("\\s")[1]);

        Arrays.fill(statuses, Status.SEARCHING);

        while(true){
            String[] input = in.readLine().split("\\s");
            if (input[0].equals(ENDTURN) || input[0].equals(ENDDAY)){
                updateStatus(input);
                continue;
            }
            if (input[0].equals(EXIT)){
                return;
            }
            if (input[0].equals(STARTDAY)){
                updateDay(input);
                continue;
            }
            if (input[0].equals(STARTTURN)){
                updateTurn(input);
                doTurn(input);
            }
        }

    }

    private void updateStatus(String[] input){
        String[] stats = input[index + 1].split(",");
        for(int i = 0; i < 5; i++){
            switch (stats[i]){
                case "R":
                case "A":
                case "N":
                    statuses[i] = Status.SLEEPING;
                    break;
                case "S":
                    statuses[i] = Status.SEARCHING;
                    break;
                case "r":
                case "D":
                    statuses[i] = Status.DEAD;
                    break;
                default:
                    break;
            }
        }
    }

    private void updateDay(String[] input) throws Exception{
        day = Integer.parseInt(input[1].split("/")[0]);
    }

    private void updateTurn(String[] input){
        time = Integer.parseInt(input[1]);
    }

    private void doTurn(String[] input){
        Move[] moves = new Move[5];
        for (int i = 0; i < 5; i++){
            if (statuses[i] == Status.DEAD){
                moves[i] = Move.NOTHING;
                continue;
            } else if (time >= NOON){
                if (statuses[i] == Status.SLEEPING){
                    moves[i] = Move.NOTHING;
                } else {
                    moves[i] = Move.RETURN;
                }
            } else {
                moves[i] = Move.SEARCH;
            }
        }
        for (int i = 0; i < 5; i++){
            switch (moves[i]){
                case SEARCH:
                    statuses[i] = Status.SEARCHING;
                    break;
                case RETURN:
                    statuses[i] = Status.RETURNING;
                    break;
                default:
                    break;
            }
        }
        String[] outputs = new String[5];
        for (int i = 0; i < 5; i++){
            switch (moves[i]){
                case SEARCH:
                    outputs[i] = MOVESEARCH;
                    break;
                case RETURN:
                    outputs[i] = MOVERETURN;
                    break;
                case NOTHING:
                    outputs[i] = MOVENOTHING;
            }
        }
        String totalOutput = "";
        for(String output : outputs){
            if (totalOutput != ""){
                totalOutput += ",";
            }
            totalOutput += output;
        }
        System.out.println(totalOutput);
    }

}
