package client;

import chess.ChessMove;
import com.google.gson.Gson;

import websocket.messages.*;
import websocket.commands.*;

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
                    Notification notification = new Gson().fromJson(message, Notification.class);
                    notificationHandler.notify(notification);
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

}