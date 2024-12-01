package client;

import websocket.MessageHandler;
import websocket.WebSocketFacade;
import model.AuthData;
import ui.LoginState;

import java.util.Arrays;

public class ChessClient {
    private final ServerFacade server;
    private final MessageHandler messageHandler;
    private WebSocketFacade ws;
    private LoginState state = LoginState.SIGNEDOUT;
    private AuthData authData;
    private int gameID;

    public ChessClient(String serverUrl, MessageHandler messageHandler) {
        this.server = new ServerFacade(serverUrl);
        this.messageHandler = messageHandler;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "signin" -> signIn(params);
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "help" -> help();
                case "signout" -> signOut();
                case "quit" -> "quit";
                default -> "Unknown command. Type 'help' for available commands.";
            };
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }
}