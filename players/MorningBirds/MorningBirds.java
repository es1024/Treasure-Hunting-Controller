package players.MorningBirds;

import java.io.*;
import java.util.*;

/*
 * Java 7
 * 
 * Compile with "javac ./players/MorningBirds/MorningBirds.java"
 * Run with "java players.MorningBirds.MorningBirds"
 * 
 * Servants find treasure from morning until noon.
 * At noon they go to bed to prepare for next day.
 * 
 * According to Benjamin Franklin, "Early to bed, early to rise, keeps a 
 *      man healthy, WEALTHY, and wise."
 * 
 * 
 */
public class MorningBirds {

    protected final static String STARTDAY = "START_DAY";

    protected final static String STARTTURN = "START_TURN";

    protected final static String ENDTURN = "END_TURN";

    protected final static String ENDDAY = "END_DAY";

    protected final static String MOVERETURN = "R";

    protected final static String MOVESEARCH = "S";

    protected final static String MOVENOTHING = "N";

    protected final static String RETURNED = "R";

    protected final static String FAILEDRETURN = "r";

    protected final static String SEARCHING = "S";

    protected final static String DEAD = "D";

    protected final static String SLEEPING = "N";

    protected final static String EXIT = "EXIT";

    protected final static String ALIVE = "A";

    protected enum Status{SEARCHING, DEAD, RETURNED}

    protected enum Move{RETURN, SEARCH, NOTHING}

    protected int index;

    protected int day;

    protected int turnNum;

    protected int howManyTeams;

    protected int howManyWillDieTodayAtMost;

    protected int howManyHaveDiedToday;

    protected int howManyEnemyPlayers;

    protected int howManyAliveEnemyPlayers;

    protected int howManyEnemyTeams;

    protected int howManyDeadEnemyPlayers;

    protected int howManyReturnedEnemyPlayers;

    protected int howManySearchingEnemyPlayers;

    protected int howManyTotalPlayers;

    protected int howManyTotalAlivePlayers;

    protected int howManyTotalDeadPlayers;

    protected int howManyTotalReturnedPlayers;

    protected int howManyTotalSearchingPlayers;

    protected int howManyOwnAlivePlayers;

    protected int howManyOwnDeadPlayers;

    protected int howManyOwnReturnedPlayers;

    protected int howManyOwnSearchingPlayers;

    protected Status[] statuses = new Status[5];

    protected Status[][] allStatuses = null;

    protected List<Status[][]> allDayStatuses = null;

    protected List<List<Status[][]>> allTimeStatuses = new ArrayList<>();

    protected BufferedReader in = new BufferedReader(
            new InputStreamReader(System.in));

    public static void main (String args[]) throws Exception{
        new MorningBirds().start();
    }

    public void start() throws Exception{

        index = Integer.parseInt(in.readLine().split("\\s")[1]);
        Arrays.fill(statuses, Status.SEARCHING);

        while(true){
            String[] input = in.readLine().split("\\s");
            if (input[0].equals(ENDTURN) || input[0].equals(ENDDAY)){
                updateStatus(input);
            } else if (input[0].equals(EXIT)){
                return;
            } else if (input[0].equals(STARTDAY)){
                updateDay(input);
            } else if (input[0].equals(STARTTURN)){
                updateTurn(input);
                doTurn(input);
            }

        }

    }

    protected void updateStatus(String[] input){
        if (allStatuses == null && input[0].equals(ENDTURN)){
            allStatuses = new Status[input.length - 2][5];
            for (Status[] enemyStatus : allStatuses){
                Arrays.fill(enemyStatus, Status.SEARCHING);
            }
            howManyTeams = input.length - 2;
            howManyEnemyTeams = input.length - 3;
            howManyTotalPlayers = howManyTeams * 5;
            howManyTotalAlivePlayers = howManyTotalPlayers;
            howManyTotalSearchingPlayers = howManyTotalAlivePlayers;
            howManyAliveEnemyPlayers = howManyTotalPlayers - 5;
            howManyEnemyPlayers = howManyEnemyTeams * 5;
            howManyOwnAlivePlayers = 5;
            howManyOwnSearchingPlayers = 5;
            howManySearchingEnemyPlayers = howManyAliveEnemyPlayers;
        }
        for ( int j = 0; j < howManyTeams; j++){
            String[] stats = input[j + 2].split(",");
            for(int i = 0; i < 5; i++){
                switch (stats[i]){
                    case "R":
                    case "N":
                        if (allStatuses[j][i] != Status.RETURNED){
                            howManyTotalReturnedPlayers++;
                            howManyTotalSearchingPlayers--;
                            if (j == index - 1) {
                                howManyOwnReturnedPlayers++;
                                howManyOwnSearchingPlayers--;
                            } else {
                                howManyReturnedEnemyPlayers++;
                                howManySearchingEnemyPlayers--;
                            }
                        }
                        allStatuses[j][i] = Status.RETURNED;
                        break;
                    case "A":
                    case "S":
                        if (allStatuses[j][i] != Status.SEARCHING){
                            howManyTotalReturnedPlayers--;
                            howManyTotalSearchingPlayers++;
                            if (j == index - 1) {
                                howManyOwnReturnedPlayers--;
                                howManyOwnSearchingPlayers++;
                            } else {
                                howManyReturnedEnemyPlayers--;
                                howManySearchingEnemyPlayers++;
                            }
                        }
                        allStatuses[j][i] = Status.SEARCHING;
                        break;
                    case "r":
                    case "D":
                        if (allStatuses[j][i] != Status.DEAD){
                            howManyTotalAlivePlayers--;
                            howManyTotalDeadPlayers++;
                            howManyHaveDiedToday++;
                            howManyTotalSearchingPlayers--;
                            if (j == index - 1){
                                howManyOwnAlivePlayers--;
                                howManyOwnDeadPlayers++;
                                howManyOwnSearchingPlayers--;
                            } else {
                                howManyAliveEnemyPlayers--;
                                howManyDeadEnemyPlayers++;
                                howManySearchingEnemyPlayers--;
                            }
                        }
                        allStatuses[j][i] = Status.DEAD;
                        break;
                    default:
                        break;
                }
            }
        }
        statuses = allStatuses[index - 1];
        if (input[0].equals(ENDTURN)){
            allDayStatuses.add(allStatuses.clone());
        }
        if (input[0].equals(ENDDAY)){
            Status[][] statusesToAdd = new Status[howManyTeams][5];
            for (int i = 0; i < statusesToAdd.length; i++){
                for (int j = 0; j < statusesToAdd[i].length; j++){
                    if (allStatuses[i][j] == Status.SEARCHING){
                        statusesToAdd[i][j] = Status.RETURNED;
                    } else {
                        statusesToAdd[i][j] = Status.DEAD;
                    }
                }
            }
            while (turnNum <= 30){
                allDayStatuses.add(statusesToAdd.clone());
                turnNum++;
            }
            allTimeStatuses.add(allDayStatuses);
        }
    }

