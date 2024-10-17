package server.Response;

import spark.Response;

public class CreateGameResult {
    public static Object response(Response resp, int gameID) {
        resp.status(200);
        return "{ \"gameID\": " + gameID + " }";
    }
}
