package server.Request;

public record JoinGameRequest(String authToken, String playerColor, int gameID) {};
