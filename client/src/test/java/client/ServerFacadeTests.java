package client;

import org.junit.jupiter.api.*;
import server.ChessServer;


public class ServerFacadeTests {

    private static ChessServer server;

    @BeforeAll
    public static void init() {
        server = new ChessServer();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

}
