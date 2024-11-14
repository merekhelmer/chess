package ui;

import client.*;
import model.AuthData;
import chess.ChessGame;

import java.util.Scanner;

public class GamePlayREPL {

    private final Scanner scanner;
    private final ChessGame.TeamColor playerColor;
    private final ChessBoardRender boardRenderer;

    public GamePlayREPL(ServerFacade serverFacade, Scanner scanner, AuthData authData, int gameID, ChessGame.TeamColor playerColor) {
        this.scanner = scanner;
        this.playerColor = playerColor;
        ChessGame chessGame = new ChessGame(); // Initialize or fetch the game instance
        this.boardRenderer = new ChessBoardRender(chessGame);
    }

    public void start() {
        boolean inGame = true;
        while (inGame) {
            System.out.println("\nPlease enter a command (help, view board, make move, resign, exit):");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "help":
                    displayHelp();
                    break;
                case "view board":
                    viewBoard();
                    break;
                case "make move":
                    makeMove();
                    break;
                case "exit":
                    inGame = false;
                    break;
                default:
                    System.out.println("Unknown command.");
            }
        }
    }

    private void displayHelp() {
        System.out.println("""
                Commands:
                - help: Display this help message.
                - view board: View the current state of the board.
                - make move: Make a move in the game.
                - resign: Resign from the game.
                - exit: Exit to main menu.
                """);
    }

    private void viewBoard() {
        boolean whiteAtBottom = playerColor == ChessGame.TeamColor.WHITE;
        System.out.println("\nCurrent board state:");
        boardRenderer.renderBoard(whiteAtBottom, null);
    }

    private void makeMove() {
        System.out.print("Enter your move (e.g., e2 e4): ");
        String move = scanner.nextLine().trim();
        // placeholder for sending move to server
        System.out.println("Move submitted: " + move);
    }
}
