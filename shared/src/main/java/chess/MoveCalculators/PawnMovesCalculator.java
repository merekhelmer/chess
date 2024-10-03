package chess.MoveCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.ChessGame;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator extends PieceMovesCalculator {

    @Override
    protected int[][] getDirections() {
        return new int[][]{
                {1, 0},  // forward for white pawns
                {1, 1},  // capture diagonally right for white
                {1, -1}  // capture diagonally left for white
        };
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece myPiece = board.getPiece(position);

        if (myPiece == null || myPiece.getTeamColor() == null) {
            return validMoves;
        }

        ChessGame.TeamColor color = myPiece.getTeamColor();
        int direction = color == ChessGame.TeamColor.WHITE ? 1 : -1;  //white pawns move up, black move down

        //forward moves
        ChessPosition forwardPosition = new ChessPosition(position.getRow() + direction, position.getColumn());
        if (isValidPosition(forwardPosition) && board.getPiece(forwardPosition) == null) {
            addMoveOrPromotion(validMoves, position, forwardPosition, color);

            // two-step moves
            if ((color == ChessGame.TeamColor.WHITE && position.getRow() == 2) ||
                    (color == ChessGame.TeamColor.BLACK && position.getRow() == 7)) {
                ChessPosition twoStepPosition = new ChessPosition(position.getRow() + (2 * direction), position.getColumn());
                if (isValidPosition(twoStepPosition) && board.getPiece(twoStepPosition) == null) {
                    validMoves.add(new ChessMove(position, twoStepPosition, null));
                }
            }
        }

        // capture moves (diagonal)
        int[] diagonalOffsets = {-1, 1};
        for (int offset : diagonalOffsets) {
            ChessPosition capturePosition = new ChessPosition(position.getRow() + direction, position.getColumn() + offset);
            if (isValidPosition(capturePosition)) {
                ChessPiece capturePiece = board.getPiece(capturePosition);
                if (capturePiece != null && !capturePiece.getTeamColor().equals(myPiece.getTeamColor())) {
                    addMoveOrPromotion(validMoves, position, capturePosition, color);
                }
            }
        }

        return validMoves;
    }

    private boolean isValidPosition(ChessPosition position) {
        return position.getRow() >= 1 && position.getRow() <= 8 && position.getColumn() >= 1 && position.getColumn() <= 8;
    }

    private boolean canPromote(ChessPosition position, ChessGame.TeamColor color) {
        return (color == ChessGame.TeamColor.WHITE && position.getRow() == 8) ||
                (color == ChessGame.TeamColor.BLACK && position.getRow() == 1);
    }

    private void addMoveOrPromotion(Collection<ChessMove> validMoves, ChessPosition start, ChessPosition end, ChessGame.TeamColor color) {
        if (canPromote(end, color)) {
            addPromotionMoves(validMoves, start, end);
        } else {
            validMoves.add(new ChessMove(start, end, null));
        }
    }

    private void addPromotionMoves(Collection<ChessMove> validMoves, ChessPosition start, ChessPosition end) {
        validMoves.add(new ChessMove(start, end, ChessPiece.PieceType.QUEEN));
        validMoves.add(new ChessMove(start, end, ChessPiece.PieceType.ROOK));
        validMoves.add(new ChessMove(start, end, ChessPiece.PieceType.BISHOP));
        validMoves.add(new ChessMove(start, end, ChessPiece.PieceType.KNIGHT));
    }
}

