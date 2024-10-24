package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.AuthData;
import model.ErrorResult;
import model.UserData;
import service.UserService;
import spark.Request;
import spark.Response;

import java.util.Map;

public class UserHandler {

    private final UserService userService;
    private final Gson gson;

    public UserHandler(UserService userService) {
        this.userService = userService;
        this.gson = new Gson();
    }

    // Register (POST /user)
    public Object register(Request req, Response resp) {
        UserData userData = gson.fromJson(req.body(), UserData.class);

        if (userData.username() == null || userData.password() == null) {
            return handleError(resp, 400, "Error: No username and/or password given");
        }

        try {
            AuthData authData = userService.register(userData);
            resp.status(200);
            return gson.toJson(authData);

        } catch (DataAccessException e) {
            if (e.getMessage().contains("User already exists")) {
                return handleError(resp, 403, "Error: Username already taken");
            }
            return handleError(resp, 500, "Error: Internal server error");
        }
    }

    // Login (POST /session)
    public Object login(Request req, Response resp) {
        UserData loginData = gson.fromJson(req.body(), UserData.class);

        if (loginData.username() == null || loginData.password() == null) {
            return handleError(resp, 400, "Error: No username and/or password given");
        }

        try {
            // Attempt to log in the user
            AuthData authData = userService.login(loginData);
            resp.status(200);  // OK
            return gson.toJson(authData);

        } catch (DataAccessException e) {
            // Handle incorrect username or password
            if (e.getMessage().contains("Invalid username") || e.getMessage().contains("Incorrect password")) {
                return handleError(resp, 401, "Error: Unauthorized - Invalid username or password");
            }
            // For any other errors, return 500 Internal Server Error
            return handleError(resp, 500, "Error: Internal server error");
        }
    }


    // Logout (DELETE /session)
    public Object logout(Request req, Response resp) {
        // Extract the authorization token from the request headers
        String authToken = req.headers("authorization");

        if (authToken == null || authToken.isEmpty()) {
            return handleError(resp, 400, "Error: No authorization token provided");
        }

        try {
            // Invalidate the auth token via the UserService
            userService.logout(authToken);
            resp.status(200);  // OK

            return gson.toJson(Map.of("message", "Successfully logged out"));

        } catch (DataAccessException e) {
            if (e.getMessage().contains("Invalid auth token")) {
                // If the auth token is invalid, return 401 Unauthorized
                return handleError(resp, 401, "Error: Unauthorized - invalid token");
            }
            // For other errors, return 500 Internal Server Error
            return handleError(resp, 500, "Error: Internal server error");
        }
    }


    // Helper method for handling errors
    private Object handleError(Response resp, int statusCode, String message) {
        resp.status(statusCode);
        return gson.toJson(new ErrorResult(message));
    }
}


