package players.Evolved;

import players.MorningBirds.*;
import java.util.*;

public class Evolved extends MorningBirds{

    List<Integer> scrambled = new ArrayList<>();

    public static void main(String[] args) throws Exception{
        new Evolved().start();
    }

    public Evolved() throws Exception{
        super();
    }

    @Override
    public MorningBirds.Move doMove(int playerNum){
        if (!(howManyTotalSearchingPlayers < (turnNum - getScrambled(index)))){
            return Move.SEARCH;
        } else {
            return Move.RETURN;
        }
    }

    @Override
    protected void updateStatus(String[] input){
        super.updateStatus(input);
        if (input[0].equals(ENDTURN) && (Integer.parseInt(input[1]) == 1)){
            for (int i = 1; i <= howManyTeams; i++){
                scrambled.add(i);
            }
            Collections.shuffle(scrambled);
        }
    } 

    public int getScrambled(int in){
        if (in > scrambled.size() || in < 1 ){
            return in;
        }
        return scrambled.get(in - 1);
    }
}
