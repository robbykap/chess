package server.response.game;

import spark.Response;

import com.google.gson.Gson;

import java.util.Map;
import java.util.Collection;

public class ListGamesResult {
    public static Object response(Response resp, Collection<Map<String, Object>> games) {
        resp.status(200);
        return "{ \"games\": " + new Gson().toJson(games) + " }";
    }
}
