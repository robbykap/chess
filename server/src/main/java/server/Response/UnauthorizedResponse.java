package server.Response;

public class UnauthorizedResponse {
    public int status() {
        return 401;
    }

    public Object response() {
        return "{ \"message\": \"Error: unauthorized\" }";
    }
}
