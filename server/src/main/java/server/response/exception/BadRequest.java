package server.response.exception;

import spark.Response;

public class BadRequest {
    public static Object response(Response resp) {
        resp.status(400);
        return "{ \"message\": \"Error: bad request\" }";
    }
}
