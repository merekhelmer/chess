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
        this(new UserService(new MemoryUserDAO(), new MemoryAuthDAO()),
                new GameService(new MemoryGameDAO(), new MemoryAuthDAO()));
    }

    public Server(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;

        this.userHandler = new UserHandler(this.userService);
        this.gameHandler = new GameHandler(this.gameService);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        // Serve static files from the /web directory
        Spark.staticFiles.location("/web");

        Spark.delete("/db", this::clearDB);
        Spark.post("/user", userHandler::register);
        Spark.post("/session", userHandler::login);
        Spark.delete("/session", userHandler::logout);

        Spark.get("/game", gameHandler::listGames);
        Spark.post("/game", gameHandler::createGame);
        Spark.put("/game", gameHandler::joinGame);

        Spark.exception(Exception.class, this::genericExceptionHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object clearDB(Request req, Response resp) {
        userService.clear();
        gameService.clear();

        resp.status(200);
        return "{}";
    }

    private void genericExceptionHandler(Exception ex, Request req, Response resp) {
        resp.status(500);
        resp.body(String.format("{ \"message\": \"Error: %s\" }", ex.getMessage()));
    }
}

