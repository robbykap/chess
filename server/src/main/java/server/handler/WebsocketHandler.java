package server.handler;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.UnauthorizedException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;

import model.AuthData;
import model.GameData;

import server.Server;
import websocket.commands.Connect;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebsocketHandler implements WebSocketListener {

    private Session session;

    @Override
    public void onWebSocketConnect(Session session) {
        this.session = session;
        Server.sessionGameMap.put(session, 0);
        System.out.println("WebSocket Connected: " + session.getRemoteAddress().getHostName());
    }

    @Override
    public void onWebSocketBinary(byte[] bytes, int i, int i1) {
        System.out.println("Binary message received");
    }

    @Override
    public void onWebSocketText(String message) {
        System.out.printf("Received: %s%n", message);
        if (message.contains("\"commandType\":\"CONNECT\"")) {
            Connect command = new Gson().fromJson(message, Connect.class);
            Server.sessionGameMap.replace(session, command.getGameID());
            handleConnect(session, command);
        }
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        System.out.printf("WebSocket Closed. Code: %d, Reason: %s%n", statusCode, reason);
        Server.sessionGameMap.remove(session);
        this.session = null;
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        System.err.println("WebSocket Error: " + cause.getMessage());
        cause.printStackTrace();
    }

    private void handleConnect(Session session, Connect command) {
        try {
            AuthData authData = Server.userService.getAuthData(command.getAuthToken());
            GameData gameData = Server.gameService.getGameData(command.getAuthToken(), command.getGameID());

            Notification notification = new Notification("%s has connected to the game" + authData.username());
            broadcastMessage(session, notification);

            LoadGame loadGame = new LoadGame(gameData.game());
            sendMessage(session, loadGame);

        } catch (UnauthorizedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void broadcastMessage(Session currSession, ServerMessage message) throws IOException {
        broadcastMessage(currSession, message, false);
    }

    // Send the notification to all clients on the current game
    public void broadcastMessage(Session currSession, ServerMessage message, boolean toSelf) throws IOException {
        System.out.printf("Broadcasting (toSelf: %s): %s%n", toSelf, new Gson().toJson(message));
        for (Session session : Server.sessionGameMap.keySet()) {
            boolean inAGame = Server.sessionGameMap.get(session) != 0;
            boolean sameGame = Server.sessionGameMap.get(session).equals(Server.sessionGameMap.get(currSession));
            boolean isSelf = session == currSession;
            if ((toSelf || !isSelf) && inAGame && sameGame) {
                sendMessage(session, message);
            }
        }
    }

    public void sendMessage(Session session, ServerMessage message) throws IOException {
        session.getRemote().sendString(new Gson().toJson(message));
    }

    private void sendError(Session session, Error error) throws IOException {
        System.out.printf("Error: %s%n", new Gson().toJson(error));
        session.getRemote().sendString(new Gson().toJson(error));
    }
}
