package server;

import server.handler.*;
import spark.*;

import service.*;
import dataaccess.*;

public class Server {
    private UserHandler userHandler;
    private GameHandler gameHandler;
    private ClearHandler clearHandler;

    private void initializeComponents() {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        UserDAO userDAO = new MemoryUserDAO();

        GameService gameService = new GameService(gameDAO, authDAO);
        UserService userService = new UserService(userDAO, authDAO);
        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);

        userHandler = new UserHandler(userService);
        gameHandler = new GameHandler(gameService);
        clearHandler = new ClearHandler(clearService);
    }

    public Server() {
        initializeComponents();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.delete("/db", (req, resp) -> clearHandler.clear(resp));
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