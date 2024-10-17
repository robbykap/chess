package server;

import com.google.gson.Gson;
import dataaccess.*;

import model.GameData;

import server.Request.*;
import spark.Request;
import spark.Response;

import service.GameService;

import java.util.Collection;
import java.util.Map;

public class GameHandler {
    private GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Object listGames(Request req, Response resp) throws UnauthorizedException, DataAccessException {
        ListGameRequest listGameRequest = new ListGameRequest(req.headers("Authorization"));

        try{
            Collection<Map<String, Object>> games = gameService.listGames(listGameRequest.authToken());
            resp.status(200);
            return "{ \"games\": " + new Gson().toJson(games) + " }";
        } catch (UnauthorizedException e) {
            resp.status(401);
            return "{ \"message\": \"Error: unauthorized\" }";
        }
    };

    public Object createGame(Request req, Response resp) throws DataAccessException {
        CreateGameRequest createGameRequest = new Gson().fromJson(req.body(), CreateGameRequest.class);
        createGameRequest = new CreateGameRequest(req.headers("Authorization"), createGameRequest.gameName());

        try {
            int gameID = gameService.createGame(createGameRequest.authToken(), createGameRequest.gameName());
            resp.status(200);
            return "{\"gameID\": " + gameID + "}";

        } catch (UnauthorizedException e) {
            resp.status(401);
            return "{ \"message\": \"Error: unauthorized\" }";

        } catch (BadRequestException e) {
            resp.status(400);
            return "{ \"message\": \"Error: bad request\" }";
        }

    };

    public Object joinGame(Request req, Response resp) throws UnauthorizedException, BadRequestException {
        JoinGameRequest joinGameData = new Gson().fromJson(req.body(), JoinGameRequest.class);
        joinGameData = new JoinGameRequest(req.headers("Authorization"), joinGameData.playerColor(), joinGameData.gameID());

        if (joinGameData.playerColor() == null) {
            resp.status(400);
            return "{ \"message\": \"Error: bad request\" }";
        }

        try {
            gameService.joinGame(joinGameData.authToken(), joinGameData.playerColor(), joinGameData.gameID());
            resp.status(200);
            return "{}";

        } catch (AlreadyTakenException e) {
            resp.status(403);
            return "{ \"message\": \"Error: already taken\" }";

        } catch (UnauthorizedException e) {
            resp.status(401);
            return "{ \"message\": \"Error: unauthorized\" }";

        } catch (BadRequestException e) {
            resp.status(400);
            return "{ \"message\": \"Error: bad request\" }";
        }
    };
}
