package websocket;

import chess.ChessMove;
import client.ResponseException;
import com.google.gson.Gson;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

public class WebSocketFacade {

    private Session session;
    private final MessageHandler messageHandler;

    public WebSocketFacade(String serverUrl, MessageHandler messageHandler) throws ResponseException {
        this.messageHandler = messageHandler;
        try {
            String wsUrl = serverUrl.replace("http", "ws") + "/ws";
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig config) {
                    session.addMessageHandler(String.class, WebSocketFacade.this::onMessage);
                }
            }, ClientEndpointConfig.Builder.create().build(), URI.create(wsUrl));
        } catch (DeploymentException | IOException e) {
            throw new ResponseException(500, "Failed to establish WebSocket connection: " + e.getMessage());
        }
    }

    public void onMessage(String rawMessage) {
        try {
            messageHandler.onMessage(rawMessage);
        } catch (Exception e) {
            System.err.println("Failed to process WebSocket message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendConnectCommand(String authToken, int gameID) throws ResponseException {
        sendCommand(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID));
    }

    public void sendLeaveCommand(String authToken, int gameID) throws ResponseException {
        sendCommand(new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID));
    }

    public void sendMakeMoveCommand(String authToken, int gameID, ChessMove move) throws ResponseException {
        MakeMoveCommand moveCommand = new MakeMoveCommand(authToken, gameID, move);
        sendCommand(moveCommand);
    }

    public void sendResignCommand(String authToken, int gameID) throws ResponseException {
        sendCommand(new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID));
    }

    private void sendCommand(Object command) throws ResponseException {
        try {
            String message = new Gson().toJson(command);
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            throw new ResponseException(500, "Failed to send command: " + e.getMessage());
        }
    }

    public void closeConnection() {
        try {
            session.close();
        } catch (IOException e) {
            System.err.println("Failed to close WebSocket connection: " + e.getMessage());
        }
    }
}


