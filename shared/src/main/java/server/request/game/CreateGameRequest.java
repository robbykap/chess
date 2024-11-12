package server.request.game;

public record CreateGameRequest(String authToken, String gameName) {}
