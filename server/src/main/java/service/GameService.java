package service;

import chess.ChessGame;

import model.AuthData;
import model.GameData;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import dataaccess.BadRequestException;
import dataaccess.UnauthorizedException;
import dataaccess.AlreadyTakenException;

import java.util.Map;
import java.util.Collection;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    private int getGameID() {
        int gameID;
        do {
            gameID = 1 + (int)(Math.random() * 10000);
            try {
                gameDAO.getGame(gameID);
            } catch (DataAccessException e) {
                break;
            }
        } while (true);
        return gameID;
    }

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public Collection<Map<String, Object>> listGames(String authToken) throws UnauthorizedException {
        // Verify the authToken, throw UnauthorizedException if invalid
        try {
            authDAO.getAuth(authToken);
            return gameDAO.getGames();

        } catch (DataAccessException e) {
            throw new UnauthorizedException(e.getMessage());
        }
    };

    public int createGame(String authToken, String gameName) throws BadRequestException, UnauthorizedException {
        // Verify the authToken, throw UnauthorizedException if invalid
        try {
            authDAO.getAuth(authToken);

            // Check for valid gameName, throw BadRequestException if invalid
            if (gameName == null) {
                throw new BadRequestException("Invalid game name");
            }

            // Create a unique gameID
            int gameID = getGameID();

            // Create a new game, throw BadRequestException if failed
            try {
                GameData gameData = new GameData(gameID, null, null, gameName, new ChessGame());
                gameDAO.createGame(gameData);

            } catch (DataAccessException e) {
                throw new BadRequestException(e.getMessage());
            }

            return gameID;

        } catch (DataAccessException e) {
            throw new UnauthorizedException(e.getMessage());
        }
    }

    public void joinGame(String authToken, String color, int gameID) throws BadRequestException, UnauthorizedException, AlreadyTakenException {
        // Verify the authToken, throw UnauthorizedException if invalid
        try {
            AuthData authData = authDAO.getAuth(authToken);

            String username = authData.username();

            // Check for valid color, throw BadRequestException if invalid
            if (color == null || (!color.equals("WHITE") && !color.equals("BLACK") && !color.equals("OBSERVE"))) {
                throw new BadRequestException("Invalid color");
            }

            // Get the game, throw BadRequestException if failed
            GameData game;
            try {
                game = gameDAO.getGame(gameID);
            } catch (DataAccessException e) {
                throw new BadRequestException(e.getMessage());
            }

            switch (color) {
                case "WHITE" -> {
                    // Check if the requested color is already taken, throw AlreadyTakenException if taken
                    if (game.whiteUsername() != null) {
                        throw new AlreadyTakenException("White player already joined");
                    }
                    gameDAO.updateGame(new GameData(gameID, username, game.blackUsername(), game.gameName(), game.game()));
                }
                case "BLACK" -> {
                    // Check if the requested color is already taken, throw AlreadyTakenException if taken
                    if (game.blackUsername() != null) {
                        throw new AlreadyTakenException("Black player already joined");
                    }
                    gameDAO.updateGame(new GameData(gameID, game.whiteUsername(), username, game.gameName(), game.game()));
                }
                case "OBSERVE" ->
                    gameDAO.updateGame(new GameData(gameID, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game()));
            }

        } catch (DataAccessException e) {
            throw new UnauthorizedException(e.getMessage());
        }

    }

    public void leaveGame(String authToken, ChessGame.TeamColor color, int gameID) throws UnauthorizedException {
        // Verify the authToken, throw UnauthorizedException if invalid
        try {
            AuthData authData = authDAO.getAuth(authToken);

            String username = authData.username();

            // Get the game, throw BadRequestException if failed
            GameData game;
            try {
                game = gameDAO.getGame(gameID);
            } catch (DataAccessException e) {
                throw new UnauthorizedException(e.getMessage());
            }

            switch (color) {
                case ChessGame.TeamColor.WHITE -> {
                    // Check if the requested color is already taken, throw AlreadyTakenException if taken
                    if (game.whiteUsername() != null && game.whiteUsername().equals(username)) {
                        gameDAO.updateGame(new GameData(gameID, null, game.blackUsername(), game.gameName(), game.game()));
                    }
                }
                case ChessGame.TeamColor.BLACK -> {
                    // Check if the requested color is already taken, throw AlreadyTakenException if taken
                    if (game.blackUsername() != null && game.blackUsername().equals(username)) {
                        gameDAO.updateGame(new GameData(gameID, game.whiteUsername(), null, game.gameName(), game.game()));
                    }
                }
                case null -> {
                    // Do nothing
                }
            }

        } catch (DataAccessException e) {
            throw new UnauthorizedException(e.getMessage());
        }

    }

    public GameData getGameData(String authToken, int gameID) throws BadRequestException {
        // Verify the authToken, throw UnauthorizedException if invalid
        try {
            authDAO.getAuth(authToken);
            return gameDAO.getGame(gameID);

        } catch (DataAccessException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public void updateGame(String authToken, GameData gameData) throws UnauthorizedException {
        // Verify the authToken, throw UnauthorizedException if invalid
        try {
            authDAO.getAuth(authToken);
            gameDAO.updateGame(gameData);

        } catch (DataAccessException e) {
            throw new UnauthorizedException(e.getMessage());
        }
    }
}
