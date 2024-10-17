package server.handler;

import service.ClearService;
import spark.Response;
import server.response.clear.ClearResult;

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
