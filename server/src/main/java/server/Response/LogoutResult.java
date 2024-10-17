package server.Response;

import spark.Response;

public class LogoutResult {
    public static Object response(Response resp) {
        resp.status(200);
        return "{}";
    }
}
