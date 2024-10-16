package server;

import com.google.gson.Gson;
import dataaccess.*;

import model.AuthData;
import model.GameData;

import spark.Request;
import spark.Response;

import service.GameService;

import java.util.Collection;

public class GameHandler {
    private GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Object listGames(Request req, Response resp) {
        String authToken = req.headers("Authorization");
        Collection<GameData> games = gameService.listGames(authToken);
        resp.status(200);
        return new Gson().toJson(games);

    };

    public Object createGame(Request req, Response resp) throws DataAccessException {
        String authToken = req.headers("Authorization");
        String gameName = req.queryParams("name");
        int gameID = gameService.createGame(authToken, gameName);
        resp.status(200);
        return "{\"gameID\": " + gameID + "}";
    };

    public Object joinGame(Request req, Response resp) throws DataAccessException {
        String authToken = req.headers("Authorization");
        String color = req.queryParams("color");
        int gameID = Integer.parseInt(req.queryParams("gameID"));
        boolean success = gameService.joinGame(authToken, color, gameID);
        resp.status(200);
        return "{}";
    };
}
