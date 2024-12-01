package chess;

import java.util.Objects;

import static chess.ChessPosition.parsePosition;

/**
 * Represents moving a chess piece on a chessboard
 */
public class ChessMove {
    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition, ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    private ChessMove parseMove(String input) throws InvalidMoveException {
        String[] parts = input.split(" ");
        if (parts.length < 2 || parts.length > 3) {
            throw new InvalidMoveException("Invalid move format. Use: <start> <end> [promotion]");
        }

        ChessPosition start = parsePosition(parts[0]);
        ChessPosition end = parsePosition(parts[1]);
        ChessPiece.PieceType promotionPiece = parts.length == 3 ? parsePromotionPiece(parts[2]) : null;

        return new ChessMove(start, end, promotionPiece);
    }

    private ChessPiece.PieceType parsePromotionPiece(String input) throws InvalidMoveException {
        return switch (input.toUpperCase()) {
            case "Q" -> ChessPiece.PieceType.QUEEN;
            case "R" -> ChessPiece.PieceType.ROOK;
            case "B" -> ChessPiece.PieceType.BISHOP;
            case "K" -> ChessPiece.PieceType.KNIGHT;
            default -> throw new InvalidMoveException("Invalid promotion piece. Use: Q, R, B, or K.");
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(startPosition, chessMove.startPosition)
                && Objects.equals(endPosition, chessMove.endPosition)
                && Objects.equals(promotionPiece, chessMove.promotionPiece);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPosition, endPosition, promotionPiece);
    }

    @Override
    public String toString() {
        return "ChessMove{" +
                "startPosition=" + startPosition +
                ", endPosition=" + endPosition +
                ", promotionPiece=" + promotionPiece +
                '}';
    }

    public ChessPosition getStartPosition() {
        return startPosition;
    }

    public ChessPosition getEndPosition() {
        return endPosition;
    }

    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }
}
