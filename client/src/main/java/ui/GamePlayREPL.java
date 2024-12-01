package ui;

import chess.*;
import client.ResponseException;
import client.ServerFacade;
import model.AuthData;
import websocket.WebSocketFacade;
import websocket.messages.ServerMessage;

import java.util.Scanner;

public class GamePlayREPL {

    private final WebSocketFacade webSocketFacade;
    private final Scanner scanner;
    private final ChessBoardRender boardRenderer;
    private final ChessGame.TeamColor playerColor;
    private final int gameID;

    public GamePlayREPL(WebSocketFacade webSocketFacade, Scanner scanner, AuthData authData,
                        ChessGame.TeamColor playerColor, int gameID) {
        this.webSocketFacade = webSocketFacade;
        this.scanner = scanner;
        this.boardRenderer = new ChessBoardRender(new ChessGame());
        this.playerColor = playerColor;
        this.gameID = gameID;
    }

    public void start() {
        boolean inGame = true;

        boolean whiteAtBottom = playerColor == ChessGame.TeamColor.WHITE;
        boardRenderer.renderBoard(whiteAtBottom, null);

        while (inGame) {
            System.out.println("\nEnter a command (help, redraw chess board, make move, resign, highlight " +
                    "legal moves, leave):");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "help" -> displayHelp();
                case "redraw chess board" -> redrawChessBoard();
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

    public void redrawChessBoard() {
        boolean whiteAtBottom = playerColor == ChessGame.TeamColor.WHITE;
        boardRenderer.renderBoard(whiteAtBottom, null);
    }
}
