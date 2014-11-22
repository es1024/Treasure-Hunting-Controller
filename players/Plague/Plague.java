package players.Plague;

import players.MorningBirds.MorningBirds;

public class Plague extends MorningBirds{

    public static void main(String[] args) throws Exception{
        new Plague().start();
    }

    public Plague() throws Exception{
        super();
    }

    @Override
    public MorningBirds.Move doMove(int playerNum){
        if (day > howManyTotalDeadPlayers){
            return Move.SEARCH;
        } else {
            return Move.RETURN;
        }
    }
}
