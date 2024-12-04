import client.ServerFacade;
import model.AuthData;
import ui.LoginState;
import ui.PostLoginREPL;
import ui.PreLoginREPL;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String serverUrl = "http://localhost:8080";
        System.out.println("♔ Welcome to 240 Chess Client");
        System.out.println("Connecting to server: " + serverUrl);

        ServerFacade serverFacade = new ServerFacade(serverUrl);
        Scanner scanner = new Scanner(System.in);
        AuthData authData = null;
        LoginState state = LoginState.SIGNEDOUT;

        boolean running = true;
        while (running) {
            if (state == LoginState.SIGNEDOUT) {
                // pre-login interactions
                PreLoginREPL preLoginREPL = new PreLoginREPL(serverFacade, scanner);
                authData = preLoginREPL.start();
                if (authData != null) {
                    state = LoginState.SIGNEDIN;
                }
            } else {
                // post-login interactions
                PostLoginREPL postLoginREPL = new PostLoginREPL(serverFacade, scanner, authData);
                boolean loggedOut = postLoginREPL.start();
                if (loggedOut) {
                    state = LoginState.SIGNEDOUT;
                    authData = null;
                }
            }

            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("yes")) {
                running = false;
            }
        }

        System.out.println("♕ Chess Client terminated. Goodbye!");
    }
}

