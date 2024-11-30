package client;

import chess.ChessGame;
import server.request.user.LoginRequest;
import server.request.user.RegisterRequest;

import java.util.Collection;
import java.util.Map;


public class ServerFacade {
    private final HttpCommunicator http;

    public ServerFacade(String serverURL) {
        this.http = new HttpCommunicator(serverURL);
    }

    public String register(RegisterRequest request) throws ResponseException {
        return http.register(request);
    }

    public String login(LoginRequest request) throws ResponseException {
        return http.login(request);
    }

    public Boolean logout() throws ResponseException {
        return http.logout();
    }

    public Collection<Map<String, Object>> listGames() throws ResponseException {
        return http.listGames();
    }

    public Boolean createGame(String gameName) throws ResponseException {
        return http.createGame(gameName);
    }

    public ChessGame joinGame(int gameID, String color) throws ResponseException {
        return http.joinGame(gameID, color);
    }

    public ChessGame observeGame(int gameID) throws ResponseException {
        return http.observeGame(gameID);
    }

    public boolean leaveGame() throws ResponseException {
        return http.leaveGame();
    }

    public boolean leaveObserving() throws ResponseException {
        return http.leaveObserving();
    }


}
