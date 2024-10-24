package chess.movecalculators;

public class QueenMovesCalculator extends PieceMovesCalculator {

    @Override
    protected int[][] getDirections() {
        return ALL_DIRECTIONS;
    }
}
