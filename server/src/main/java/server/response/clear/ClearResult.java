package server.response.clear;

import spark.Response;

public class ClearResult {
    public static Object response(Response resp) {
        resp.status(200);
        return "{}";
    }
}
