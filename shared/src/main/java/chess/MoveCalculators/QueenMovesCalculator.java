package chess.MoveCalculators;

public class QueenMovesCalculator extends PieceMovesCalculator {

    @Override
    protected int[][] getDirections() {
        return new int[][]{
                {1, 0}, {-1, 0}, {0, 1}, {0, -1},
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };
    }
}
