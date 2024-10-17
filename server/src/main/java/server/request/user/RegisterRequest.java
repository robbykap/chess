package server.request.user;

public record RegisterRequest(String username, String password, String email) {
}
