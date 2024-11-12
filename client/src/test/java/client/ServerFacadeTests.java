package client;

import org.junit.jupiter.api.*;
import server.ChessServer;
import server.request.user.LoginRequest;
import server.request.user.RegisterRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ServerFacadeTests {

    private static ChessServer server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new ChessServer("SQL");
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
            assertEquals("Username already taken", e.getMessage());
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
            assertEquals("Not logged in", e.getMessage());
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

    

}
