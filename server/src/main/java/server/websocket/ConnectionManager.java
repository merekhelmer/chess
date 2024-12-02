package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;


public class ConnectionManager {
    private final ConcurrentHashMap<Integer, ArrayList<Session>> gameConnections = new ConcurrentHashMap<>();

    public void add(Session session, int gameId) {
        gameConnections.computeIfAbsent(gameId, k -> new ArrayList<>()).add(session);
    }

    public void remove(Session session) {
        gameConnections.values().forEach(sessions -> sessions.remove(session));
    }

    public void broadcast(int gameID, ServerMessage message) throws IOException {
        ArrayList<Session> sessions = gameConnections.get(gameID);
        if (sessions != null) {

            String jsonMessage = new Gson().toJson(message);
            ArrayList<Session> toRemove = new ArrayList<>();

            for (Session session : sessions) {
                if (session.isOpen()) {
                    session.getRemote().sendString(jsonMessage);
                } else {
                    toRemove.add(session);
                }
            }
            sessions.removeAll(toRemove);
        }
    }
}
