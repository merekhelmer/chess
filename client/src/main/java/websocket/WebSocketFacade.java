package websocket;

import client.ResponseException;
import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;


import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade {
    private Session session;
    private final MessageHandler notificationHandler;

    public WebSocketFacade(String serverUrl, MessageHandler notificationHandler) throws ResponseException {
        try {
            URI uri = new URI(serverUrl.replace("http", "ws") + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig config) {
                    session.addMessageHandler(String.class, WebSocketFacade.this::onMessage);
                }
            }, uri);
        } catch (IOException | URISyntaxException | DeploymentException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private void onMessage(String message) {
        ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
        notificationHandler.notify();
    }

    public void sendConnectCommand(String authToken, int gameID) throws ResponseException {
        sendCommand(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID));
    }

    public void sendCommand(UserGameCommand command) throws ResponseException {
        try {
            session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public boolean isConnected() {
        return session != null && session.isOpen();
    }

    public void disconnect() throws ResponseException {
        try {
            if (session != null) {
                session.close();
            }
        } catch (IOException e) {
            throw new ResponseException(500, "Failed to close WebSocket connection.");
        }
    }
}
