package client;

import chess.ChessMove;
import com.google.gson.Gson;

import websocket.messages.*;
import websocket.commands.*;
import websocket.messages.Error;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;


    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                     if (message.contains("\"serverMessageType\":\"NOTIFICATION\"")) {
                         Notification notification = new Gson().fromJson(message, Notification.class);
                         notificationHandler.notify(notification);
                     }
                    else if (message.contains("\"serverMessageType\":\"ERROR\"")) {
                         Error error = new Gson().fromJson(message, Error.class);
                         notificationHandler.error(error);
                     }
                    else if (message.contains("\"serverMessageType\":\"LOAD_GAME\"")) {
                         LoadGame loadGame = new Gson().fromJson(message, LoadGame.class);
                         notificationHandler.loadGame(loadGame);
                     }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connectGame(String authToken, int gameID) throws ResponseException {
        try {
            var action = new ConnectCommand(authToken, gameID);
            String message = new Gson().toJson(action);
            this.session.getBasicRemote().sendText(message);
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void leaveGame(String authToken, int gameID) throws ResponseException {
        try {
            var action = new LeaveCommand(authToken, gameID);
            String message = new Gson().toJson(action);
            this.session.getBasicRemote().sendText(message);
            this.session.close();
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void resign(String authToken, int gameID) throws ResponseException {
        try {
            var action = new ResignCommand(authToken, gameID);
            String message = new Gson().toJson(action);
            this.session.getBasicRemote().sendText(message);
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void move(String authToken, int gameID, ChessMove move) throws ResponseException {
        try {
            var action = new MoveCommand(authToken, gameID, move);
            String message = new Gson().toJson(action);
            this.session.getBasicRemote().sendText(message);
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

}