package server;

import dataaccess.*;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Spark;

public class Server {

    private final UserService userService;
    private final GameService gameService;

    private final UserHandler userHandler;
    private final GameHandler gameHandler;

    public Server() {
//        UserDAO userDAO = new MemoryUserDAO();
//        AuthDAO authDAO = new MemoryAuthDAO();
//        GameDAO gameDAO = new MemoryGameDAO();

        SQLUserDAO userDAO = new SQLUserDAO();
        AuthDAO authDAO = new SQLAuthDAO();
        GameDAO gameDAO = new SQLGameDAO();

        this.userService = new UserService(userDAO, authDAO);
        this.gameService = new GameService(gameDAO, authDAO);

        this.userHandler = new UserHandler(userService);
        this.gameHandler = new GameHandler(gameService);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        // serve static files from the /web directory
        Spark.staticFiles.location("/web");

        Spark.delete("/db", this::clearDB);
        Spark.post("/user", userHandler::register);
        Spark.post("/session", userHandler::login);
        Spark.delete("/session", userHandler::logout);

        Spark.get("/game", gameHandler::listGames);
        Spark.post("/game", gameHandler::createGame);
        Spark.put("/game", gameHandler::joinGame);

        // global exception handling
        Spark.exception(Exception.class, this::genericExceptionHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object clearDB(Request req, Response resp) throws DataAccessException {
        gameService.clear();   // clear games first to remove dependencies
        userService.clear();   // clear users and auth data last to satisfy FK constraints

        resp.status(200);
        return "{}";
    }

    private void genericExceptionHandler(Exception ex, Request req, Response resp) {
        resp.status(500);
        resp.body(String.format("{ \"message\": \"Error: %s\" }", ex.getMessage()));
    }
}



