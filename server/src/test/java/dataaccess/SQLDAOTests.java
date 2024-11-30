package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SQLDAOTests {

    SQLAuthDAO authDao = new SQLAuthDAO();
    SQLGameDAO gameDao = new SQLGameDAO();
    SQLUserDAO userDao = new SQLUserDAO();

    @BeforeEach
    public void setup() throws DataAccessException {
        DatabaseManager.createDatabase();
    }

   @AfterEach
    public void tearDown() throws DataAccessException, SQLException {
        userDao.clear();
        gameDao.clear();
        authDao.clear();
        DatabaseManager.getConnection().close();
    }

    @Test
    public void testCreateUserPositive() throws DataAccessException {
        userDao.createUser(new UserData("username", "password", "email"));

        try (var connection = DatabaseManager.getConnection()) {
            try (var statement = connection.prepareStatement(
                    "SELECT * FROM UserData WHERE username = ?")) {
                statement.setString(1, "username");
                try (var resultSet = statement.executeQuery()) {
                    resultSet.next();
                    assert resultSet.getString("username").equals("username");
                    assert BCrypt.checkpw("password", resultSet.getString("password"));
                    assert resultSet.getString("email").equals("email");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("User already exists");
        }
    }

    @Test
    public void testCreateUserNegative() throws DataAccessException {
        userDao.createUser(new UserData("username", "password", "email"));
        try {
            userDao.createUser(new UserData("username", "password", "email"));
        } catch (DataAccessException e) {
            assert e.getMessage().equals("User already exists");
        }
    }

    @Test
    public void testGetUserPositive() throws DataAccessException {
        userDao.createUser(new UserData("username", "password", "email"));
        var user = userDao.getUser("username");
        assert user.username().equals("username");
        assert BCrypt.checkpw("password", user.password());
        assert user.email().equals("email");
    }

    @Test
    public void testGetUserNegative() {
        assertEquals(0, userDao.size());
        try {
            userDao.getUser("username");
        } catch (DataAccessException e) {
            assert e.getMessage().equals("User not found");
        }
    }

    @Test
    public void createGamePositive() throws DataAccessException {
        gameDao.createGame(
                new GameData(1,
                             "white",
                             "black",
                             "gameName",
                             new ChessGame()));

        try (var connection = DatabaseManager.getConnection()) {
            try (var statement = connection.prepareStatement(
                    "SELECT * FROM GameData WHERE gameID = ?")) {
                statement.setInt(1, 1);
                try (var resultSet = statement.executeQuery()) {
                    resultSet.next();
                    assert resultSet.getInt("gameID") == 1;
                    assert resultSet.getString("whiteUsername").equals("white");
                    assert resultSet.getString("blackUsername").equals("black");
                    assert resultSet.getString("gameName").equals("gameName");
                    assert new Gson().fromJson(resultSet.getString("game"), ChessGame.class).equals
                            (new ChessGame());
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Test
    public void createGameNegative() throws DataAccessException, SQLException {
        gameDao.createGame(
                new GameData(1,
                        "white",
                        "black",
                        "gameName",
                        new ChessGame()));
        try {
            gameDao.createGame(
                    new GameData(1,
                            "white",
                            "black",
                            "gameName",
                            new ChessGame()));
        } catch (DataAccessException e) {
            assertEquals("Failed to create game", e.getMessage());
        }
    }

    @Test
    public void testGetGamePositive() throws DataAccessException {
        gameDao.createGame(
                new GameData(1,
                             "white",
                             "black",
                             "gameName",
                             new ChessGame()));

        var game = gameDao.getGame(1);
        assert game.gameID() == 1;
        assert game.whiteUsername().equals("white");
        assert game.blackUsername().equals("black");
        assert game.gameName().equals("gameName");
        assert game.game().equals(new ChessGame());
    }

    @Test
    public void testGetGameNegative() {
        assertEquals(0, gameDao.size());
        try {
            gameDao.getGame(1);
        } catch (DataAccessException e) {
            assertEquals("Illegal operation on empty result set.", e.getMessage());
        }
    }

    @Test
    public void testUpdateGamePositive() throws DataAccessException {
        gameDao.createGame(
                new GameData(1,
                             "white",
                             "black",
                             "gameName",
                             new ChessGame()));
        gameDao.updateGame(
                new GameData(1,
                             "newWhite",
                             "newBlack",
                             "newGameName",
                             new ChessGame()));
        var game = gameDao.getGame(1);
        assert game.gameID() == 1;
        assert game.whiteUsername().equals("newWhite");
        assert game.blackUsername().equals("newBlack");
        assert game.gameName().equals("newGameName");
        assert game.game().equals(new ChessGame());
    }

    @Test
    public void testUpdateGameNegative() throws DataAccessException {
        gameDao.createGame(
                new GameData(1,
                             "white",
                             "black",
                             "gameName",
                             new ChessGame()));
        try {
            gameDao.updateGame(
                    new GameData(2,
                                 "newWhite",
                                 "newBlack",
                                 "newGameName",
                                 new ChessGame()));
        } catch (DataAccessException e) {
            assertEquals("Game not found", e.getMessage());
        }
    }

    @Test
    public void testGetGamesPositive() throws DataAccessException {
        gameDao.createGame(
                new GameData(1,
                             "white",
                             "black",
                             "gameName",
                             new ChessGame()));
        gameDao.createGame(
                new GameData(2,
                             "white",
                             "black",
                             "gameName",
                             new ChessGame()));
        var games = gameDao.getGames();
        assert games.size() == 2;
    }

    @Test
    public void testGetGamesNegative() {
        assertEquals(0, gameDao.size());
        gameDao.getGames();
    }

    @Test
    public void testCreateAuthPositive() throws DataAccessException {
        authDao.createAuth(new AuthData("username", "authToken"));
        try (var connection = DatabaseManager.getConnection()) {
            try (var statement = connection.prepareStatement(
                    "SELECT * FROM AuthData WHERE username = ?")) {
                statement.setString(1, "username");
                try (var resultSet = statement.executeQuery()) {
                    resultSet.next();
                    assert resultSet.getString("username").equals("username");
                    assert resultSet.getString("authToken").equals("authToken");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("User already exists");
        }
    }

    @Test
    public void testCreateAuthNegative() throws DataAccessException {
        authDao.createAuth(new AuthData("username", "authToken"));
        try {
            authDao.createAuth(new AuthData("username", "authToken"));
        } catch (DataAccessException e) {
            assertEquals("Auth data already exists", e.getMessage());
        }
    }

    @Test
    public void testGetAuthPositive() throws DataAccessException {
        authDao.createAuth(new AuthData("username", "authToken"));
        var auth = authDao.getAuth("authToken");
        assert auth.username().equals("username");
        assert auth.authToken().equals("authToken");
    }

    @Test
    public void testGetAuthNegative() {
        assertEquals(0, authDao.size());
        try {
            authDao.getAuth("authToken");
        } catch (DataAccessException e) {
            assertEquals("Illegal operation on empty result set.", e.getMessage());
        }
    }

    @Test
    public void testDeleteAuthPositive() throws DataAccessException {
        authDao.createAuth(new AuthData("username", "authToken"));
        authDao.deleteAuth("authToken");
        assertEquals(0, authDao.size());
    }

    @Test
    public void testDeleteAuthNegative() throws DataAccessException {
        authDao.createAuth(new AuthData("username", "authToken"));
        try {
            authDao.deleteAuth("authToken");
            authDao.deleteAuth("authToken");
        } catch (DataAccessException e) {
            assert e.getMessage().equals("User not found");
        }
    }

    @Test
    public void testGameClear() throws DataAccessException {
        gameDao.createGame(
                new GameData(1,
                             "white",
                             "black",
                             "gameName",
                             new ChessGame()));
        gameDao.clear();
        assertEquals(0, gameDao.size());
    }

    @Test
    public void testAuthClear() throws DataAccessException {
        authDao.createAuth(new AuthData("username", "authToken"));
        authDao.clear();
        assertEquals(0, authDao.size());
    }

    @Test
    public void testUserClear() throws DataAccessException {
        userDao.createUser(new UserData("username", "password", "email"));
        userDao.clear();
        assertEquals(0, userDao.size());
    }

    @Test
    public void testAuthSizePositive() throws DataAccessException {
        authDao.createAuth(new AuthData("username", "authToken"));
        assertEquals(1, authDao.size());
    }

    @Test
    public void testAuthSizeNegative() {
        assertEquals(0, authDao.size());
    }

    @Test
    public void testGameSizePositive() throws DataAccessException {
        gameDao.createGame(
                new GameData(1,
                             "white",
                             "black",
                             "gameName",
                             new ChessGame()));
        assertEquals(1, gameDao.size());
    }

    @Test
    public void testGameSizeNegative() {
        assertEquals(1, gameDao.size());
    }

    @Test
    public void testUserSizePositive() throws DataAccessException {
        userDao.createUser(new UserData("username", "password", "email"));
        assertEquals(1, userDao.size());
    }

    @Test
    public void testUserSizeNegative() {
        assertEquals(0, userDao.size());
    }

}
