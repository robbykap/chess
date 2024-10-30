package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ServiceTests {
    // ServiceTests.java
    private UserService userService;
    private GameService gameService;
    private ClearService clearService;

    private SQLUserDAO userDAO;
    private SQLAuthDAO authDAO;
    private SQLGameDAO gameDAO;

    @BeforeEach
    public void setUp() {
        userDAO = new SQLUserDAO();
        authDAO = new SQLAuthDAO();
        gameDAO = new SQLGameDAO();

        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);
        clearService = new ClearService(userDAO, authDAO, gameDAO);

        clearService.clear();
    }

    @Test
    public void testRegisterPositive() throws BadRequestException, AlreadyTakenException {
        // Registers multiple users and checks that they are added to the "Database"
        assertEquals(0, userDAO.size(), "register should start with no users");
        assertEquals(0, authDAO.size(), "register should start with no auths");

        AuthData mikeData = userService.register(new UserData("mike", "password", "bob@example.com"));

        assertEquals(1, userDAO.size(), "register should add a user");
        assertEquals(1, authDAO.size(), "register should add an auth");

        AuthData joeData = userService.register(new UserData("joe", "password", "joe@example.com"));
        AuthData sueData = userService.register(new UserData("sue", "password", "sue@example.com"));
        AuthData janeData = userService.register(new UserData("jane", "password", "jane@example.com"));

        assertEquals(4, userDAO.size(), "register should add multiple users");
        assertEquals(4, authDAO.size(), "register should add multiple auths");

        assertEquals("mike", mikeData.username(), "register should return the correct username");
        assertEquals("joe", joeData.username(), "register should return the correct username");
        assertEquals("sue", sueData.username(), "register should return the correct username");
        assertEquals("jane", janeData.username(), "register should return the correct username");

    }

    @Test
    public void testRegisterNegative() throws BadRequestException, AlreadyTakenException {
        // Registers a user and then tries to register the same user again
        assertEquals(0, userDAO.size(), "register should start with no users");
        assertEquals(0, authDAO.size(), "register should start with no auths");

        userService.register(new UserData("mike", "password", "bob@example.com"));

        assertEquals(1, userDAO.size(), "register should add a user");
        assertEquals(1, authDAO.size(), "register should add an auth");

        try {
            userService.register(new UserData("mike", "password", "bob@example.com"));
        } catch (AlreadyTakenException e) {
            assertEquals("User already exists", e.getMessage(), "register should not allow duplicate users");
        }
    }

    @Test
    public void testLogoutPositive() throws BadRequestException, UnauthorizedException, AlreadyTakenException {
        // Registers a user, logs them out, and checks that the auth is removed
        assertEquals(0, userDAO.size(), "register should start with no users");
        assertEquals(0, authDAO.size(), "register should start with no auths");

        AuthData authData = userService.register(new UserData("mike", "password", "bob@example.com"));

        assertEquals(1, userDAO.size(), "register should add a user");
        assertEquals(1, authDAO.size(), "register should add an auth");

        userService.logout(authData.authToken());

        assertEquals(1, userDAO.size(), "logout should not remove the user");
        assertEquals(0, authDAO.size(), "logout should remove the auth");
    }

    @Test
    public void testLogoutNegative() throws BadRequestException, AlreadyTakenException {
        // Tries to logout with an invalid token
        assertEquals(0, userDAO.size(), "register should start with no users");
        assertEquals(0, authDAO.size(), "register should start with no auths");

        userService.register(new UserData("mike", "password", "bob@example.com"));

        assertEquals(1, userDAO.size(), "register should add a user");
        assertEquals(1, authDAO.size(), "register should add an auth");

        try {
            userService.logout("badToken");
        } catch (UnauthorizedException e) {
            assertEquals("Invalid authToken", e.getMessage(), "logout should not allow invalid tokens");
        }
    }

    @Test
    public void testLoginPositive() throws BadRequestException, UnauthorizedException, AlreadyTakenException {
        // Registers a user, logs them out, and then logs them back in
        assertEquals(0, userDAO.size(), "register should start with no users");
        assertEquals(0, authDAO.size(), "register should start with no auths");

        AuthData authData = userService.register(new UserData("mike", "password", "bob@example.com"));
        userService.logout(authData.authToken());

        assertEquals(1, userDAO.size(), "register should add a user");
        assertEquals(0, authDAO.size(), "register should add an auth");

        AuthData newAuthData = userService.login("mike", "password");

        assertNotEquals(authData.authToken(), newAuthData.authToken(), "login should create a new auth token");

        assertEquals(1, userDAO.size(), "login should not add a user");
        assertEquals(1, authDAO.size(), "login should add an auth");
    }

    @Test
    public void testLoginNegative() throws BadRequestException, UnauthorizedException, AlreadyTakenException {
        // Registers a user, logs them out, and then tries to log them back in with the wrong password
        assertEquals(0, userDAO.size(), "register should start with no users");
        assertEquals(0, authDAO.size(), "register should start with no auths");

        AuthData authData = userService.register(new UserData("mike", "password", "bob@example.com"));
        userService.logout(authData.authToken());

        assertEquals(1, userDAO.size(), "register should add a user");
        assertEquals(0, authDAO.size(), "register should add an auth");

        try {
            userService.login("mike", "password1");
        } catch (UnauthorizedException e) {
            assertEquals("Invalid password", e.getMessage(), "login should not allow invalid passwords");
        }

        assertEquals(1, userDAO.size(), "login should not add a user");
        assertEquals(0, authDAO.size(), "login should not add an auth");
    }

    @Test
    public void testListGamesPositive() throws BadRequestException, UnauthorizedException, AlreadyTakenException, DataAccessException {
        // Registers a user, creates a game, and then lists the games
        AuthData authData = userService.register(new UserData("mike", "password", "bob@example.com"));

        assertEquals(0, gameDAO.size(), "listGames should start with no games");

        int test1 = gameService.createGame(authData.authToken(), "TestGame1");
        int test2 = gameService.createGame(authData.authToken(), "TestGame2");
        int test3 = gameService.createGame(authData.authToken(), "TestGame3");
        int test4 = gameService.createGame(authData.authToken(), "TestGame4");

        List<Integer> gameIDs = List.of(test1, test2, test3, test4);

        Collection<Map<String, Object>> actual = gameService.listGames(authData.authToken());

        Collection<Map<String, Object>> expected = new ArrayList<>();
        for (int gameID : gameIDs) {
            GameData gameData = gameDAO.getGame(gameID);
            Map<String, Object> expectedGame = new HashMap<>();
            expectedGame.put("gameID", gameData.gameID());
            expectedGame.put("whiteUsername", gameData.whiteUsername());
            expectedGame.put("blackUsername", gameData.blackUsername());
            expectedGame.put("gameName", gameData.gameName());

            expected.add(expectedGame);
        }

        assertEquals(new HashSet<>(expected), new HashSet<>(actual), "listGames should return all games");
    }

    @Test
    public void testListGamesNegative() throws BadRequestException, UnauthorizedException, AlreadyTakenException {
        // Registers a user, logs them out, and then tries to list the games with an invalid token
        AuthData authData = userService.register(new UserData("mike", "password", "bob@example.com"));

        assertEquals(0, gameDAO.size(), "listGames should start with no games");

        gameService.createGame(authData.authToken(), "TestGame1");
        gameService.createGame(authData.authToken(), "TestGame2");
        gameService.createGame(authData.authToken(), "TestGame3");
        gameService.createGame(authData.authToken(), "TestGame4");

        assertEquals(4, gameDAO.size(), "listGames should return all games");

        userService.logout(authData.authToken());

        try {
            gameService.listGames("silly rabbit");
        } catch (UnauthorizedException e) {
            assertEquals("Illegal operation on empty result set.", e.getMessage(), "listGames should not allow invalid tokens");
        }
    }

    @Test
    public void testCreateGamePositive() throws BadRequestException, UnauthorizedException, AlreadyTakenException {
        // Registers a user, creates a game, and then checks that the game is added
        AuthData authData = userService.register(new UserData("mike", "password", "bob@example.com"));

        assertEquals(0, gameDAO.size(), "createGame should start with no games");

        gameService.createGame(authData.authToken(), "Test1");
        gameService.createGame(authData.authToken(), "Test2");
        gameService.createGame(authData.authToken(), "Test3");
        gameService.createGame(authData.authToken(), "Test4");

        assertEquals(4, gameDAO.size(), "createGame should add a game");
    }

    @Test
    public void testCreateGameNegative() throws BadRequestException, UnauthorizedException, AlreadyTakenException {
        // Registers a user, logs them out, and then tries to create a game with an invalid token
        AuthData authData = userService.register(new UserData("mike", "password", "bob@example.com"));

        assertEquals(0, gameDAO.size(), "createGame should start with no games");

        userService.logout(authData.authToken());

        try {
            gameService.createGame("badToken", "Test1");
        } catch (UnauthorizedException e) {
            assertEquals("Illegal operation on empty result set.", e.getMessage(), "createGame should not allow invalid tokens");
        }
    }

    @Test
    public void testJoinGamePositive() throws BadRequestException, UnauthorizedException, AlreadyTakenException, DataAccessException {
        // Registers two users, creates a game, and adds them to the game
        AuthData mikeData = userService.register(new UserData("mike", "password", "bob@example.com"));
        AuthData bobData = userService.register(new UserData("bob", "password1", "mike@example.com"));

        assertEquals(0, gameDAO.size(), "createGame should start with no games");

        int gameID = gameService.createGame(mikeData.authToken(), "Test1");

        gameService.joinGame(mikeData.authToken(), "WHITE", gameID);
        gameService.joinGame(bobData.authToken(), "BLACK", gameID);

        assertEquals(1, gameDAO.size(), "createGame should add a game");

        GameData game = gameDAO.getGame(gameID);

        assertEquals("mike", game.whiteUsername(), "joinGame should add a white player");
        assertEquals("bob", game.blackUsername(), "joinGame should add a black player");
    }

    @Test
    public void testJoinGameNegative() throws BadRequestException, UnauthorizedException, AlreadyTakenException, DataAccessException {
        // Registers two users, creates a game, and tries to add them to the same color
        AuthData mikeData = userService.register(new UserData("mike", "password", "bob@example.com"));
        AuthData bobData = userService.register(new UserData("bob", "password1", "mike@example.com"));

        assertEquals(0, gameDAO.size(), "createGame should start with no games");

        int gameID = gameService.createGame(mikeData.authToken(), "Test1");

        gameService.joinGame(mikeData.authToken(), "WHITE", gameID);

        GameData gameData = gameDAO.getGame(gameID);

        assertEquals("mike", gameData.whiteUsername(), "joinGame should add a white player");

        try {
            gameService.joinGame(bobData.authToken(), "WHITE", gameID);
        } catch (AlreadyTakenException e) {
            assertEquals("White player already joined", e.getMessage(), "joinGame should not allow duplicate colors");
        }
    }

    @Test
    public void testClear() throws BadRequestException, UnauthorizedException, AlreadyTakenException {
        // Registers a user, creates a game, and then clears the database
        UserData userData = new UserData("mike", "password", "bob@example.com");

        AuthData authData = userService.register(userData);

        gameService.createGame(authData.authToken(), "game1");

        assertEquals(1, userDAO.size(), "clear should start with users");
        assertEquals(1, authDAO.size(), "clear should start with auths");
        assertEquals(1, gameDAO.size(), "clear should start with games");

        clearService.clear();
        assertEquals(0, userDAO.size(), "clearUsers should clear all users");
        assertEquals(0, authDAO.size(), "clearAuths should clear all auths");
        assertEquals(0, gameDAO.size(), "clearGames should clear all games");
    }

    @Test
    public void testClearNegative() throws BadRequestException, UnauthorizedException, AlreadyTakenException {
        // Registers a user, creates a game, and then tries to clear the database with an invalid token
        UserData userData = new UserData("mike", "password", "bob@example.com");

        AuthData authData = userService.register(userData);

        gameService.createGame(authData.authToken(), "game1");

        assertEquals(1, userDAO.size(), "clear should start with users");

        userService.logout(authData.authToken());

        clearService.clear();
    }
}

