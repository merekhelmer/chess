package chess.MoveCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public abstract class PieceMovesCalculator {

    //each piece provides own set of directions
    protected abstract int[][] getDirections();

    //subclasses customize how far a piece can move
    protected int maxDistance() {
        return 7;  //default: entire board
    }

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


                if (row < 1 || row > 8 || col < 1 || col > 8) {
                    break;
                }

                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece pieceAtPosition = board.getPiece(newPosition);

                if (pieceAtPosition == null) {
                    //no blocking piece, add this move
                    validMoves.add(new ChessMove(position, newPosition, null));
                } else if (!pieceAtPosition.getTeamColor().equals(myPiece.getTeamColor())) {
                    //capture opponent's piece
                    validMoves.add(new ChessMove(position, newPosition, null));
                    break;  //stop after capturing
                } else {
                    //blocked by friendly piece
                    break;
                }
            }
        }

        return validMoves;
    }
}


