package websocket;

public interface MessageHandler {
    void onMessage(String rawMessage);
}
