package server.handler;

import server.response.clear.*;

import service.ClearService;

import spark.Response;

public class ClearHandler {
    private final ClearService clearService;

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    public Object clear(Response resp) {
        clearService.clear();
        return ClearResult.response(resp);
    }
}
