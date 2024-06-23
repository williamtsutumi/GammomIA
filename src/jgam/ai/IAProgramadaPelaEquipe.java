package jgam.ai;

import java.util.Iterator;
import java.util.List;
import jgam.game.BoardSetup;
import jgam.game.PossibleMoves;
import jgam.game.SingleMove;

public class IAProgramadaPelaEquipe implements AI {
    private int profundidadeMax = 2;
    
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
    
    private double heuristic(BoardSetup bs) {
        double evaluation = 0.0;
        
        int player1 = 1;
        int player2 = 2;
        
        for (int i = 1; i < 25; i++) {
            int p1Checkers = bs.getPoint(player1, i);
            int p2Checkers = bs.getPoint(player2, i);
            
            evaluation += p1Checkers * i;
            evaluation -= p2Checkers * (25 - i);
            
            if (p1Checkers == 1)
                evaluation -= 10;
            if (p2Checkers == 1)
                evaluation += 10;
        }
        
        return evaluation;
    }

    public SingleMove[] makeMoves(BoardSetup bs) throws CannotDecideException {
        return valorMax(bs, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY).second;
    }
    
    private Pair<Double, SingleMove[]> valorMax(
            BoardSetup bs,
            double profundidade,
            double alpha,
            double beta){
        if (profundidade == profundidadeMax)
            return new Pair(heuristic(bs), new SingleMove[0]);
        
        double max = Double.NEGATIVE_INFINITY;
        
        PossibleMoves pm = new PossibleMoves(bs);
        List moveList = pm.getPossbibleNextSetups();
        if (moveList.size() == 0)
            return new Pair(0, new SingleMove[0]);

        int i = 0;
        int index = 0;
        for (Iterator iter = moveList.iterator(); iter.hasNext(); i++) {
            BoardSetup boardSetup = (BoardSetup) iter.next();
        
            double min = valorMin(boardSetup, profundidade + 1, alpha, beta).first;
            if (min > max) {
                max = min;
                index = i;
            }
        }
        return new Pair(max, pm.getMoveChain(index));
    }
    
    private Pair<Double, SingleMove[]> valorMin(
            BoardSetup bs,
            double profundidade,
            double alpha,
            double beta){
        if (profundidade == profundidadeMax)
            return new Pair((Double)heuristic(bs), new SingleMove[0]);
        
        double min = Double.POSITIVE_INFINITY;
        
        PossibleMoves pm = new PossibleMoves(bs);
        List moveList = pm.getPossbibleNextSetups();
        if (moveList.size() == 0)
            return new Pair(0, new SingleMove[0]);

        int i = 0;
        int index = 0;
        for (Iterator iter = moveList.iterator(); iter.hasNext(); i++) {
            BoardSetup boardSetup = (BoardSetup) iter.next();
        
            double max = valorMax(boardSetup, profundidade + 1, alpha, beta).first;
            if (max < min){
                min = max;
                index = i;
            }
        }
        
        return new Pair(min, pm.getMoveChain(index));
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
}
