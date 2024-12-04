package client;

import websocket.messages.Error;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

public interface NotificationHandler {
    void notify(Notification notification);

    void error(Error error);

    void loadGame(LoadGame loadGame);


}