package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();


    public void add(Session session, int gameID) {
        String sessionId = Integer.toString(session.hashCode());
        connections.put(sessionId, new Connection(sessionId, session, gameID));
    }

    public void remove(Session session) {
        String sessionId = Integer.toString(session.hashCode());
        connections.remove(sessionId);
    }

    public void broadcast(int gameID, ServerMessage message) throws IOException {
        var removeList = new ArrayList<String>();

        for (var entry : connections.entrySet()) {
            Connection connection = entry.getValue();
            if (connection.getGameID() == gameID) {
                try {
                    connection.send(message);
                } catch (IOException e) {
                    removeList.add(entry.getKey());
                }
            }
        }
        // remove closed connections
        for (String sessionId : removeList) {
            connections.remove(sessionId);
        }
    }

    public void broadcastExclude(int gameID, Session excludeSession, ServerMessage message) throws IOException {
        var removeList = new ArrayList<String>();

        for (var entry : connections.entrySet()) {
            Connection connection = entry.getValue();
            if (connection.getGameID() == gameID && !connection.getSession().equals(excludeSession)) {
                try {
                    connection.send(message);
                } catch (IOException e) {
                    removeList.add(entry.getKey());
                }
            }
        }
        // remove closed connections
        for (String sessionId : removeList) {
            connections.remove(sessionId);
        }
    }

    public void closeAll() {
        for (var connection : connections.values()) {
            try {
                connection.close();
            } catch (IOException e) {
                System.err.println("Failed to close connection: " + e.getMessage());
            }
        }
        connections.clear();
    }
}

