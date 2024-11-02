package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;


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
        // verify password with BCrypt
        if (!BCrypt.checkpw(user.password(), existingUser.password())) {
            throw new DataAccessException("Incorrect password");
        }
        // generate new auth token
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

    public void clear() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
    }

    private String generateAuthToken() {
        return java.util.UUID.randomUUID().toString();
    }
}
