package chess.movecalculators;

public class KingMovesCalculator extends PieceMovesCalculator {

    @Override
    protected int[][] getDirections() {
        return new int[][]{
                {1, 0}, {-1, 0}, {0, 1}, {0, -1},  //rook-like moves
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1} //bishop-like moves
        };
    }

    @Override
    protected int maxDistance() {
        return 1;
    }
}
