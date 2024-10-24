package chess.movecalculators;

public class KingMovesCalculator extends PieceMovesCalculator {

    @Override
    protected int[][] getDirections() {
        return ALL_DIRECTIONS;
    }

    @Override
    protected int maxDistance() {
        return 1;
    }
}
