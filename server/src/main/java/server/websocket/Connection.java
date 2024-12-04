package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class Connection {
    private final Session session;
    private final int gameID;

    public Connection(String sessionId, Session session, int gameID) {
        this.session = session;
        this.gameID = gameID;
    }

    // sends a message to the client
    public void send(ServerMessage message) throws IOException {
        if (session.isOpen()) {
            session.getRemote().sendString(message.toJson());
        }
    }

    public void close() throws IOException {
        if (session.isOpen()) {
            session.close();
        }
    }

    public int getGameID() {
        return gameID;
    }

    public Object getSession() {
        return session;
    }
}
