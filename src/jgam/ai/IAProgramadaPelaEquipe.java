package jgam.ai;

import java.util.Iterator;
import java.util.List;
import jgam.game.BoardSetup;
import jgam.game.PossibleMoves;
import jgam.game.SingleMove;

public class IAProgramadaPelaEquipe implements AI {
    private final int profundidadeMax = 1;
    
    public void init() throws Exception {
    }

    public void dispose() {
    }

    public String getName() {
        return "IA programada pela equipe";
    }

    public String getDescription() {
        return "IA programada pela equipe";
    }
    
    private final int earlyThreshold = 240;
    
    private final int[] weightMapEarly = {
            0,
            0,50,50,50,50,50,
            50,50,40,35,35,35,
            30,25,25,25,20,0,
            0,0,0,0,0,0,
            0};
    
    private final int[] weightMapEnd = {
            0,
            0,0,0,0,0,0,
            0,5,10,15,20,25,
            30,35,40,40,40,40,
            35,35,25,25,25,25,
            100};
    
    private double heuristic(BoardSetup bs) {
        double evaluation = 0;
        
        int player1 = 1;
        int player2 = 2;
        boolean p1IsEarly = false;
        boolean p2IsEarly = false;
        
        for (int i = 0; i <= 6; i++) {
            if (bs.getPoint(player1, i) > 0) p1IsEarly = true;
            if (bs.getPoint(player2, i) > 0) p2IsEarly = true;
        }
        for (int i = 0; i <= 25; i++) {
            int p1Checkers = bs.getPoint(player1, i);
            int p2Checkers = bs.getPoint(player2, i);
            
            evaluation += (p1IsEarly) ?
                    p1Checkers * weightMapEarly[i] :
                    p1Checkers * weightMapEnd[i];
            
            evaluation -= (p2IsEarly) ?
                    p2Checkers * weightMapEarly[25-i] :
                    p2Checkers * weightMapEnd[25-i];
            
            if (p1Checkers == 1)
                evaluation -= 5*i;
            else if (p1Checkers == 2)
                evaluation += 50;
            else
                evaluation -= 75 * (p1Checkers - 2);
            
            if (p2Checkers == 1)
                evaluation += 5*(25-i);
            else if (p2Checkers == 2)
                evaluation -= 50;
            else
                evaluation += 75 * (p2Checkers - 2);
        }
        return evaluation;
    }

    public SingleMove[] makeMoves(BoardSetup bs) throws CannotDecideException {
        if (bs.getActivePlayer() == 2)
            return miniMax(bs,
                    MoveType.MIN,
                    (double)0,
                    Double.NEGATIVE_INFINITY,
                    Double.POSITIVE_INFINITY).second;
        else
            return miniMax(bs,
                    MoveType.MAX,
                    (double)0,
                    Double.NEGATIVE_INFINITY,
                    Double.POSITIVE_INFINITY).second;
    }
    
    private Pair<Double, SingleMove[]> valorMax(
            BoardSetup bs,
            double profundidade,
            double alpha,
            double beta
    ){
        if (profundidade == profundidadeMax)
            return new Pair((double)heuristic(bs), null);
        
        double max = Double.NEGATIVE_INFINITY;
        
        PossibleMoves pm = new PossibleMoves(bs);
        List moveList = pm.getPossbibleNextSetups();
        
        int i = 0;
        int index = -1;
        for (Iterator iter = moveList.iterator(); iter.hasNext(); i++) {
            BoardSetup boardSetup = (BoardSetup) iter.next();
            
            double opponentEval = miniMax(
                    boardSetup,
                    MoveType.CHANCE_TO_MIN,
                    profundidade + 1,
                    alpha,
                    beta).first;
            
            if (opponentEval >= beta)
                return new Pair(max, pm.getMoveChain(i));
            
            alpha = Double.max(alpha, max);
            
            if (opponentEval > max) {
                max = opponentEval;
                index = i;
            }
        }
        if (index != -1)
            return new Pair(max, pm.getMoveChain(index));
        else
            return new Pair(max, null);
    }
    
    private Pair<Double, SingleMove[]> valorMin(
            BoardSetup bs,
            double profundidade,
            double alpha,
            double beta
    ){
        if (profundidade == profundidadeMax)
            return new Pair((double)heuristic(bs), null);
        
        double min = Double.POSITIVE_INFINITY;
        
        PossibleMoves pm = new PossibleMoves(bs);
        List moveList = pm.getPossbibleNextSetups();

        int i = 0;
        int index = -1;
        for (Iterator iter = moveList.iterator(); iter.hasNext(); i++) {
            BoardSetup boardSetup = (BoardSetup) iter.next();
        
            double opponentEval = miniMax(
                    boardSetup,
                    MoveType.CHANCE_TO_MAX,
                    profundidade + 1,
                    alpha,
                    beta).first;
            if (opponentEval <= alpha)
                return new Pair(min, pm.getMoveChain(i));
            
            beta = Double.min(beta, min);
            
            if (opponentEval < min){
                min = opponentEval;
                index = i;
            }
        }
        if (index != -1)
            return new Pair(min, pm.getMoveChain(index));
        else
            return new Pair(min, null);
    }
    
    private Pair<Double, SingleMove[]> valorChance(
            BoardSetup bs,
            MoveType nextType,
            double profundidade,
            double alpha,
            double beta
    ){
        double output = 0;
        
        for (int i = 0; i <= 6; i++) {
            for (int j = i; j <= 6; j++) {
                double probability = (i == j) ? 1/36 : 1/18;
                
                int[] dices = {i, j};
                PossibleMoves pm = new PossibleMoves(bs, dices);
                List moveList = pm.getPossbibleNextSetups();
                
                for (Iterator iter = moveList.iterator(); iter.hasNext(); i++) {
                    BoardSetup boardSetup = (BoardSetup) iter.next();

                    double opponentEval = miniMax(
                            boardSetup,
                            nextType,
                            profundidade,
                            alpha,
                            beta).first;
                    
                    output += probability * opponentEval;
                }
            }
        }
        
        return new Pair(output, null);
    }
    
    private Pair<Double, SingleMove[]> miniMax(
            BoardSetup bs,
            MoveType type,
            double profundidade,
            double alpha,
            double beta
    ){
        if (type == MoveType.MIN)
            return valorMin(bs, profundidade, alpha, beta);
        else if (type == MoveType.MAX)
            return valorMax(bs, profundidade, alpha, beta);
        else if (type == MoveType.CHANCE_TO_MAX)
            return valorChance(bs, MoveType.MAX, profundidade, alpha, beta);
        else
            return valorChance(bs, MoveType.MIN, profundidade, alpha, beta);
    }

    public int rollOrDouble(BoardSetup boardSetup) throws CannotDecideException {
        return ROLL;
    }

    public int takeOrDrop(BoardSetup boardSetup) throws CannotDecideException {
        return TAKE;
    }
    
    public static class Pair<K, L> {
        public final K first;
        public final L second;

        public Pair(K first, L second) {
            this.first = first;
            this.second = second;
        }
    }
    
    public static enum MoveType {
        MIN,
        MAX,
        CHANCE_TO_MAX,
        CHANCE_TO_MIN,
    }
}
