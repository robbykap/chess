package client;

import org.junit.jupiter.api.*;
import server.ChessServer;
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

    @Test
    public void positiveRegister() throws ResponseException {
        var authToken = facade.register(
                new RegisterRequest("player1", "password", "p1@email.com"));
        assertTrue(authToken.length() > 10);
    }

    @Test
    public void negativeRegister() {
        try {
            facade.register(new RegisterRequest("player1", "password", "p1email.com"));
        } catch (ResponseException e) {
            assertEquals("failure: 403", e.getMessage());
        }
    }

}
