package server.handler;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.BadRequestException;
import dataaccess.UnauthorizedException;
import org.eclipse.jetty.websocket.api.Session;

import model.AuthData;
import model.GameData;

import org.eclipse.jetty.websocket.api.annotations.*;
import server.Server;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.MoveCommand;
import websocket.commands.ResignCommand;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;
import websocket.messages.Error;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebsocketHandler {

    @OnWebSocketConnect
    public void onWebSocketConnect(Session session) {
        System.out.println("WebSocket Connected: " + session.getRemoteAddress().getHostName());
    }

    @OnWebSocketMessage
    public void onWebSocketText(Session session, String message) {
        System.out.printf("Received: %s%n", message);
        try {
            if (message.contains("\"commandType\":\"CONNECT\"")) {
                ConnectCommand command = new Gson().fromJson(message, ConnectCommand.class);
                int gameID = command.getGameID();

                if (Server.sessionGameMap.get(gameID) != null) {
                    Server.sessionGameMap.get(gameID).put(command.getAuthToken(), session);
                } else {
                    Server.sessionGameMap.put(gameID, new ConcurrentHashMap<>(Map.of(command.getAuthToken(), session)));
                }

                Server.authDataGameMap.put(command.getAuthToken(), gameID);
                handleConnectCommand(session, command);

            } else if (message.contains("\"commandType\":\"LEAVE\"")) {
                LeaveCommand command = new Gson().fromJson(message, LeaveCommand.class);
                int gameID = command.getGameID();
                String authToken = command.getAuthToken();

                handleLeaveCommand(session, command);

                Server.sessionGameMap.get(gameID).remove(authToken);
                Server.authDataGameMap.remove(authToken);

            } else if (message.contains("\"commandType\":\"MAKE_MOVE\"")) {
                MoveCommand command = new Gson().fromJson(message, MoveCommand.class);
                handleMoveCommand(session, command);

            } else if (message.contains("\"commandType\":\"RESIGN\"")) {
                ResignCommand command = new Gson().fromJson(message, ResignCommand.class);
                handleResignCommand(session, command);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnWebSocketClose
    public void onWebSocketClose(int statusCode, String reason) {
        System.out.printf("WebSocket Closed. Code: %d, Reason: %s%n", statusCode, reason);
    }

    @OnWebSocketError
    public void onWebSocketError(Throwable cause) {
        System.err.println("WebSocket Error: " + cause.getMessage());
        cause.printStackTrace();
    }

    private void handleConnectCommand(Session session, ConnectCommand command) throws IOException {
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

    private void handleLeaveCommand(Session session, LeaveCommand command) throws IOException {
        try {
            AuthData authData = Server.userService.getAuthData(command.getAuthToken());
            GameData gameData = Server.gameService.getGameData(command.getAuthToken(), command.getGameID());
            ChessGame.TeamColor playerColor = getPlayerColor(authData.username(), gameData);

            Notification notification = new Notification("%s has left the game".formatted(authData.username()));
            broadcastMessage(command.getAuthToken(), notification);
            Server.gameService.leaveGame(command.getAuthToken(), playerColor, command.getGameID());

        } catch (UnauthorizedException | BadRequestException e) {
            sendError(session, new Error("Error: Not authorized"));
        }
    }

    public void handleMoveCommand(Session session, MoveCommand command) throws IOException {
        try {
            AuthData authData = Server.userService.getAuthData(command.getAuthToken());
            GameData gameData = Server.gameService.getGameData(command.getAuthToken(), command.getGameID());
            ChessGame.TeamColor playerColor = getPlayerColor(authData.username(), gameData);

            if (playerColor == null) {
                sendError(session, new Error("Error: Observer cannot make moves"));
                return;
            }

            if (gameData.game().isOver()) {
                sendError(session, new Error("Error: Game is over"));
                return;
            }

            if (gameData.game().getTeamTurn() != playerColor) {
                sendError(session, new Error("Error: Not your turn"));
                return;
            }

            ChessGame.TeamColor oppColor = playerColor == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
            gameData.game().makeMove(command.getMove());

            if (gameData.game().isInCheckmate(oppColor)) {
                gameData.game().setOver(true);
                Notification notification = new Notification("Checkmate! %s wins!".formatted(authData.username()));
                broadcastMessage(command.getAuthToken(), notification);

            } else if (gameData.game().isInStalemate(oppColor)) {
                gameData.game().setOver(true);
                Notification notification = new Notification("Stalemate! Game is a draw");
                broadcastMessage(command.getAuthToken(), notification);

            } else if (gameData.game().isInCheck(oppColor)) {
                Notification notification = new Notification("Check! %s is in check".formatted(authData.username()));
                broadcastMessage(command.getAuthToken(), notification);

            } else {
                Notification notification = new Notification("%s has made a move".formatted(authData.username()));
                broadcastMessage(command.getAuthToken(), notification);
            }

            Server.gameService.updateGame(command.getAuthToken(), gameData);

            LoadGame loadGame = new LoadGame(gameData.game());
            broadcastMessage(command.getAuthToken(), loadGame, true);
        } catch (UnauthorizedException e) {
            sendError(session, new Error("Error: Not authorized"));
        } catch (BadRequestException e) {
            sendError(session, new Error("Error: Invalid game"));
        } catch (InvalidMoveException e) {
            sendError(session, new Error("Error: Invalid move"));
        }
    }

    private void handleResignCommand(Session session, ResignCommand command) throws IOException {
        try {
            AuthData authData = Server.userService.getAuthData(command.getAuthToken());
            GameData gameData = Server.gameService.getGameData(command.getAuthToken(), command.getGameID());
            ChessGame.TeamColor playerColor = getPlayerColor(authData.username(), gameData);

            if (playerColor == null) {
                sendError(session, new Error("Error: Observer cannot resign"));
                return;
            }

            if (gameData.game().isOver()) {
                sendError(session, new Error("Error: Game is over"));
                return;
            }

            gameData.game().setOver(true);

            Server.gameService.updateGame(command.getAuthToken(), gameData);

            Notification notification = new Notification("%s has resigned".formatted(authData.username()));
            broadcastMessage(command.getAuthToken(), notification, true);
        } catch (UnauthorizedException e) {
            sendError(session, new Error("Error: Not authorized"));
        } catch (BadRequestException e) {
            sendError(session, new Error("Error: Invalid game"));
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

    private ChessGame.TeamColor getPlayerColor(String username, GameData gameData) {
        if (gameData.whiteUsername() != null && gameData.whiteUsername().equals(username)) {
            return ChessGame.TeamColor.WHITE;
        } else if (gameData.blackUsername() != null && gameData.blackUsername().equals(username)) {
            return ChessGame.TeamColor.BLACK;
        } else {
            return null;
        }
    }
}
