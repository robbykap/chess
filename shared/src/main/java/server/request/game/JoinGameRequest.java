package server.request.game;

public record JoinGameRequest(String authToken, String playerColor, int gameID) {};
