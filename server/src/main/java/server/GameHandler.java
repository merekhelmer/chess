package server;

import com.google.gson.Gson;
import service.GameService;
import spark.Request;
import spark.Response;
import model.ErrorResult;

public class GameHandler {
    private final GameService gameService;
    private final Gson gson;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
        this.gson = new Gson();
    }

    // Handler for joining a game (PUT /game)
    public Object joinGame(Request req, Response res) {
        try {
            // Extract the authorization token from headers
            String authToken = req.headers("authorization");

            // Parse the request body
            String playerColor = req.queryParams("playerColor");
            int gameID = Integer.parseInt(req.queryParams("gameID"));

            // Delegate to GameService
            return gson.toJson(gameService.joinGame(gameID, playerColor, authToken));
        } catch (Exception e) {
            res.status(400);
            return gson.toJson(new ErrorResult("Error: " + e.getMessage()));
        }
    }

    // Handler for creating a game (POST /game)
    public Object createGame(Request req, Response res) {
        try {
            // Extract the authorization token from headers
            String authToken = req.headers("authorization");

            // Parse the request body
            String gameName = req.queryParams("gameName");

            // Delegate to GameService
            return gson.toJson(gameService.createGame(gameName, authToken));
        } catch (Exception e) {
            res.status(400);
            return gson.toJson(new ErrorResult("Error: " + e.getMessage()));
        }
    }

    // Handler for listing all games (GET /game)
    public Object listGames(Request req, Response res) {
        try {
            // Extract the authorization token from headers
            String authToken = req.headers("authorization");

            // Delegate to GameService
            return gson.toJson(gameService.listGames(authToken));
        } catch (Exception e) {
            res.status(400);
            return gson.toJson(new ErrorResult("Error: " + e.getMessage()));
        }
    }
}
