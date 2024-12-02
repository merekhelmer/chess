package client;

import chess.ChessGame;
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
                case "signIn" -> signIn(params);
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "help" -> help();
                case "signOut" -> signOut();
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

    public String joinGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length >= 2) {
            gameID = Integer.parseInt(params[0]);
            var playerColor = ChessGame.TeamColor.valueOf(params[1].toUpperCase());
            server.joinGame(gameID, playerColor, authData.authToken());
            connectWebSocket();
            ws.sendConnectCommand(authData.authToken(), gameID);
            return "Joined game as " + playerColor;
        }
        throw new ResponseException(400, "Usage: join <game_id> <WHITE|BLACK>");
    }

    public String observeGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length >= 1) {
            gameID = Integer.parseInt(params[0]);
            connectWebSocket();
            ws.sendConnectCommand(authData.authToken(), gameID);
            return "Observing game with ID: " + gameID;
        }
        throw new ResponseException(400, "Usage: observe <game_id>");
    }

    public String signOut() throws ResponseException {
        assertSignedIn();
        server.logout(authData.authToken());
        ws.disconnect();
        state = LoginState.SIGNEDOUT;
        authData = null;
        return "Successfully signed out.";
    }

    private void connectWebSocket() throws ResponseException {
        if (ws == null || !ws.isConnected()) {
            ws = new WebSocketFacade(server.getServerUrl(), messageHandler);
        }
    }

    public String help() {
        if (state == LoginState.SIGNEDOUT) {
            return """
                     Commands:
                     - signin <username> <password>
                     - quit
                    """;
        }
        return """
                Commands:
                - create <game_name>
                - list
                - join <game_id> <WHITE|BLACK>
                - observe <game_id>
                - signout
                - quit
                """;
    }

    private void assertSignedIn() throws ResponseException {
        if (state == LoginState.SIGNEDOUT) {
            throw new ResponseException(400, "You must be signed in.");
        }
    }
}