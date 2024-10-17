package server.Request;

public record CreateGameRequest(String authToken, String gameName) {}
