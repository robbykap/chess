package dataaccess;
import chess.ChessGame;

import com.google.gson.Gson;
import model.GameData;

import java.sql.*;
import java.util.Collection;
import java.util.Map;

public class SQLGameDAO implements GameDAO {

    public SQLGameDAO() {
        try {
            DatabaseManager.configureDatabase("GameData");
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<Map<String, Object>> getGames() {
        Collection<Map<String, Object>> games = new java.util.ArrayList<>();
        try (var connection = DatabaseManager.getConnection()) {
            try (var statement = connection.prepareStatement("SELECT * FROM GameData")) {
                try (var resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        Map<String, Object> game = new java.util.HashMap<>();
                        game.put("gameID", resultSet.getInt("gameID"));
                        game.put("whiteUsername", resultSet.getString("whiteUsername"));
                        game.put("blackUsername", resultSet.getString("blackUsername"));
                        game.put("gameName", resultSet.getString("gameName"));
                        games.add(game);
                    }
                }
            }
            return games;

        } catch (SQLException | DataAccessException e) {
            return null;
        }

    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            try (var statement = connection.prepareStatement(
                    "INSERT INTO GameData (gameID, whiteUsername, blackUsername, gameName, game) " +
                        "VALUES (?, ?, ?, ?, ?)"))
            {
                statement.setInt(1, game.gameID());
                statement.setString(2, game.whiteUsername());
                statement.setString(3, game.blackUsername());
                statement.setString(4, game.gameName());
                statement.setString(5, new Gson().toJson(game.game()));
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to create game");
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            try (var statement = connection.prepareStatement(
                    "SELECT * FROM GameData WHERE gameID = ?"))
            {
                statement.setInt(1, gameID);
                try (var resultSet = statement.executeQuery()) {
                    resultSet.next();
                    return new GameData(
                            resultSet.getInt("gameID"),
                            resultSet.getString("whiteUsername"),
                            resultSet.getString("blackUsername"),
                            resultSet.getString("gameName"),
                            new Gson().fromJson(resultSet.getString("game"), ChessGame.class)
                    );
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            try (var statement = connection.prepareStatement(
                    "UPDATE GameData SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? " +
                        "WHERE gameID = ?"))
            {
                statement.setString(1, game.whiteUsername());
                statement.setString(2, game.blackUsername());
                statement.setString(3, game.gameName());
                statement.setString(4, new Gson().toJson(game.game()));
                statement.setInt(5, game.gameID());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clear() {
        try (var connection = DatabaseManager.getConnection()) {
            try (var statement = connection.prepareStatement(
                    "DELETE FROM GameData"))
            {
                statement.executeUpdate();
            }
        } catch (SQLException | DataAccessException ignored) {
        }
    }

   public int size() {
        try {
            return DatabaseManager.size("GameData");
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
