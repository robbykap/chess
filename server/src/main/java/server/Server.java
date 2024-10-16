package server;

import spark.*;

import service.*;
import dataaccess.*;

public class Server {
    private AuthDAO authDAO = new MemoryAuthDAO();
    private GameDAO gameDAO = new MemoryGameDAO();
    private UserDAO userDAO = new MemoryUserDAO();

    private GameService gameService = new GameService(gameDAO, authDAO);
    private UserService userService = new UserService(userDAO, authDAO);

    private UserHandler userHandler = new UserHandler(userService);
    private GameHandler gameHandler = new GameHandler(gameService);

    public Server() {};

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.delete("/db", this::clear);
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

    public Object clear(Request req, Response resp) {
        authDAO.clearAuths();
        gameDAO.clearGames();
        userDAO.clearUsers();
        return "{}";
    }
}
