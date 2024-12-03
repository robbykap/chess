package server.request.game;

import chess.ChessGame;

public record LeaveGameRequest(String authToken, int gameID, ChessGame.TeamColor playerColor) {};
