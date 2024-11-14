import client.ServerFacade;
import model.AuthData;
import ui.LoginState;
import ui.PostLoginREPL;
import ui.PreLoginREPL;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String serverUrl = "http://localhost:8080"; // Adjust as necessary
        ServerFacade serverFacade = new ServerFacade(serverUrl);
        Scanner scanner = new Scanner(System.in);
        AuthData authData = null;
        LoginState state = LoginState.SIGNEDOUT;

        while (true) {
            if (state == LoginState.SIGNEDOUT) {
                // Pre-login REPL
                PreLoginREPL preLoginREPL = new PreLoginREPL(serverFacade, scanner);
                authData = preLoginREPL.start();
                if (authData != null) {
                    state = LoginState.SIGNEDIN;
                }
            } else {
                // Post-login REPL
                PostLoginREPL postLoginREPL = new PostLoginREPL(serverFacade, scanner, authData);
                boolean loggedOut = postLoginREPL.start();
                if (loggedOut) {
                    state = LoginState.SIGNEDOUT;
                    authData = null;
                }
            }
        }
    }
}
