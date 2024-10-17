package server;

import server.handler.*;
import spark.*;

import service.*;
import dataaccess.*;

public class Server {
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();
    private final UserDAO userDAO = new MemoryUserDAO();

    private final GameService gameService = new GameService(gameDAO, authDAO);
    private final UserService userService = new UserService(userDAO, authDAO);
    private final ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);

    private final UserHandler userHandler = new UserHandler(userService);
    private final GameHandler gameHandler = new GameHandler(gameService);
    private final ClearHandler clearHandler = new ClearHandler(clearService);

    public Server() {};

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.delete("/db", clearHandler::clear);
        Spark.post("/user", userHandler::register);
        Spark.post("/session", userHandler::login);
        Spark.delete("/session", userHandler::logout);

        Spark.get("/game", gameHandler::listGames);
        Spark.post("/game", gameHandler::createGame);
        Spark.put("/game", gameHandler::joinGame);

        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
