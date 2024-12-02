package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.GameService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final GameService gameService;
    private final Gson gson = new Gson();

    public WebSocketHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
            handleCommand(session, command);
        } catch (Exception e) {
            sendError(session, "Invalid command: " + e.getMessage());
        }
    }

    private void handleCommand(Session session, UserGameCommand command) throws IOException {
        switch (command.getCommandType()) {
            case CONNECT -> handleConnect(session, command);
            case MAKE_MOVE -> handleMakeMove(session, command);
            case LEAVE -> handleLeave(session, command);
            case RESIGN -> handleResign(session, command);
            default -> sendError(session, "Unknown command type.");
        }
    }

    private void handleConnect(Session session, UserGameCommand command) throws IOException {
        connections.add(session, command.getGameID());

        try {
            ChessGame game = gameService.getGame(command.getGameID());
            sendLoadGame(session, game);

            String notification = command.getAuthToken() + " connected to the game.";
            connections.broadcast(command.getGameID(), new NotificationMessage(notification));
        } catch (Exception e) {
            sendError(session, "Failed to connect: " + e.getMessage());
        }
    }

    private void handleMakeMove(Session session, UserGameCommand command) throws IOException {
        if (!(command instanceof MakeMoveCommand)) {
            sendError(session, "Invalid command: MAKE_MOVE requires a move.");
            return;
        }

        MakeMoveCommand moveCommand = (MakeMoveCommand) command;

        try {
            ChessGame game = gameService.getGame(moveCommand.getGameID());
            ChessMove move = moveCommand.getMove(); // Now resolves correctly

            game.makeMove(move);
            gameService.updateGame(moveCommand.getGameID(), game);

            connections.broadcast(moveCommand.getGameID(), new LoadGameMessage(game));
            connections.broadcast(moveCommand.getGameID(), new NotificationMessage("Move made: " + move));
        } catch (Exception e) {
            sendError(session, "Invalid move: " + e.getMessage());
        }
    }


    private void handleLeave(Session session, UserGameCommand command) throws IOException {
        connections.remove(session);
        connections.broadcast(command.getGameID(), new NotificationMessage(command.getAuthToken() + " left the game."));
    }

    private void handleResign(Session session, UserGameCommand command) throws IOException {
        try {
            gameService.markGameAsResigned(command.getGameID());
            connections.broadcast(command.getGameID(), new NotificationMessage(command.getAuthToken() + " resigned from the game."));
        } catch (Exception e) {
            sendError(session, "Failed to resign: " + e.getMessage());
        }
    }

    private void sendLoadGame(Session session, ChessGame game) throws IOException {
        LoadGameMessage loadGameMessage = new LoadGameMessage(game);
        session.getRemote().sendString(gson.toJson(loadGameMessage));
    }

    private void sendError(Session session, String errorMessage) throws IOException {
        ErrorMessage errorMessageObject = new ErrorMessage(errorMessage);
        session.getRemote().sendString(gson.toJson(errorMessageObject));
    }

    public void closeAllConnections() {
        connections.closeAll();
    }
}

