package service;

import chess.ChessBoard;
import chess.ChessGame;
import dataaccess.*;

import model.AuthData;
import model.GameData;

import java.util.Collection;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public Collection<GameData> listGames(String authToken) throws UnathorizedException {
        try {
            authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnathorizedException(e.getMessage());
        }
        return gameDAO.listGames();
    };

    public int createGame(String authToken, String gameName) throws BadRequestException, UnathorizedException, DataAccessException {
        try {
            authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnathorizedException(e.getMessage());
        }

        int gameID;
        do {
            gameID = (int) (Math.random() * Integer.MAX_VALUE);
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

    public boolean joinGame(String authToken, String color, int gameID) throws BadRequestException, UnathorizedException {
        AuthData authData;
        try {
            authData = authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnathorizedException(e.getMessage());
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
                return false;
            }
            try {
                gameDAO.updateGame(new GameData(gameID, username, blackUsername, game.gameName(), game.game()));
            } catch (DataAccessException e) {
                throw new BadRequestException(e.getMessage());
            }
        } else if (color.equals("black")) {
            if (blackUsername != null) {
                return false;
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
        gameDAO.clear();
    };
}
