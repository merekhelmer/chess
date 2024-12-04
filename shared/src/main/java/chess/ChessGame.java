package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor teamTurn;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard(); // default starting positions
        this.teamTurn = TeamColor.WHITE; // White team goes first
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.teamTurn;
    }

    /**
     * Set's which team's turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Gets valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }

        Collection<ChessMove> potentialMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();

        // castling logic for King
        if (piece.getPieceType() == ChessPiece.PieceType.KING && !piece.hasMoved()) {
            // castling conditions for both sides
            if (canCastle(startPosition, true)) {
                validMoves.add(new ChessMove(startPosition, new ChessPosition(startPosition.getRow(), startPosition.getColumn() + 2), null));
            }
            if (canCastle(startPosition, false)) {
                validMoves.add(new ChessMove(startPosition, new ChessPosition(startPosition.getRow(), startPosition.getColumn() - 2), null));
            }
        }

        for (ChessMove move : potentialMoves) {
            ChessPiece originalEndPiece = board.getPiece(move.getEndPosition());
            ChessPiece originalStartPiece = board.getPiece(move.getStartPosition());

            // make move temporarily on the board
            board.addPiece(move.getEndPosition(), originalStartPiece);
            board.addPiece(move.getStartPosition(), null);

            if (!isInCheck(originalStartPiece.getTeamColor())) {
                validMoves.add(move);
            }

            // undo the move
            board.addPiece(move.getStartPosition(), originalStartPiece);
            board.addPiece(move.getEndPosition(), originalEndPiece);
        }
        return validMoves;
    }

    private boolean canCastle(ChessPosition kingPosition, boolean isKingSide) {
        TeamColor teamColor = board.getPiece(kingPosition).getTeamColor();
        int row = kingPosition.getRow();

        // find the rook
        int rookColumn = isKingSide ? 8 : 1;
        ChessPiece rook = board.getPiece(new ChessPosition(row, rookColumn));
        if (rook == null || rook.getPieceType() != ChessPiece.PieceType.ROOK || rook.hasMoved()) {
            return false;
        }

        // check clear path between King and Rook
        int[] columnsToCheck = isKingSide ? new int[]{6, 7} : new int[]{2, 3, 4};
        for (int col : columnsToCheck) {
            if (board.getPiece(new ChessPosition(row, col)) != null) {
                return false;
            }
        }

        // king does not pass through position under attack
        int[] kingPathColumns = isKingSide ? new int[]{5, 6, 7} : new int[]{5, 4, 3};
        for (int col : kingPathColumns) {
            ChessPosition newPosition = new ChessPosition(row, col);
            if (isInCheckAfterMove(kingPosition, newPosition, teamColor)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if a move puts the king in check.
     */
    private boolean isInCheckAfterMove(ChessPosition from, ChessPosition to, TeamColor teamColor) {
        ChessPiece originalEndPiece = board.getPiece(to);
        ChessPiece movingPiece = board.getPiece(from);

        board.addPiece(to, movingPiece);
        board.addPiece(from, null);

        boolean isInCheck = isInCheck(teamColor);

        // restore original
        board.addPiece(from, movingPiece);
        board.addPiece(to, originalEndPiece);

        return isInCheck;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece pieceToMove = board.getPiece(move.getStartPosition());

        if (pieceToMove == null || pieceToMove.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("It's not the turn of the player attempting to make this move.");
        }

        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());

        if (validMoves == null || !validMoves.contains(move)) {
            throw new InvalidMoveException("The move is invalid.");
        }

        // castling
        if (pieceToMove.getPieceType() == ChessPiece.PieceType.KING &&
                Math.abs(move.getEndPosition().getColumn() - move.getStartPosition().getColumn()) == 2) {
            performCastling(move, pieceToMove);

        } else {
            // normal move
            board.addPiece(move.getEndPosition(), pieceToMove);
            board.addPiece(move.getStartPosition(), null);
        }

        // promotion if necessary
        if (move.getPromotionPiece() != null) {
            ChessPiece promotedPiece = new ChessPiece(teamTurn, move.getPromotionPiece());
            board.addPiece(move.getEndPosition(), promotedPiece);
        } else {
            // chess piece movement status
            pieceToMove.setHasMoved(true);
        }

        // change the turn
        teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    private void performCastling(ChessMove move, ChessPiece king) {
        // determine if king-side or queen-side
        boolean isKingSide = move.getEndPosition().getColumn() > move.getStartPosition().getColumn();
        int rookStartColumn = isKingSide ? 8 : 1;
        int rookEndColumn = isKingSide ? move.getEndPosition().getColumn() - 1 : move.getEndPosition().getColumn() + 1;

        ChessPosition rookStartPos = new ChessPosition(move.getStartPosition().getRow(), rookStartColumn);
        ChessPosition rookEndPos = new ChessPosition(move.getStartPosition().getRow(), rookEndColumn);

        // king
        board.addPiece(move.getEndPosition(), king);
        board.addPiece(move.getStartPosition(), null);
        king.setHasMoved(true);

        // rook
        ChessPiece rook = board.getPiece(rookStartPos);
        if (rook != null && rook.getPieceType() == ChessPiece.PieceType.ROOK) {
            board.addPiece(rookEndPos, rook);
            board.addPiece(rookStartPos, null);
            rook.setHasMoved(true);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKingPosition(teamColor);

        // Iterate over opposing pieces to determine if any could capture the king
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);

                if (piece == null || piece.getTeamColor() == teamColor) {
                    continue;
                }

                Collection<ChessMove> moves = piece.pieceMoves(board, pos);
                for (ChessMove move : moves) {
                    if (move.getEndPosition().equals(kingPosition)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }

        return findPiece(teamColor);
    }

    private boolean findPiece(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(pos);
                    if (moves != null && !moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }

        return findPiece(teamColor);
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Finds the king's position for the specified team
     *
     * @param color the team color
     * @return the position of the king
     */
    private ChessPosition findKingPosition(TeamColor color) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == color && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return pos;
                }
            }
        }
        return null;
    }
    
    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }
}
