package dataaccess;

import java.sql.*;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;


public class SQLUserDAO implements UserDAO {

    public SQLUserDAO() {
        try {
            DatabaseManager.configureDatabase("UserData");
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            try (var statement = connection.prepareStatement(
                    "SELECT * FROM UserData WHERE username = ?"))
            {
                statement.setString(1, username);
                try (var resultSet = statement.executeQuery()) {
                    resultSet.next();
                    return new UserData(
                            resultSet.getString("username"),
                            resultSet.getString("password"),
                            resultSet.getString("email")
                    );
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("User not found");
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            var statement = connection.prepareStatement(
                    "INSERT INTO UserData (username, password, email) VALUES (?, ?, ?)");
            statement.setString(1, user.username());
            statement.setString(2, BCrypt.hashpw(user.password(), BCrypt.gensalt()));
            statement.setString(3, user.email());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("User already exists");
        }

    }

    @Override
    public void clear() {
        try (var connection = DatabaseManager.getConnection()) {
            var statement = connection.prepareStatement("DELETE FROM UserData");
            statement.executeUpdate();
        } catch (SQLException | DataAccessException ignored) {
        }
    }

    public int size() {
        try (var connection = DatabaseManager.getConnection()) {
            try (var statement = connection.prepareStatement("SELECT COUNT(*) FROM UserData")) {
                try (var resultSet = statement.executeQuery()) {
                    resultSet.next();
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException | DataAccessException e) {
            return 0;
        }
    }
}
