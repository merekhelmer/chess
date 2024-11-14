package ui;

import chess.ChessGame;
import client.ResponseException;
import client.ServerFacade;
import model.AuthData;

import java.util.Scanner;

public class GamePlayREPL {

    private final ChessGame.TeamColor playerColor;
    private final ChessBoardRender boardRenderer;

    public GamePlayREPL(ServerFacade serverFacade, Scanner scanner, AuthData authData, int gameID, ChessGame.TeamColor playerColor) throws ResponseException {
        this.playerColor = playerColor;

        ChessGame chessGame = serverFacade.getGameState(gameID, authData.authToken());
        this.boardRenderer = new ChessBoardRender(chessGame);
    }

    public void displayInitialGame() {
        boolean whiteAtBottom = playerColor == ChessGame.TeamColor.WHITE;
        System.out.println("\nInitial board state:");
        boardRenderer.renderBoard(whiteAtBottom, null);
    }
}
