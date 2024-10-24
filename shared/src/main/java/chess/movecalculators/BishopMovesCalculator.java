package chess.movecalculators;

public class BishopMovesCalculator extends PieceMovesCalculator {

    @Override
    protected int[][] getDirections() {
        return new int[][]{
                {1, 1},
                {-1, 1},
                {-1, -1},
                {1, -1}
        };
    }
}

