package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Set;

public class ChessBoardRender {
    private ChessGame chessGame;

    public ChessBoardRender(ChessGame chessGame) {
        this.chessGame = chessGame;
    }

    public void setChessGame(ChessGame chessGame) {
        this.chessGame = chessGame;
    }

    public void renderBoard(boolean whiteAtBottom, Set<ChessPosition> highlightedPositions) {
        // clear the terminal screen
        System.out.print(EscapeSequences.ERASE_SCREEN);
        System.out.flush();

        ChessBoard board = chessGame.getBoard();
        if (board == null) {
            System.out.println("Cannot render board: board is null.");
            return;
        }

        printColumnLabels(whiteAtBottom);

        // print each row of the board
        for (int row = 8; row >= 1; row--) {
            int actualRow = whiteAtBottom ? row : 9 - row;
            renderRow(actualRow, whiteAtBottom, board, highlightedPositions);
        }

        printColumnLabels(whiteAtBottom);

        // reset any lingering styles
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
        System.out.print(EscapeSequences.RESET_BG_COLOR);
    }

    private void renderRow(int rowNumber, boolean whiteAtBottom, ChessBoard board, Set<ChessPosition> highlightedPositions) {
        // print row number at the start
        System.out.print(" " + rowNumber + " ");

        for (int col = 1; col <= 8; col++) {
            int actualCol = whiteAtBottom ? col : 9 - col;
            ChessPosition position = new ChessPosition(rowNumber, actualCol);
            ChessPiece piece = board.getPiece(position);

            // determine if the square is light or dark
            boolean isLightSquare = (rowNumber + actualCol) % 2 != 0;

            // highlighting logic
            if (highlightedPositions != null && highlightedPositions.contains(position)) {
                System.out.print(EscapeSequences.SET_BG_COLOR_YELLOW);
            } else if (isLightSquare) {
                System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_SQUARE);
            } else {
                System.out.print(EscapeSequences.SET_BG_COLOR_DARK_SQUARE);
            }

            // piece with appropriate coloring
            if (piece != null) {
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
                } else {
                    System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
                }
                System.out.print(getPieceSymbol(piece));
            } else {
                System.out.print("   ");
            }

            System.out.print(EscapeSequences.RESET_TEXT_COLOR);
            System.out.print(EscapeSequences.RESET_BG_COLOR);
        }

        System.out.println(" " + rowNumber);
    }

    private void printColumnLabels(boolean whiteAtBottom) {
        System.out.print("   "); // spacing for row numbers

        if (whiteAtBottom) {
            for (char c = 'a'; c <= 'h'; c++) {
                System.out.print(" " + c + " ");
            }
        } else {
            for (char c = 'h'; c >= 'a'; c--) {
                System.out.print(" " + c + " ");
            }
        }

        System.out.println();
    }

    private String getPieceSymbol(ChessPiece piece) {
        if (piece == null) {
            return EscapeSequences.EMPTY;
        }

        return switch (piece.getPieceType()) {
            case KING ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            case QUEEN ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case BISHOP ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case KNIGHT ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case ROOK ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case PAWN ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
        };
    }
}