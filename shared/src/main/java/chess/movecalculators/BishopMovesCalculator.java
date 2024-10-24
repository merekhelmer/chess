package chess.movecalculators;

public class BishopMovesCalculator extends PieceMovesCalculator {

    @Override
    protected int[][] getDirections() {
        return DIAGONAL_DIRECTIONS;
    }
}

