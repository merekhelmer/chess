package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class ChessBoardRender {

    private final ChessGame chessGame;
    private final PrintStream out;

    private static final int BOARD_SIZE = 8;
    private static final int SQUARE_HEIGHT = 1; // height of each square in lines

    public ChessBoardRender(ChessGame chessGame) {
        this.chessGame = chessGame;
        this.out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    }


    public void renderInitialGame() {
        // black's perspective
        renderBoard(false, null);

        // separator b/w boards
        out.println();

        // white's perspective
        renderBoard(true, null);
    }

    public void renderBoard(boolean whiteAtBottom, ChessPosition selectedPos) {
        // row traversal based on orientation
        int startRow = whiteAtBottom ? 8 : 1;
        int endRow = whiteAtBottom ? 0 : 9;
        int rowStep = whiteAtBottom ? -1 : 1;

        renderColumnLabels(whiteAtBottom);

        // render each row
        for (int row = startRow; row != endRow; row += rowStep) {
            renderRow(row, whiteAtBottom, selectedPos);
        }

        // render col labels again at the bottom
        renderColumnLabels(whiteAtBottom);

        // reset colors
        out.print(EscapeSequences.RESET_TEXT_COLOR);
        out.print(EscapeSequences.RESET_BG_COLOR);
    }

    private void renderRow(int row, boolean whiteAtBottom, ChessPosition selectedPos) {
        out.printf(" %d ", row);

        // col traversal based on orientation
        int startCol = whiteAtBottom ? 1 : BOARD_SIZE;
        int endCol = whiteAtBottom ? BOARD_SIZE + 1 : 0;
        int colStep = whiteAtBottom ? 1 : -1;

        for (int col = startCol; col != endCol; col += colStep) {
            boolean isLightSquare = (row + col) % 2 != 0;

            applySquareColor(row, col, isLightSquare);

            ChessPiece piece = chessGame.getBoard().getPiece(new ChessPosition(row, col));
            String pieceSymbol = getPieceSymbol(piece);
            out.print(pieceSymbol);
        }

        // reset color at the end of the line
        out.print(EscapeSequences.RESET_BG_COLOR);
        out.print(EscapeSequences.RESET_TEXT_COLOR);

        out.printf(" %d%n", row);
    }

    private void renderColumnLabels(boolean whiteAtBottom) {
        out.print("   "); // spacing for row labels

        char startFile = whiteAtBottom ? 'a' : 'h';
        int fileStep = whiteAtBottom ? 1 : -1;

        for (int i = 0; i < BOARD_SIZE; i++) {
            char fileLabel = (char) (startFile + i * fileStep);
            out.printf(" %c ", fileLabel);
        }
        out.println();
    }

    private void applySquareColor(int row, int col, boolean isLightSquare) {
        if (isLightSquare) {
            out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
        } else {
            out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
        }

        // text color for better contrast
        out.print(isLightSquare ? EscapeSequences.SET_TEXT_COLOR_BLACK : EscapeSequences.SET_TEXT_COLOR_WHITE);
    }

    private String getPieceSymbol(ChessPiece piece) {
        if (piece == null) {
            return "   ";
        }

        String pieceLetter = switch (piece.getPieceType()) {
            case KING -> " K ";
            case QUEEN -> " Q ";
            case BISHOP -> " B ";
            case KNIGHT -> " N ";
            case ROOK -> " R ";
            case PAWN -> " P ";
        };

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            out.print(EscapeSequences.SET_TEXT_COLOR_RED);
        } else {
            out.print(EscapeSequences.SET_TEXT_COLOR_BLUE);
        }
        return pieceLetter;
    }
}