package client;

import model.AuthData;
import ui.LoginState;
import ui.PostLoginREPL;
import ui.PreLoginREPL;

import java.util.Scanner;

public class ChessClient {
    private final ServerFacade serverFacade;
    private final Scanner scanner;
    private AuthData authData;
    private LoginState state = LoginState.SIGNEDOUT;

    public ChessClient(String serverUrl, Scanner scanner) {
        this.serverFacade = new ServerFacade(serverUrl);
        this.scanner = scanner;
    }

    public void start() {
        boolean running = true;
        while (running) {
            if (state == LoginState.SIGNEDOUT) {
                // PreLoginREPL for login or registration
                PreLoginREPL preLoginREPL = new PreLoginREPL(serverFacade, scanner);
                authData = preLoginREPL.start();
                if (authData != null) {
                    state = LoginState.SIGNEDIN;
                }
            } else {
                // PostLoginREPL
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