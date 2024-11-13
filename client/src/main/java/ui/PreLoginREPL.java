package client;

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
}