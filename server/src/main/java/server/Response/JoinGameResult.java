package server.Response;

import spark.Response;

public class JoinGameResult {
    public static Object response(Response resp) {
        resp.status(200);
        return "{}";
    }
}
