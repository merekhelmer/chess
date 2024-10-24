package chess.movecalculators;

public class KnightMovesCalculator extends PieceMovesCalculator {

    @Override
    protected int[][] getDirections() {
        return new int[][]{
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1}, //various L-shaped moves
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };
    }

    @Override
    protected int maxDistance() {
        return 1;
    }
}
