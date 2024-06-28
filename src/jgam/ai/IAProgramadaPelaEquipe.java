package jgam.ai;

import java.util.Iterator;
import java.util.List;
import jgam.game.BoardSetup;
import jgam.game.PossibleMoves;
import jgam.game.SingleMove;

public class IAProgramadaPelaEquipe implements AI {
    private final int profundidadeMax = 2;
    
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
    
    private final int earlyThreshold = 260;
    
    private final int[] weightMapEarly = {
            0,
            -50,-30,-20,0,10,20,
            20,20,20,20,25,30,
            30,25,25,25,20,0,
            0,0,0,0,0,0,
            0};
    
    private final int[] weightMapEnd = {
            0,
            0,0,0,0,0,0,
            0,5,10,15,20,25,
            30,35,40,40,40,40,
            40,40,40,40,40,40,
            100};
    
    public Double heuristics(BoardSetup bs) {
        Double evaluation = 0.0;
        
        int player1 = 1;
        int player2 = 2;
        for (int i = 1; i <= 25; i++) {
            int p1Checkers = bs.getPoint(player1, i);
            int p2Checkers = bs.getPoint(player2, i);
            
            evaluation += p1Checkers * 5.0*Integer.min(12, 25-i);
            evaluation -= p2Checkers * 5.0*Integer.min(12, 25-i);
            
            if (p1Checkers == 1)
                evaluation -= 10.0;
            else if (p1Checkers == 2)
                evaluation += 10.0;
            else if (p1Checkers > 2)
                evaluation -= 10.0 * (p1Checkers - 2);
            
            if (p2Checkers == 1)
                evaluation += 10.0;
            else if (p2Checkers == 2)
                evaluation -= 10.0;
            else if (p2Checkers > 2)
                evaluation += 10.0 * (p2Checkers - 2);
        }
        evaluation += 200.0 * bs.getPoint(player1, 0);
        evaluation -= 200.0 * bs.getPoint(player2, 0);

        return evaluation;
    }

    public SingleMove[] makeMoves(BoardSetup bs) throws CannotDecideException {
        if (bs.getActivePlayer() == 2)
            return miniMax(bs, MoveType.MIN, 0).second;
        else {
            Pair<Double, SingleMove[]> out = miniMax(bs, MoveType.MAX, 0);
            System.out.println("Heuristica escolhida: " + out.first);
            return out.second;
        }
            
    }
    
    private Pair<Double, SingleMove[]> valorMax(
            BoardSetup bs,
            int profundidade
    ){
        if (profundidade == profundidadeMax)
            return new Pair(heuristics(bs), null);
        
        Double max = Double.NEGATIVE_INFINITY;
        
        PossibleMoves pm = new PossibleMoves(bs);
        List moveList = pm.getPossbibleNextSetups();
        
        int i = 0;
        int index = -1;
        for (Iterator iter = moveList.iterator(); iter.hasNext(); i++) {
            BoardSetup boardSetup = (BoardSetup) iter.next();
            
            Double opponentEval = miniMax(
                    boardSetup,
                    MoveType.CHANCE_TO_MIN,
                    profundidade + 1).first;
            
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
            int profundidade
    ){
        if (profundidade == profundidadeMax)
            return new Pair(heuristics(bs), null);
        
        Double min = Double.POSITIVE_INFINITY;
        
        PossibleMoves pm = new PossibleMoves(bs);
        List moveList = pm.getPossbibleNextSetups();

        int i = 0;
        int index = -1;
        for (Iterator iter = moveList.iterator(); iter.hasNext(); i++) {
            BoardSetup boardSetup = (BoardSetup) iter.next();
        
            Double opponentEval = miniMax(
                    boardSetup,
                    MoveType.CHANCE_TO_MAX,
                    profundidade + 1).first;
            
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
            int profundidade
    ){
        Double output = 0.0;
        
        for (int i = 0; i <= 6; i++) {
            for (int j = i; j <= 6; j++) {
                double probability = (i == j) ? 1.0/36.0 : 1.0/18.0;
                
                int[] dices = {i, j};
                PossibleMoves pm = new PossibleMoves(bs, dices);
                List moveList = pm.getPossbibleNextSetups();
                
                for (Iterator iter = moveList.iterator(); iter.hasNext(); i++) {
                    BoardSetup boardSetup = (BoardSetup) iter.next();

                    double opponentEval = miniMax(
                            boardSetup,
                            nextType,
                            profundidade + 1).first;
                    
                    output += probability * opponentEval;
                }
            }
        }
        
        return new Pair(output, null);
    }
    
    private Pair<Double, SingleMove[]> miniMax(
            BoardSetup bs,
            MoveType type,
            int profundidade
    ){
        if (type == MoveType.MIN)
            return valorMin(bs, profundidade);
        else if (type == MoveType.MAX)
            return valorMax(bs, profundidade);
        else if (type == MoveType.CHANCE_TO_MAX)
            return valorChance(bs, MoveType.MAX, profundidade);
        else
            return valorChance(bs, MoveType.MIN, profundidade);
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
