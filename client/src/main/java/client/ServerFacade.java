package client;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import results.CreateGameResult;

import java.io.*;
import java.net.*;
import java.util.List;

public class ServerFacade {

    private final String serverUrl;
    private final Gson gson;

    public ServerFacade(String url) {
        this.serverUrl = url;
        this.gson = new Gson();
    }

    // USER
    public AuthData register(UserData userData) throws ResponseException {
        String path = "/user";
        return makeRequest("POST", path, userData, AuthData.class, null);
    }

    public AuthData login(UserData userData) throws ResponseException {
        String path = "/session";
        return makeRequest("POST", path, userData, AuthData.class, null);
    }

    public void logout(String authToken) throws ResponseException {
        String path = "/session";
        makeRequest("DELETE", path, null, null, authToken);
    }

    // GAME
    public List<GameData> listGames(String authToken) throws ResponseException {
        String path = "/game";
        record ListGamesResponse(List<GameData> games) {
        }
        ListGamesResponse response = makeRequest("GET", path, null, ListGamesResponse.class, authToken);
        return response.games();
    }

    public CreateGameResult createGame(String gameName, String authToken) throws ResponseException {
        String path = "/game";
        CreateGameRequest request = new CreateGameRequest(gameName);
        return makeRequest("POST", path, request, CreateGameResult.class, authToken);
    }

    public void joinGame(int gameID, ChessGame.TeamColor playerColor, String authToken) throws ResponseException {
        String path = "/game";
        JoinGameRequest request = new JoinGameRequest(playerColor, gameID);
        makeRequest("PUT", path, request, null, authToken);
    }

    public void clearDatabase() throws ResponseException {
        String path = "/db";
        makeRequest("DELETE", path, null, null, null);
    }

    public ChessGame getGameState(int gameID, String authToken) {
        return new ChessGame();
    }

    public String getServerUrl() {
        return serverUrl;
    }

    // helper Methods
    private <T> T makeRequest(String method, String path, Object requestBody, Class<T> responseClass, String authToken)
            throws ResponseException {
        try {
            URL url = new URL(serverUrl + path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);

            if (authToken != null) {
                connection.setRequestProperty("Authorization", authToken);
            }

            if (requestBody != null) {
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                writeRequestBody(connection, requestBody);
            }

            connection.connect();
            int responseCode = connection.getResponseCode();
            if (!isSuccessful(responseCode)) {
                String errorMessage = readErrorResponse(connection);
                throw new ResponseException(responseCode, errorMessage);
            }

            return readResponseBody(connection, responseClass);

        } catch (IOException e) {
            throw new ResponseException(500, "Network error: " + e.getMessage());
        }
    }

    private void writeRequestBody(HttpURLConnection connection, Object requestBody) throws IOException {
        try (OutputStream os = connection.getOutputStream()) {
            String json = gson.toJson(requestBody);
            os.write(json.getBytes());
        }
    }

    private <T> T readResponseBody(HttpURLConnection connection, Class<T> responseClass) throws IOException {
        if (responseClass == null) {
            return null;
        }
        try (InputStream is = connection.getInputStream()) {
            InputStreamReader reader = new InputStreamReader(is);
            return gson.fromJson(reader, responseClass);
        }
    }

    private String readErrorResponse(HttpURLConnection connection) throws IOException {
        try (InputStream is = connection.getErrorStream()) {
            InputStreamReader reader = new InputStreamReader(is);
            ErrorResult errorResult = gson.fromJson(reader, ErrorResult.class);
            return errorResult.message();
        }
    }

    private boolean isSuccessful(int statusCode) {
        return statusCode >= 200 && statusCode < 300;
    }
}
