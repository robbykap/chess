package service;

import chess.ChessBoard;
import chess.ChessGame;
import dataaccess.*;

import model.AuthData;
import model.GameData;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public Collection<Map<String, Object>> listGames(String authToken) throws UnauthorizedException, DataAccessException {
        try {
            authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException(e.getMessage());
        }
        return gameDAO.getGames();
    };

    public int createGame(String authToken, String gameName) throws BadRequestException, UnauthorizedException, DataAccessException {
        try {
            authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException(e.getMessage());
        }

        if (gameName == null) {
            throw new BadRequestException("Invalid game name");
        }

        int gameID;
        do {
            gameID = Math.abs(UUID.randomUUID().hashCode());
            try {
                gameDAO.getGame(gameID);
            } catch (DataAccessException e) {
                break;
            }
        } while (true);

        try {
            ChessGame game = new ChessGame();
            ChessBoard board = new ChessBoard();
            board.resetBoard();
            game.setBoard(board);
            gameDAO.createGame(new GameData(gameID, null, null, gameName, game));
        } catch (DataAccessException e) {
            throw new BadRequestException(e.getMessage());
        }

        return gameID;
    };

    public void joinGame(String authToken, String color, int gameID) throws BadRequestException, UnauthorizedException, AlreadyTakenException {
        AuthData authData;
        try {
            authData = authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException(e.getMessage());
        }
        String username = authData.username();

        GameData game;
        try {
            game = gameDAO.getGame(gameID);
        } catch (DataAccessException e) {
            throw new BadRequestException(e.getMessage());
        }

        String whiteUsername = game.whiteUsername();
        String blackUsername = game.blackUsername();

        if (color.equals("WHITE")) {
            if (whiteUsername != null) {
                throw new AlreadyTakenException("White player already joined");
            }
            try {
                gameDAO.updateGame(new GameData(gameID, username, blackUsername, game.gameName(), game.game()));
            } catch (DataAccessException e) {
                throw new AlreadyTakenException(e.getMessage());
            }
        } else if (color.equals("BLACK")) {
            if (blackUsername != null) {
                throw new AlreadyTakenException("Black player already joined");
            }
            try {
                gameDAO.updateGame(new GameData(gameID, whiteUsername, username, game.gameName(), game.game()));
            } catch (DataAccessException e) {
                throw new BadRequestException(e.getMessage());
            }
        } else {
            throw new BadRequestException("Invalid color");
        }
    };
}
