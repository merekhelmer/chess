package ui;

import client.*;
import model.AuthData;
import model.UserData;

import java.util.Scanner;

public class PreLoginREPL {

    private final ServerFacade serverFacade;
    private final Scanner scanner;

    public PreLoginREPL(ServerFacade serverFacade, Scanner scanner) {
        this.serverFacade = serverFacade;
        this.scanner = scanner;
    }

    public AuthData start() {
        System.out.println("Welcome to the Chess Client!");
        AuthData authData = null;

        while (authData == null) {
            System.out.println("\nPlease enter a command (help, login, register, quit):");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "help":
                    displayHelp();
                    break;
                case "login":
                    authData = login();
                    break;
                case "register":
                    authData = register();
                    break;
                case "quit":
                    System.exit(0);
                default:
                    System.out.println("Unknown command.");
            }
        }
        return authData;
    }

    private void displayHelp() {
        System.out.println("""
                Commands:
                - help: Display this help message.
                - login: Login with your username and password.
                - register: Register a new account.
                - quit: Exit the application.
                """);
    }

    private AuthData login() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        try {
            UserData userData = new UserData(username, password, null);
            AuthData authData = serverFacade.login(userData);
            System.out.println("Successfully logged in as " + authData.username());
            return authData;
        } catch (ResponseException e) {
            System.out.println("Login failed: " + e.getMessage());
            return null;
        }
    }

    private AuthData register() {
        System.out.print("Choose a username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Choose a password: ");
        String password = scanner.nextLine().trim();
        System.out.print("Enter your email: ");
        String email = scanner.nextLine().trim();

        try {
            UserData userData = new UserData(username, password, email);
            AuthData authData = serverFacade.register(userData);
            System.out.println("Successfully registered and logged in as " + authData.username());
            return authData;
        } catch (ResponseException e) {
            System.out.println("Registration failed: " + e.getMessage());
            return null;
        }
    }
}