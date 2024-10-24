package server;

import com.google.gson.Gson;
import model.GameData;
import service.*;
import service.requests.*;
import service.results.*;
import spark.Request;
import spark.Response;
import model.ErrorResult;
import dataaccess.DataAccessException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameHandler {
    private final GameService gameService;
    private final Gson gson;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
        this.gson = new Gson();
    }

    // (PUT /game)
    public Object joinGame(Request req, Response resp) {
        String authToken = req.headers("authorization");
        JoinGameRequest joinRequest = gson.fromJson(req.body(), JoinGameRequest.class);

        if (joinRequest.gameID() <= 0 || joinRequest.playerColor() == null) {
            return handleError(resp, 400, "Error: Game ID and player color are required.");
        }

        try {
            gameService.joinGame(joinRequest.gameID(), joinRequest.playerColor(), authToken);
            resp.status(200);
            return "{}";
        } catch (DataAccessException e) {
            if (e.getMessage().contains("Invalid auth token")) {
                return handleError(resp, 401, e.getMessage());
            }
            if (e.getMessage().contains("Requested team color is already taken")) {
                return handleError(resp, 403, e.getMessage());
            }
            if (e.getMessage().contains("Game not found")) {
                return handleError(resp, 400, e.getMessage());
            }
            return handleError(resp, 500, "Error: Internal server error");
        }
    }

    // (POST /game)
    public Object createGame(Request req, Response resp) {
        String authToken = req.headers("authorization");
        CreateGameRequest createGameRequest = gson.fromJson(req.body(), CreateGameRequest.class);

        if (createGameRequest.gameName() == null) {
            return handleError(resp, 400, "Error: Game name is required.");
        }

        try {
            GameData gameData = gameService.createGame(createGameRequest.gameName(), authToken);
            resp.status(200);
            return gson.toJson(new CreateGameResult(gameData.gameID()));
        } catch (DataAccessException e) {
            if (e.getMessage().contains("Invalid auth token")) {
                return handleError(resp, 401, e.getMessage());
            }
            return handleError(resp, 500, "Error: Internal server error");
        }
    }


    //  (GET /game)
    public Object listGames(Request req, Response resp) {
        String authToken = req.headers("authorization");

        if (authToken == null || authToken.isEmpty()) {
            return handleError(resp, 401, "Error: Authorization token required.");
        }

        try {
            List<GameData> games = gameService.listGames(authToken);
            resp.status(200);

            Map<String, Object> result = new HashMap<>();
            result.put("games", games.isEmpty() ? new ArrayList<>() : games);  // ensure it's an array, even if empty

            return gson.toJson(result);  // return a structured JSON object with a "games" field
        } catch (DataAccessException e) {
            if (e.getMessage().contains("Invalid auth token")) {
                return handleError(resp, 401, "Error: Unauthorized - invalid token");
            }
            return handleError(resp, 500, "Error: Internal server error");
        }
    }

    // Helper method
    private Object handleError(Response resp, int statusCode, String message) {
        resp.status(statusCode);
        return gson.toJson(new ErrorResult(message));
    }
}
