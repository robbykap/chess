package server.response.game;

import spark.Response;

public class LeaveGameResult {
    public static Object response(Response resp) {
        resp.status(200);
        return "{}";
    }
}
