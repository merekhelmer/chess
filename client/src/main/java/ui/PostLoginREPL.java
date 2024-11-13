package ui;

import client.*;
import model.AuthData;
import model.GameData;
import service.results.CreateGameResult;
import chess.ChessGame;

import java.util.List;
import java.util.Scanner;

public class PostLoginREPL {

    private final ServerFacade serverFacade;
    private final Scanner scanner;
    private AuthData authData;
    private List<GameData> listedGames;

    public PostLoginREPL(ServerFacade serverFacade, Scanner scanner, AuthData authData) {
        this.serverFacade = serverFacade;
        this.scanner = scanner;
        this.authData = authData;
    }

    public void start() {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\nPlease enter a command (help, logout, create game, list games, play game, observe game):");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "help":
                    displayHelp();
                    break;
                case "logout":
                    loggedIn = !logout();
                    break;
                case "create game":
                    createGame();
                    break;
                case "list games":
                    listGames();
                    break;
                case "play game":
                    playGame();
                    break;
                case "observe game":
                    observeGame();
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
                - logout: Logout from your account.
                - create game: Create a new game.
                - list games: List all available games.
                - play game: Join a game to play.
                - observe game: Observe an existing game.
                """);
    }

    private boolean logout() {
        try {
            serverFacade.logout(authData.authToken());
            System.out.println("Successfully logged out.");
            return true;
        } catch (ResponseException e) {
            System.out.println("Logout failed: " + e.getMessage());
            return false;
        }
    }

    private void createGame() {
        System.out.print("Enter a name for the new game: ");
        String gameName = scanner.nextLine().trim();

        try {
            CreateGameResult result = serverFacade.createGame(gameName, authData.authToken());
            System.out.println("Game created with ID: " + result.gameID());
        } catch (ResponseException e) {
            System.out.println("Failed to create game: " + e.getMessage());
        }
    }

    private void listGames() {
        try {
            listedGames = serverFacade.listGames(authData.authToken());
            if (listedGames.isEmpty()) {
                System.out.println("No games available.");
            } else {
                System.out.println("Available games:");
                for (int i = 0; i < listedGames.size(); i++) {
                    GameData game = listedGames.get(i);
                    String players = String.format("White: %s, Black: %s",
                            game.whiteUsername() != null ? game.whiteUsername() : "Open",
                            game.blackUsername() != null ? game.blackUsername() : "Open");
                    System.out.printf("%d. %s (%s)%n", i + 1, game.gameName(), players);
                }
            }
        } catch (ResponseException e) {
            System.out.println("Failed to list games: " + e.getMessage());
        }
    }

    private void playGame() {
        if (listedGames == null || listedGames.isEmpty()) {
            System.out.println("No games available to join. Please list games first.");
            return;
        }
        System.out.print("Enter which game you want to join: ");
        String gameNumberStr = scanner.nextLine().trim();

        try {
            int gameNumber = Integer.parseInt(gameNumberStr);
            if (gameNumber < 1 || gameNumber > listedGames.size()) {
                System.out.println("Invalid game number.");
                return;
            }
            GameData selectedGame = listedGames.get(gameNumber - 1);

            System.out.print("Enter the color you want to play (WHITE or BLACK): ");
            String colorStr = scanner.nextLine().trim().toUpperCase();

            ChessGame.TeamColor playerColor;
            try {
                playerColor = ChessGame.TeamColor.valueOf(colorStr);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid color. Please enter WHITE or BLACK.");
                return;
            }

            serverFacade.joinGame(selectedGame.gameID(), playerColor, authData.authToken());
            System.out.println("Successfully joined the game.");

            // start the gameplay REPL
            new GamePlayREPL(serverFacade, scanner, authData, selectedGame.gameID(), playerColor).start();

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid game number.");
        } catch (ResponseException e) {
            System.out.println("Failed to join game: " + e.getMessage());
        }
    }

    private void observeGame() {
        if (listedGames == null || listedGames.isEmpty()) {
            System.out.println("No games available to observe. Please list games first.");
            return;
        }
        System.out.print("Enter the number of the game you want to observe: ");
        String gameNumberStr = scanner.nextLine().trim();

        try {
            int gameNumber = Integer.parseInt(gameNumberStr);
            if (gameNumber < 1 || gameNumber > listedGames.size()) {
                System.out.println("Invalid game number.");
                return;
            }
            GameData selectedGame = listedGames.get(gameNumber - 1);

            // placeholder for phase 6
            System.out.println("Observing game");

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid game number.");
        }
    }
}
