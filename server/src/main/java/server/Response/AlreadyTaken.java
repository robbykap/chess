package server.Response;

import spark.Response;

public class AlreadyTaken {
    public static Object response(Response resp) {
        resp.status(403);
        return "{ \"message\": \"Error: already taken\" }";
    }
}
