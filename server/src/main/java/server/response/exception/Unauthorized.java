package server.response.exception;

import spark.Response;

public class Unauthorized {
    public static Object response(Response resp) {
        resp.status(401);
        return "{ \"message\": \"Error: unauthorized\" }";
    }
}
