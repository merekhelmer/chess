package chess.movecalculators;

public class RookMovesCalculator extends PieceMovesCalculator {

    @Override
    protected int[][] getDirections() {
        return new int[][]{
                {1, 0}, {-1, 0}, {0, 1}, {0, -1}  //horizontal and vertical moves
        };
    }
}

