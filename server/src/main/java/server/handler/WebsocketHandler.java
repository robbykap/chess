package server.handler;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.BadRequestException;
import dataaccess.UnauthorizedException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;

import model.AuthData;
import model.GameData;

import server.Server;
import websocket.commands.Connect;
import websocket.commands.Leave;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;
import websocket.messages.Error;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
            if (message.contains("\"commandType\":\"CONNECT\"")) {
                Connect command = new Gson().fromJson(message, Connect.class);
                int gameID = command.getGameID();

                if (Server.sessionGameMap.get(gameID) != null) {
                    Server.sessionGameMap.get(gameID).put(command.getAuthToken(), session);
                } else {
                    Server.sessionGameMap.put(gameID, new ConcurrentHashMap<>(Map.of(command.getAuthToken(), session)));
                }

                Server.authDataGameMap.put(command.getAuthToken(), gameID);
                handleConnect(session, command);

            } else if (message.contains("\"commandType\":\"LEAVE\"")) {
                Leave command = new Gson().fromJson(message, Leave.class);
                int gameID = command.getGameID();
                String authToken = command.getAuthToken();

                handleLeave(session, command);

                Server.sessionGameMap.get(gameID).remove(authToken);
                Server.authDataGameMap.remove(authToken);
            } else if (messag)
        } catch (IOException e) {
            e.printStackTrace();
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

    private void handleConnect(Session session, Connect command) throws IOException {
        try {
            AuthData authData = Server.userService.getAuthData(command.getAuthToken());
            GameData gameData = Server.gameService.getGameData(command.getAuthToken(), command.getGameID());

            Notification notification = new Notification("%s has connected to the game" + authData.username());
            broadcastMessage(command.getAuthToken(), notification);

            LoadGame loadGame = new LoadGame(gameData.game());
            sendMessage(session, loadGame);

        } catch (UnauthorizedException e) {
            sendError(session, new Error("Error: Not authorized"));
        } catch (BadRequestException e) {
            sendError(session, new Error("Error: Not a valid request"));
        }
    }

    private void handleLeave(Session session, Leave command) throws IOException {
        try {
            AuthData authData = Server.userService.getAuthData(command.getAuthToken());

            Notification notification = new Notification("%s has left the game".formatted(authData.username()));
            broadcastMessage(command.getAuthToken(), notification);

        } catch (UnauthorizedException e) {
            sendError(session, new Error("Error: Not authorized"));
        }
    }

    public void broadcastMessage(String authToken , ServerMessage message) throws IOException {
        broadcastMessage(authToken, message, false);
    }

    public void broadcastMessage(String authToken , ServerMessage message, boolean toSelf) throws IOException {
        System.out.printf("Broadcasting (toSelf: %s): %s%n", toSelf, new Gson().toJson(message));
        int GameID = Server.authDataGameMap.get(authToken);

        for (Map.Entry<String, Session> entry : Server.sessionGameMap.get(GameID).entrySet()) {
            String user = entry.getKey();
            Session session = entry.getValue();

            if (toSelf || !user.equals(authToken)) {
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
