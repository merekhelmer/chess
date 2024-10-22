package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.AuthData;
import model.ErrorResult;
import model.UserData;
import service.UserService;
import spark.Request;
import spark.Response;

public class UserHandler {

    private final UserService userService;
    private final Gson gson;

    public UserHandler(UserService userService) {
        this.userService = userService;
        this.gson = new Gson();
    }

    // Register User (POST /user)
    public Object register(Request req, Response resp) {
        // Deserialize the request body into a UserData object
        UserData userData = gson.fromJson(req.body(), UserData.class);

        // Validate the required fields
        if (userData.username() == null || userData.password() == null) {
            return handleError(resp, 400, "Error: No username and/or password given");
        }

        try {
            // Attempt to register the user via the UserService
            AuthData authData = userService.register(userData);
            resp.status(200);  // OK
            return gson.toJson(authData);

        } catch (DataAccessException e) {
            if (e.getMessage().contains("User already exists")) {
                return handleError(resp, 403, "Error: Username already taken");
            }
            return handleError(resp, 500, "Error: Internal server error");
        }
    }

    // Login User (POST /session)
    public Object login(Request req, Response resp) {
        // Deserialize the request body into a UserData object
        UserData loginData = gson.fromJson(req.body(), UserData.class);

        // Validate the required fields
        if (loginData.username() == null || loginData.password() == null) {
            return handleError(resp, 400, "Error: No username and/or password given");
        }

        try {
            // Attempt to log in the user via the UserService
            AuthData authData = userService.login(loginData);
            resp.status(200);  // OK
            return gson.toJson(authData);

        } catch (DataAccessException e) {
            if (e.getMessage().contains("Invalid credentials")) {
                return handleError(resp, 401, "Error: Invalid credentials");
            }
            return handleError(resp, 500, "Error: Internal server error");
        }
    }

    // Logout User (DELETE /session)
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
            return gson.toJson("{ \"message\": \"Successfully logged out\" }");

        } catch (DataAccessException e) {
            return handleError(resp, 500, "Error: Internal server error");
        }
    }

    // Helper method for handling errors
    private Object handleError(Response resp, int statusCode, String message) {
        resp.status(statusCode);
        return gson.toJson(new ErrorResult(message));
    }
}


