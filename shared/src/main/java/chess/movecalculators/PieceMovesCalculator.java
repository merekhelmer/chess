package chess.movecalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public abstract class PieceMovesCalculator {

    // movement directions
    protected static final int[][] ORTHOGONAL_DIRECTIONS = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1}
    };
    protected static final int[][] DIAGONAL_DIRECTIONS = {
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
    };
    protected static final int[][] ALL_DIRECTIONS = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1},
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
    };
    private static final int MIN_ROW = 1;
    private static final int MAX_ROW = 8;
    private static final int MIN_COL = 1;
    private static final int MAX_COL = 8;

    protected abstract int[][] getDirections();

    protected int maxDistance() {
        return 7;
    }

    /**
     * Calculates the possible moves for a piece from a given position.
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece myPiece = board.getPiece(position);

        if (myPiece == null || myPiece.getTeamColor() == null) {
            return validMoves;
        }

        for (int[] direction : getDirections()) {
            int row = position.getRow();
            int col = position.getColumn();

            for (int i = 0; i < maxDistance(); i++) {
                row += direction[0];
                col += direction[1];

                if (row < MIN_ROW || row > MAX_ROW || col < MIN_COL || col > MAX_COL) {
                    break;
                }

                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece pieceAtPosition = board.getPiece(newPosition);

                if (pieceAtPosition == null) {
                    validMoves.add(new ChessMove(position, newPosition, null));
                    continue;
                }

                if (!pieceAtPosition.getTeamColor().equals(myPiece.getTeamColor())) {
                    validMoves.add(new ChessMove(position, newPosition, null));
                }

                break;
            }
        }

        return validMoves;
    }
}
