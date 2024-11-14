package ui;

import client.ServerFacade;
import client.ResponseException;
import model.AuthData;
import chess.*;

import java.util.Scanner;

public class GamePlayREPL {

    private final ServerFacade serverFacade;
    private final Scanner scanner;
    private final AuthData authData;
    private final int gameID;
    private final ChessGame.TeamColor playerColor;
    private ChessBoardRender boardRenderer;

    public GamePlayREPL(ServerFacade serverFacade, Scanner scanner, AuthData authData, int gameID, ChessGame.TeamColor playerColor) {
        this.serverFacade = serverFacade;
        this.scanner = scanner;
        this.authData = authData;
        this.gameID = gameID;
        this.playerColor = playerColor;

        ChessGame chessGame = serverFacade.getGameState(gameID, authData.authToken());
        this.boardRenderer = new ChessBoardRender(chessGame);
    }

    public void displayInitialGame() {
        if (boardRenderer == null) {
            System.out.println("Cannot display board: game state not available.");
            return;
        }
        boolean whiteAtBottom = true; // Default orientation
        if (playerColor != null) {
            whiteAtBottom = playerColor == ChessGame.TeamColor.WHITE;
        }
        System.out.println("\nInitial board state:");
        boardRenderer.renderBoard(whiteAtBottom, null);
    }
}
