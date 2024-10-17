package server.handler;

import server.response.clear.*;

import service.ClearService;

import spark.Request;
import spark.Response;

public class ClearHandler {
    private final ClearService clearService;

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    public Object clear(Request req, Response resp) {
        clearService.clear();
        return ClearResult.response(resp);
    }
}
