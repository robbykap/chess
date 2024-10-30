package dataaccess;

import model.AuthData;

import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO{

    public SQLAuthDAO() {
        try {
            configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            try (var statement = connection.prepareStatement("INSERT INTO AuthData (username, authToken) VALUES (?, ?)")) {
                statement.setString(1, authData.username());
                statement.setString(2, authData.authToken());
                statement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            try (var statement = connection.prepareStatement("SELECT * FROM AuthData WHERE authToken = ?")) {
                statement.setString(1, authToken);
                try (var resultSet = statement.executeQuery()) {
                    resultSet.next();
                    return new AuthData(resultSet.getString("username"), resultSet.getString("authToken"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            try (var statement = connection.prepareStatement("DELETE FROM AuthData WHERE authToken = ?")) {
                statement.setString(1, authToken);
                statement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clear() {
        try (var connection = DatabaseManager.getConnection()) {
            try (var statement = connection.prepareStatement("DELETE FROM AuthData")) {
                statement.executeUpdate();
            }
        } catch (SQLException | DataAccessException ignored) {
        }
    }

    private final String[] createStatements = {
        """
        CREATE TABLE IF NOT EXISTS AuthData (
                        username VARCHAR(255) NOT NULL,
                        authToken VARCHAR(255) NOT NULL,
                        PRIMARY KEY (authToken)
        )"""
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var connection = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = connection.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error when configuring database");
        }
    }
}
