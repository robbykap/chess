package server;

import server.handler.*;
import spark.*;

import service.*;
import dataaccess.*;

import java.util.Objects;

public class ChessServer {
    private UserHandler userHandler;
    private GameHandler gameHandler;
    private ClearHandler clearHandler;

    private void _initializeComponents(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        GameService gameService = new GameService(gameDAO, authDAO);
        UserService userService = new UserService(userDAO, authDAO);
        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);

        userHandler = new UserHandler(userService);
        gameHandler = new GameHandler(gameService);
        clearHandler = new ClearHandler(clearService);
    }

    private void initializeComponents(String service) {
        if (Objects.equals(service, "Memory")) {
            AuthDAO authDAO = new MemoryAuthDAO();
            GameDAO gameDAO = new MemoryGameDAO();
            UserDAO userDAO = new MemoryUserDAO();

            _initializeComponents(authDAO, gameDAO, userDAO);

        } else if (Objects.equals(service, "SQL")) {
            AuthDAO authDAO = new SQLAuthDAO();
            GameDAO gameDAO = new SQLGameDAO();
            UserDAO userDAO = new SQLUserDAO();

            _initializeComponents(authDAO, gameDAO, userDAO);
        }
    }

    public ChessServer(String service) {
        try {
            initializeComponents(service);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
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

    public int port() {
        return Spark.port();
    }
}