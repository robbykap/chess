package client;

import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

public interface NotificationHandler {
    void notify(Notification notification);

}