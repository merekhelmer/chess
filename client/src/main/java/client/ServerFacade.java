package client;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;
import service.requests.CreateGameRequest;
import service.requests.JoinGameRequest;
import service.results.CreateGameResult;

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
}