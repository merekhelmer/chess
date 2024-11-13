package ui;

import client.*;
import model.AuthData;
import model.GameData;

import java.util.List;
import java.util.Scanner;

public class PostLoginREPL {

    private final ServerFacade serverFacade;
    private final Scanner scanner;
    private AuthData authData;
    private List<GameData> lastFetchedGames;

    public PostLoginREPL(ServerFacade serverFacade, Scanner scanner, AuthData authData) {
        this.serverFacade = serverFacade;
        this.scanner = scanner;
        this.authData = authData;
    }

    public void start() {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\nPlease enter a command (help, logout, create game, list games, play game):");
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
}