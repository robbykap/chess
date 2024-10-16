package service;

import chess.ChessBoard;
import chess.ChessGame;
import dataaccess.*;

import model.AuthData;
import model.GameData;

import java.util.Collection;
import java.util.UUID;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public Collection<GameData> listGames(String authToken) throws UnauthorizedException {
        try {
            authDAO.verifyAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException(e.getMessage());
        }
        return gameDAO.getGames();
    };

    public int createGame(String authToken, String gameName) throws BadRequestException, UnauthorizedException, DataAccessException {
        try {
            authDAO.verifyAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException(e.getMessage());
        }

        int gameID;
        do {
            gameID = UUID.randomUUID().hashCode();
        } while (gameDAO.getGame(gameID) != null);

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

    public boolean joinGame(String authToken, String color, int gameID) throws BadRequestException, UnauthorizedException {
        AuthData authData;
        try {
            authData = authDAO.verifyAuth(authToken);
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

        if (color.equals("white")) {
            if (whiteUsername != null) {
                throw new BadRequestException("White player already joined");
            }
            try {
                gameDAO.updateGame(new GameData(gameID, username, blackUsername, game.gameName(), game.game()));
            } catch (DataAccessException e) {
                throw new BadRequestException(e.getMessage());
            }
        } else if (color.equals("black")) {
            if (blackUsername != null) {
                throw new BadRequestException("Black player already joined");
            }
            try {
                gameDAO.updateGame(new GameData(gameID, whiteUsername, username, game.gameName(), game.game()));
            } catch (DataAccessException e) {
                throw new BadRequestException(e.getMessage());
            }
        } else {
            throw new BadRequestException("Invalid color");
        }
        return true;
    };

    public void clear() throws DataAccessException {
        gameDAO.clearGames();
    };
}
