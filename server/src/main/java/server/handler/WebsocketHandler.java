package server.handler;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;

public class WebsocketHandler implements WebSocketListener {

    private Session session;

    @Override
    public void onWebSocketConnect(Session session) {
        this.session = session;
        System.out.println("WebSocket Connected: " + session.getRemoteAddress().getHostName());
    }

    @Override
    public void onWebSocketBinary(byte[] bytes, int i, int i1) {
        System.out.println("Binary message received");
    }

    @Override
    public void onWebSocketText(String message) {
        System.out.printf("Received: %s%n", message);
        try {
            if (session != null && session.isOpen()) {
                session.getRemote().sendString("WebSocket response: " + message);
            }
        } catch (Exception e) {
            System.err.println("Error sending response: " + e.getMessage());
        }
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        System.out.printf("WebSocket Closed. Code: %d, Reason: %s%n", statusCode, reason);
        this.session = null;
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        System.err.println("WebSocket Error: " + cause.getMessage());
        cause.printStackTrace();
    }
}
