package ui;

import chess.*;
import client.ResponseException;
import model.AuthData;
import websocket.MessageHandler;
import websocket.WebSocketFacade;
import websocket.messages.*;
import com.google.gson.Gson;

import java.util.stream.Collectors;
import java.util.*;

public class GamePlayREPL implements MessageHandler {

    private final WebSocketFacade webSocketFacade;
    private final Scanner scanner;
    private final ChessBoardRender boardRenderer;
    private final ChessGame.TeamColor playerColor;
    private final AuthData authData;
    private final int gameID;
    private volatile ChessGame chessGame;

    public GamePlayREPL(String serverUrl, Scanner scanner, ChessGame.TeamColor playerColor,
                        AuthData authData, int gameID) throws ResponseException {
        this.scanner = scanner;
        this.playerColor = playerColor;
        this.authData = authData;
        this.gameID = gameID;

        // updated when LOAD_GAME is received
        this.chessGame = null;
        this.boardRenderer = new ChessBoardRender(new ChessGame());

        this.webSocketFacade = new WebSocketFacade(serverUrl, this);
        webSocketFacade.sendConnectCommand(authData.authToken(), gameID);
    }

    public void start() {
        boolean inGame = true;

        while (inGame) {
            System.out.println("\nEnter a command (help, redraw chess board, make move, resign, highlight legal moves, leave):");
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

    private void handleServerMessage(String rawMessage) {
        Gson gson = new Gson();
        ServerMessage serverMessage = gson.fromJson(rawMessage, ServerMessage.class);

        switch (serverMessage.getServerMessageType()) {
            case LOAD_GAME -> {
                LoadGameMessage loadGameMessage = gson.fromJson(rawMessage, LoadGameMessage.class);
                this.chessGame = loadGameMessage.getGame();
                boardRenderer.setChessGame(this.chessGame);
                drawBoard();
            }
            case ERROR -> {
                ErrorMessage errorMessage = gson.fromJson(rawMessage, ErrorMessage.class);
                System.out.println("Error: " + errorMessage.getErrorMessage());
            }
            case NOTIFICATION -> {
                NotificationMessage notificationMessage = gson.fromJson(rawMessage, NotificationMessage.class);
                System.out.println("Notification: " + notificationMessage.getMessage());
            }
            default -> System.out.println("Unknown message type received: " + serverMessage.getServerMessageType());
        }
    }

    @Override
    public void onMessage(String rawMessage) {
        handleServerMessage(rawMessage);
    }

    private void drawBoard() {
        if (chessGame == null || chessGame.getBoard() == null) {
            System.out.println("Game state not yet loaded. Please wait...");
            return;
        }
        boolean whiteAtBottom = playerColor == ChessGame.TeamColor.WHITE || playerColor == null;
        boardRenderer.renderBoard(whiteAtBottom, null);
    }

    private void makeMove() {
        if (chessGame == null || chessGame.getBoard() == null) {
            System.out.println("Game state not yet loaded. Please wait...");
            return;
        }

        System.out.print("Enter your move (e.g., e2 e4): ");
        String input = scanner.nextLine().trim();

        try {
            String[] positions = input.split(" ");
            if (positions.length != 2) {
                throw new IllegalArgumentException("Invalid input format. Usage: e2 e4.");
            }

            ChessPosition start = parsePosition(positions[0]);
            ChessPosition end = parsePosition(positions[1]);
            ChessMove move = new ChessMove(start, end, null);

            if (requiresPromotion(move)) {
                move = handlePromotion(move);
            }

            webSocketFacade.sendMakeMoveCommand(authData.authToken(), gameID, move);
            System.out.println("Move sent successfully.");
        } catch (Exception e) {
            System.out.println("Failed to send move: " + e.getMessage());
        }
    }

    private boolean requiresPromotion(ChessMove move) {
        ChessPiece piece = chessGame.getBoard().getPiece(move.getStartPosition());
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

    private ChessPosition parsePosition(String input) {
        if (input.length() != 2) {
            throw new IllegalArgumentException("Invalid position format.");
        }
        char column = input.charAt(0);
        char row = input.charAt(1);

        int colIndex = column - 'a' + 1;
        int rowIndex = Character.getNumericValue(row);

        if (colIndex < 1 || colIndex > 8 || rowIndex < 1 || rowIndex > 8) {
            throw new IllegalArgumentException("Position out of bounds.");
        }

        // adjust for the coordinate system
        return new ChessPosition(rowIndex, colIndex);
    }

    private void resign() {
        try {
            webSocketFacade.sendResignCommand(authData.authToken(), gameID);
            System.out.println("You resigned from the game.");
        } catch (ResponseException e) {
            System.out.println("Failed to resign: " + e.getMessage());
        }
    }

    private void highlightLegalMoves() {
        if (chessGame == null || chessGame.getBoard() == null) {
            System.out.println("Game state not yet loaded. Please wait...");
            return;
        }

        System.out.print("Enter the position of the piece (e.g., e2): ");
        String input = scanner.nextLine().trim();

        try {
            ChessPosition position = parsePosition(input);

            Collection<ChessMove> validMoves = chessGame.validMoves(position);

            if (validMoves == null || validMoves.isEmpty()) {
                System.out.println("No legal moves for the selected piece.");
                return;
            }

            // extract end positions for highlighting
            Set<ChessPosition> highlightPositions = validMoves.stream()
                    .map(ChessMove::getEndPosition)
                    .collect(Collectors.toSet());

            boolean whiteAtBottom = playerColor == ChessGame.TeamColor.WHITE || playerColor == null;
            boardRenderer.renderBoard(whiteAtBottom, highlightPositions);
        } catch (Exception e) {
            System.out.println("Failed to highlight moves: " + e.getMessage());
        }
    }

    private void leaveGame() {
        try {
            webSocketFacade.sendLeaveCommand(authData.authToken(), gameID);
            webSocketFacade.closeConnection();
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
