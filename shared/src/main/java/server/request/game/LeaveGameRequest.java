package server.request.game;

public record LeaveGameRequest(String authToken, int gameID, String playerColor) {};
