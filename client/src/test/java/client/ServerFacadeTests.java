package client;

import chess.ChessGame;
import org.junit.jupiter.api.*;
import server.Server;
import server.request.user.LoginRequest;
import server.request.user.RegisterRequest;

import java.util.Collection;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clear() {
        server.clearDB();
    }

    @Test
    public void positiveRegister() throws ResponseException {
        var authToken = facade.register(
                new RegisterRequest("player1", "password", "p1@email.com"));
        assertTrue(authToken.length() > 10);
    }

    @Test
    public void negativeRegister() throws ResponseException {
        facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        try {
            facade.register(new RegisterRequest("player1", "password", "p1email.com"));
        } catch (ResponseException e) {
            assertEquals("Username or email already taken", e.getMessage());
        }
    }

    @Test
    public void positiveLogout() throws ResponseException {
        facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        assertTrue(facade.logout());
    }

    @Test
    public void negativeLogout() {
        try {
            facade.logout();
        } catch (ResponseException e) {
            assertEquals("You are not logged in", e.getMessage());
        }
    }

    @Test
    public void positiveLogin() throws ResponseException {
        facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        var authToken = facade.login(new LoginRequest("player1", "password"));
        assertTrue(authToken.length() > 10);
    }

    @Test
    public void negativeLogin() {
        try {
            facade.login(new LoginRequest("player1", "password"));
        } catch (ResponseException e) {
            assertEquals("Username or password incorrect", e.getMessage());
        }
    }

    @Test
    public void positiveCreateGame() throws ResponseException {
        facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        assertTrue(facade.createGame("game1"));
    }

    @Test
    public void negativeCreateGame() {
        try {
            facade.createGame("game1");
        } catch (ResponseException e) {
            assertEquals("You are not logged in", e.getMessage());
        }
    }

    @Test
    public void positiveListGames() throws ResponseException {
        facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        facade.createGame("game1");
        var games = facade.listGames();
        assertEquals(1, games.size());
    }

    @Test
    public void negativeListGames() {
        try {
            facade.listGames();
        } catch (ResponseException e) {
            assertEquals("You are not logged in", e.getMessage());
        }
    }

    @Test
    public void positiveJoinGame() throws ResponseException {
        facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        facade.createGame("game1");
        Collection<Map<String, Object>> games = facade.listGames();
        for (Map<String, Object> game : games) {
            if (game.get("gameName").equals("game1")) {
                int gameID = Double.valueOf(game.get("gameID").toString()).intValue();
                ChessGame gameData = facade.joinGame(gameID, "WHITE");
                assertTrue(gameData != null);
            }
        }
    }

    @Test
    public void negativeJoinGame() throws ResponseException {
        try {
            facade.joinGame(1, "WHITE");
        } catch (ResponseException e) {
            assertEquals("You are not logged in", e.getMessage());
        }
        facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        try {
            facade.joinGame(1, "WHITE");
        } catch (ResponseException e) {
            assertEquals("Game not found, check id", e.getMessage());
        }
        try {
            facade.createGame("game1");
            Collection<Map<String, Object>> games = facade.listGames();
            for (Map<String, Object> game : games) {
                if (game.get("gameName").equals("game1")) {
                    int gameID = Double.valueOf(game.get("gameID").toString()).intValue();
                    facade.joinGame(gameID, "WHITE");
                    facade.joinGame(gameID, "WHITE");
                }
            }
        } catch (ResponseException e) {
            assertEquals("Color already taken, or game is full", e.getMessage());
        }
    }

    @Test void positiveObserveGame() throws ResponseException {
        facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        facade.createGame("game1");
        Collection<Map<String, Object>> games = facade.listGames();
        for (Map<String, Object> game : games) {
            if (game.get("gameName").equals("game1")) {
                int gameID = Double.valueOf(game.get("gameID").toString()).intValue();
                ChessGame gameData = facade.joinGame(gameID, "WHITE");
                assertTrue(gameData != null);
            }
        }
    }

    @Test void negativeObserveGame() throws ResponseException {
        try {
            facade.observeGame(1);
        } catch (ResponseException e) {
            assertEquals("You are not logged in", e.getMessage());
        }
        facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        try {
            facade.observeGame(1);
        } catch (ResponseException e) {
            assertEquals("Game not found, check id", e.getMessage());
        }
    }

    @Test void register() throws ResponseException {
        facade.register(new RegisterRequest("player1", "password", "p1email.com"));
        facade.logout();
        facade.register(new RegisterRequest("player2", "password", "p2email.com"));
    }

}
