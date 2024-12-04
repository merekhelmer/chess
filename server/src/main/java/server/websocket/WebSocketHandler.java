package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
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
    public void onMessage(Session session, String rawMessage) throws IOException {
        try {
            UserGameCommand command = gson.fromJson(rawMessage, UserGameCommand.class);
            handleCommand(session, rawMessage, command);
        } catch (Exception e) {
            sendError(session, "Invalid command: " + e.getMessage());
        }
    }

    private void handleCommand(Session session, String rawMessage, UserGameCommand command) throws IOException {
        switch (command.getCommandType()) {
            case CONNECT -> handleConnect(session, command);
            case MAKE_MOVE -> handleMakeMove(session, rawMessage);
            case LEAVE -> handleLeave(session, command);
            case RESIGN -> handleResign(session, command);
            default -> sendError(session, "Unknown command type.");
        }
    }

    private void handleConnect(Session session, UserGameCommand command) throws IOException {
        try {
            ChessGame game = gameService.getGame(command.getGameID());
            String username = gameService.getUsernameFromAuthToken(command.getAuthToken());
            String whitePlayer = gameService.getWhitePlayer(command.getGameID());
            String blackPlayer = gameService.getBlackPlayer(command.getGameID());

            if (game == null || username == null) {
                sendError(session, "Invalid authToken or gameID.");
                return;
            }

            connections.add(session, command.getGameID());
            sendLoadGame(session, game); //LOAD_GAME to Root Client

            String notificationMessage = username + " connected as ";
            if (username.equals(whitePlayer)) {
                notificationMessage += "White.";
            } else if (username.equals(blackPlayer)) {
                notificationMessage += "Black.";
            } else {
                notificationMessage += "an observer.";
            }

            connections.broadcastExclude(command.getGameID(), session, new NotificationMessage(notificationMessage));
        } catch (Exception e) {
            sendError(session, "Failed to connect: " + e.getMessage());
        }
    }

    private void handleMakeMove(Session session, String rawMessage) throws IOException {
        try {
            MakeMoveCommand moveCommand = gson.fromJson(rawMessage, MakeMoveCommand.class);

            ChessGame game = gameService.getGame(moveCommand.getGameID());
            String username = gameService.getUsernameFromAuthToken(moveCommand.getAuthToken());
            String whitePlayer = gameService.getWhitePlayer(moveCommand.getGameID());
            String blackPlayer = gameService.getBlackPlayer(moveCommand.getGameID());

            if (game == null || username == null) {
                sendError(session, "Invalid authToken or gameID.");
                return;
            }
            if (game.isOver()) {
                sendError(session, "The game is over. No moves can be made.");
                return;
            }
            if (!game.isPlayerTurn(username, whitePlayer, blackPlayer)) {
                sendError(session, "It's not your turn.");
                return;
            }

            ChessMove move = moveCommand.getMove();
            game.makeMove(move);
            gameService.updateGame(moveCommand.getGameID(), game);

            connections.broadcast(moveCommand.getGameID(), new LoadGameMessage(game)); //LOAD_GAME to all clients

            String notificationMessage = username + " made the move: " + move;
            connections.broadcastExclude(moveCommand.getGameID(), session, new NotificationMessage(notificationMessage));

            if (game.isInCheckmate(game.getTeamTurn())) {
                connections.broadcast(moveCommand.getGameID(), new NotificationMessage("Checkmate!"));
                gameService.markGameAsResigned(moveCommand.getGameID()); // mark the game as over
                return; // no further messages should be sent
            }
            if (game.isInCheck(game.getTeamTurn())) {
                connections.broadcast(moveCommand.getGameID(), new NotificationMessage("Check!"));
            }
            if (game.isInStalemate(game.getTeamTurn())) {
                connections.broadcast(moveCommand.getGameID(), new NotificationMessage("Stalemate!"));
                gameService.markGameAsResigned(moveCommand.getGameID());
            }
        } catch (InvalidMoveException e) {
            sendError(session, "Invalid move: " + e.getMessage());
        } catch (Exception e) {
            sendError(session, "An error occurred while processing MAKE_MOVE: " + e.getMessage());
        }
    }

    private void handleLeave(Session session, UserGameCommand command) throws IOException {
        try {
            ChessGame game = gameService.getGame(command.getGameID());
            String username = gameService.getUsernameFromAuthToken(command.getAuthToken());

            if (game == null || username == null) {
                sendError(session, "Invalid authToken or gameID.");
                return;
            }

            gameService.removePlayerFromGame(command.getGameID(), username); // clear player slot
            connections.remove(session);

            String notificationMessage = username + " left the game.";
            connections.broadcastExclude(command.getGameID(), session, new NotificationMessage(notificationMessage));
        } catch (Exception e) {
            sendError(session, "Failed to leave the game: " + e.getMessage());
        }
    }

    private void handleResign(Session session, UserGameCommand command) throws IOException {
        try {
            ChessGame game = gameService.getGame(command.getGameID());
            String username = gameService.getUsernameFromAuthToken(command.getAuthToken());

            if (game == null || username == null) {
                sendError(session, "Invalid authToken or gameID.");
                return;
            }

            if (game.isOver()) {
                sendError(session, "The game is already over. Resign not allowed.");
                return;
            }

            // Ensure only players can resign
            String whitePlayer = gameService.getWhitePlayer(command.getGameID());
            String blackPlayer = gameService.getBlackPlayer(command.getGameID());
            if (!username.equals(whitePlayer) && !username.equals(blackPlayer)) {
                sendError(session, "Observers cannot resign.");
                return;
            }

            gameService.markGameAsResigned(command.getGameID()); // Mark the game as over
            String notificationMessage = username + " resigned.";
            connections.broadcast(command.getGameID(), new NotificationMessage(notificationMessage));
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

