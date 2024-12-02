package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;


public class ConnectionManager {
    private final ConcurrentHashMap<Integer, ArrayList<Session>> gameConnections = new ConcurrentHashMap<>();
}
