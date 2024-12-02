package ui;

import chess.*;
import client.ResponseException;
import websocket.WebSocketFacade;
import model.AuthData;

import java.util.Scanner;

public class GamePlayREPL {

    private final WebSocketFacade webSocketFacade;
    private final Scanner scanner;
    private final ChessBoardRender boardRenderer;
    private final ChessGame.TeamColor playerColor;
    private final AuthData authData;
    private final int gameID;

    public GamePlayREPL(WebSocketFacade webSocketFacade, Scanner scanner, ChessGame.TeamColor playerColor,
                        AuthData authData, int gameID) {
        this.webSocketFacade = webSocketFacade;
        this.scanner = scanner;
        this.boardRenderer = new ChessBoardRender(new ChessGame());
        this.playerColor = playerColor;
        this.authData = authData;
        this.gameID = gameID;
    }

    public void start() throws InvalidMoveException {
        boolean inGame = true;

        drawBoard();

        while (inGame) {
            System.out.println("\nEnter a command (help, redraw chess board, make move, resign," +
                    "highlight legal moves, leave):");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "help" -> displayHelp();
                case "redraw chess board" -> drawBoard();
                case "make move" -> makeMove();
                case "resign" -> resign();
                case "highlight legal moves" -> highlightLegalMoves();
                case "leave" -> {
                    leaveGame();
                    inGame = false;
                }
                default -> System.out.println("Unknown command.");
            }
        }
    }

    private void drawBoard() {
        boolean whiteAtBottom = playerColor == ChessGame.TeamColor.WHITE;
        boardRenderer.renderBoard(whiteAtBottom, null);
    }

    private void makeMove() {
        System.out.print("Enter your move (e.g., e2 e4): ");
        String moveInput = scanner.nextLine().trim();
        try {
            ChessMove move = ChessMove.parseMove(moveInput);

            if (requiresPromotion(move)) {
                move = handlePromotion(move);
            }

            webSocketFacade.sendMakeMoveCommand(authData.authToken(), gameID, move);
            System.out.println("Move sent successfully.");
        } catch (ResponseException e) {
            System.out.println("Failed to make move: " + e.getMessage());
        } catch (InvalidMoveException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean requiresPromotion(ChessMove move) {
        ChessPiece piece = boardRenderer.getChessGame().getBoard().getPiece(move.getStartPosition());
        return piece != null && piece.getPieceType() == ChessPiece.PieceType.PAWN &&
                (move.getEndPosition().getRow() == 1 || move.getEndPosition().getRow() == 8);
    }

    private ChessMove handlePromotion(ChessMove move) {
        System.out.println("Choose a promotion piece (Q, R, B, K): ");
        String promotionInput = scanner.nextLine().trim().toUpperCase();

        ChessPiece.PieceType promotionPiece = switch (promotionInput) {
            case "Q" -> ChessPiece.PieceType.QUEEN;
            case "R" -> ChessPiece.PieceType.ROOK;
            case "B" -> ChessPiece.PieceType.BISHOP;
            case "K" -> ChessPiece.PieceType.KNIGHT;
            default -> {
                System.out.println("Invalid choice, defaulting to Queen.");
                yield ChessPiece.PieceType.QUEEN;
            }
        };
        return new ChessMove(move.getStartPosition(), move.getEndPosition(), promotionPiece);
    }

    private void resign() {
        try {
            webSocketFacade.sendResignCommand(null, 0);
            System.out.println("You resigned from the game.");
        } catch (ResponseException e) {
            System.out.println("Failed to resign: " + e.getMessage());
        }
    }

    private void highlightLegalMoves() throws InvalidMoveException {
        System.out.print("Enter the position of the piece (e.g., e2): ");
        String positionInput = scanner.nextLine().trim();
        ChessPosition position = ChessPosition.parsePosition(positionInput);

        System.out.println("Highlighting legal moves for: " + position);
        // Implementation for highlighting legal moves.
    }

    private void leaveGame() {
        try {
            webSocketFacade.sendLeaveCommand(null, 0);
            System.out.println("You left the game.");
        } catch (ResponseException e) {
            System.out.println("Failed to leave game: " + e.getMessage());
        }
    }

    private void displayHelp() {
        System.out.println("""
                Commands:
                - help: Display this help message.
                - redraw chess board: Redraw the current chess board.
                - make move: Input a move to make in the game.
                - resign: Resign from the game.
                - highlight legal moves: Highlight possible moves for a selected piece.
                - leave: Leave the game.
                """);
    }
}

