package service;

import model.AuthData;
import model.UserData;

import dataaccess.*;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    // Registration
    public AuthData register(UserData user) throws DataAccessException {
        UserData existingUser = userDAO.getUser(user.username());
        if (existingUser != null) {
            throw new DataAccessException("User already exists");
        }
        //create user
        userDAO.createUser(user);
        AuthData auth = new AuthData(generateAuthToken(), user.username());
        authDAO.createAuth(auth);
        return auth;
    }

    // Login
    public AuthData login(UserData user) throws DataAccessException {
        UserData existingUser = userDAO.getUser(user.username());
        if (existingUser == null) {
            throw new DataAccessException("User does not exist");
        }
        if (existingUser.password().equals(user.password())) {
            AuthData auth = new AuthData(generateAuthToken(), user.username());
            authDAO.createAuth(auth);
            return auth;
        } else {
            throw new DataAccessException("Incorrect username or password");
        }
    }

    // Logout
    public void logout(String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("Invalid auth token.");
        }
        authDAO.deleteAuth(authToken);
    }


    public void clear() {
        userDAO.clear();
        authDAO.clear();
    }


    // generate random auth token
    private String generateAuthToken() {
        return java.util.UUID.randomUUID().toString();
    }
}
