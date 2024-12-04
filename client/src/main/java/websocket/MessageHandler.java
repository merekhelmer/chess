package websocket;

import websocket.messages.ServerMessage;

public interface MessageHandler {
    void notify(ServerMessage message);

    public abstract class Whole<T> implements javax.websocket.MessageHandler {
        public abstract void onMessage(String message);
    }
}
