package client;

import com.google.gson.Gson;
import model.*;

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

    // User Operations
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
}