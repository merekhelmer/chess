package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.io.ConnectionManager;
import service.GameService;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private final GameService gameService;
    private final Gson gson;
    private final ConnectionManager connectionManager;

    public WebSocketHandler(GameService gameService) {
        this.connectionManager = new ConnectionManager();
        this.gameService = gameService;
        this.gson = new Gson();
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
            handleCommand(session, command);
        } catch (Exception ex) {
            sendError(session, "Invali command: " + ex.getMessage());
        }
    }
}
