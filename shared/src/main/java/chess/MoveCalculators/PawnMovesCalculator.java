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
                {1, 0},  //forward for white pawns
                {1, 1},  //capture diagonally right for white
                {1, -1}  //capture diagonally left for white
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
        int direction = color == ChessGame.TeamColor.WHITE ? 1 : -1;  //white move up, black pawns move down

        //regular forward move
        ChessPosition forwardPosition = new ChessPosition(position.getRow() + direction, position.getColumn());
        if (isValidPosition(forwardPosition) && board.getPiece(forwardPosition) == null) {
            //if pawn can be promoted
            if (canPromote(forwardPosition, color)) {
                addPromotionMoves(validMoves, position, forwardPosition);
            } else {
                validMoves.add(new ChessMove(position, forwardPosition, null));
            }

            //check for two-square move from starting position
            if ((color == ChessGame.TeamColor.WHITE && position.getRow() == 2) ||
                    (color == ChessGame.TeamColor.BLACK && position.getRow() == 7)) {
                ChessPosition twoStepPosition = new ChessPosition(position.getRow() + (2 * direction), position.getColumn());
                if (isValidPosition(twoStepPosition) && board.getPiece(twoStepPosition) == null) {
                    validMoves.add(new ChessMove(position, twoStepPosition, null));
                }
            }
        }

        //capture moves (diagonal)
        ChessPosition captureRight = new ChessPosition(position.getRow() + direction, position.getColumn() + 1);
        ChessPosition captureLeft = new ChessPosition(position.getRow() + direction, position.getColumn() - 1);

        pawnCapture(board, position, validMoves, myPiece, color, captureRight);

        pawnCapture(board, position, validMoves, myPiece, color, captureLeft);

        return validMoves;
    }

    private void pawnCapture(ChessBoard board, ChessPosition position, Collection<ChessMove> validMoves, ChessPiece myPiece, ChessGame.TeamColor color, ChessPosition captureRight) {
        if (isValidPosition(captureRight)) {
            ChessPiece rightPiece = board.getPiece(captureRight);
            if (rightPiece != null && !rightPiece.getTeamColor().equals(myPiece.getTeamColor())) {
                if (canPromote(captureRight, color)) {
                    addPromotionMoves(validMoves, position, captureRight);
                } else {
                    validMoves.add(new ChessMove(position, captureRight, null));
                }
            }
        }
    }

    private boolean canPromote(ChessPosition position, ChessGame.TeamColor color) {
        return (color == ChessGame.TeamColor.WHITE && position.getRow() == 8) ||
                (color == ChessGame.TeamColor.BLACK && position.getRow() == 1);
    }

    private void addPromotionMoves(Collection<ChessMove> validMoves, ChessPosition start, ChessPosition end) {
        validMoves.add(new ChessMove(start, end, ChessPiece.PieceType.QUEEN));
        validMoves.add(new ChessMove(start, end, ChessPiece.PieceType.ROOK));
        validMoves.add(new ChessMove(start, end, ChessPiece.PieceType.BISHOP));
        validMoves.add(new ChessMove(start, end, ChessPiece.PieceType.KNIGHT));
    }

    private boolean isValidPosition(ChessPosition position) {
        return position.getRow() >= 1 && position.getRow() <= 8 && position.getColumn() >= 1 && position.getColumn() <= 8;
    }
}