    protected void updateDay(String[] input) throws Exception{
        day = Integer.parseInt(input[1].split("/")[0]);
        howManyWillDieTodayAtMost = Integer.parseInt(input[1].split("/")[1]);
        howManyHaveDiedToday = 0;
        allDayStatuses = new ArrayList<>();
        if (day == 1){
            Arrays.fill(statuses, Status.SEARCHING);
            howManyOwnAlivePlayers = 5;
            howManyOwnSearchingPlayers = 5;
        }
    }

    protected void updateTurn(String[] input){
        turnNum = Integer.parseInt(input[1]);
    }

    protected void doTurn(String[] input){
        Move[] moves = new Move[5];
        for (int i = 0; i < 5; i++){
            if (statuses[i] == Status.DEAD ||
                        statuses[i] == Status.RETURNED) {
                moves[i] = Move.NOTHING;
                continue;
            } else {
                moves[i] = doMove(i);
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

    //Implement this method differently for different 
    //strategies. 
    public Move doMove(int playerNumber){
        if (turnNum >= 15){
            return Move.RETURN;
        }
        return Move.SEARCH;
    }

    /**
     * Returns the status of one of your players. 
     * Your players have numbers 1 to 5 inclusive.
     * Throws exception if number is outside range.
     * 
     */
    protected Status getStatus(int player){
        if (player > 5 || player < 1){
            throw new IllegalArgumentException(
                    "getStatus(" + player +") failed.");
        }
        return statuses[player - 1];
    }

    /**
     * Returns the status of a player in a team.
     * Team numbers start with 1 inclusive.
     * Players have numbers 1 to 5 inclusive.
     * Throws exception if argument player is outside range.
     * Throws exception if argument team is less than 1 or is greater
     * than the number of teams.
     * Returns Status.SEARCHING if day == 1 and turnNum == 1 and argument 
     * team >= 1.
     */
    protected Status getStatus(int team, int player){
        if (team < 1 || player < 1 || player > 1 || 
                (team > howManyTeams && day == 1 && turnNum == 1)){
            throw new IllegalArgumentException(
                    "getStatus(" + team + ", " + player + ") failed.");
        }
        if (day == 1 && turnNum == 1 && team >= 1){
            return Status.SEARCHING;
        }
        return allStatuses[team - 1][player - 1];
    }

    /**
     * Returns the status of a player in a team at the end of argument
     * turn.
     * Team numbers start with 1 inclusive.
     * Players have numbers 1 to 5 inclusive.
     * Turns have numbers 0 to 30 inclusive.
     * Status at turn 0 is equal to status at start of turn 1.
     * Throws exception if argument turn hasn't happened yet.
     * Throws exception if argument player is outside range.
     * Throws exception if argument team is less than 1 or is greater
     * than the number of teams.
     */
    protected Status getStatus(int turn, int team, int player){
        if (turn == 0){
            if (day == 1){
                return Status.SEARCHING;
            } else {
                return getStatus(day - 1, 30, team, player);
            }
        }
        if (turnNum <= turn || turn < 0|| player > 5 || player < 1 ||
                team < 1 || team > howManyTeams){
            throw new IllegalArgumentException("getStatus(" + turn + 
                    ", " + team + ", " + player + ") failed.");
        }
        return allDayStatuses.get(turn - 1)[team - 1][player - 1];
    }

    /**
     * Returns the status of a player in a team at the end of argument
     * turn on the day of argument day.
     * Team numbers start with 1 inclusive.
     * Players have numbers 1 to 5 inclusive.
     * Turns have numbers 0 to 30 inclusive.
     * Days have numbers 1 inclusive and up.
     * Status at turn 0 is equal to status at start of turn 1.
     * Throws exception if argument day hasn't ended yet or is less 
     * than one.
     * Throws exception if argument turn is out of range.
     * Throws exception if argument player is outside range.
     * Throws exception if argument team is less than 1 or is greater
     * than the number of teams.
     */
    protected Status getStatus(int day, int turn, int team, int player){
        if (turn == 0){
            if (day == 1){
                return Status.SEARCHING;
            } else {
                return getStatus(day - 1, 30, team, player);
            }
        }
        if (this.day <= day || day < 1 || turn > 30 || turn < 0 || 
                player > 5 || player < 1 ||
                team < 1 || team > howManyTeams){
            throw new IllegalArgumentException("getStatus(" + day + ", "
                    + turn + ", " + team + ", " + player + ") failed.");
        }
        return allTimeStatuses.get(day - 1).get(turn - 1)[team - 1][player - 1];
    }

}
