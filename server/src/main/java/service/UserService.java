package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(UserData user) throws DataAccessException {
        UserData existingUser = userDAO.getUser(user.username());
        if (existingUser != null) {
            throw new DataAccessException("User already exists");
        }

        userDAO.createUser(user);
        AuthData auth = new AuthData(generateAuthToken(), user.username());
        authDAO.createAuth(auth);
        return auth;
    }

    public AuthData login(UserData user) throws DataAccessException {
        UserData existingUser = userDAO.getUser(user.username());

        if (existingUser == null) {
            throw new DataAccessException("Invalid username");
        }
        if (!existingUser.password().equals(user.password())) {
            throw new DataAccessException("Incorrect password");
        }
        AuthData auth = new AuthData(generateAuthToken(), user.username());
        authDAO.createAuth(auth);
        return auth;
    }

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

    private String generateAuthToken() {
        return java.util.UUID.randomUUID().toString();
    }
}
