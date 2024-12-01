package client;

import model.GameData;
import websocket.MessageHandler;
import websocket.WebSocketFacade;
import model.AuthData;
import ui.LoginState;

import java.util.Arrays;
import java.util.List;

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

    public String signIn(String... params) throws ResponseException {
        if (params.length >= 2) {
            var username = params[0];
            var password = params[1];
            authData = server.login(new model.UserData(username, password, null));
            state = LoginState.SIGNEDIN;
            return "Successfully signed in as " + authData.username();
        }
        throw new ResponseException(400, "Usage: signin <username> <password>");
    }

    public String createGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length >= 1) {
            var gameName = params[0];
            var result = server.createGame(gameName, authData.authToken());
            return "Game created with ID: " + result.gameID();
        }
        throw new ResponseException(400, "Usage: create <game_name>");
    }

    public String listGames() throws ResponseException {
        assertSignedIn();
        List<GameData> games = server.listGames(authData.authToken());
        if (games.isEmpty()) {
            return "No games available.";
        }
        StringBuilder sb = new StringBuilder("Available games:\n");
        for (var game : games) {
            sb.append(String.format("- %s (White: %s, Black: %s)\n",
                    game.gameName(),
                    game.whiteUsername() != null ? game.whiteUsername() : "Open",
                    game.blackUsername() != null ? game.blackUsername() : "Open"));
        }
        return sb.toString();
    }

    private void assertSignedIn() throws ResponseException {
        if (state == LoginState.SIGNEDOUT) {
            throw new ResponseException(400, "You must be signed in.");
        }
    }
}