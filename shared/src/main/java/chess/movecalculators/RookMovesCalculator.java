package chess.movecalculators;

public class RookMovesCalculator extends PieceMovesCalculator {

    @Override
    protected int[][] getDirections() {
        return ORTHOGONAL_DIRECTIONS;
    }

}

