package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SQLAuthTest extends BaseSQLTest {

    private SQLAuthDAO authDAO;
    private SQLUserDAO userDAO;

    @BeforeEach
    void setup() {
        authDAO = new SQLAuthDAO();
        userDAO = new SQLUserDAO();
    }

    @Test
    void createAuthPositive() throws DataAccessException {
        UserData user = new UserData("user1", "password1", "user1@example.com");
        userDAO.createUser(user);

        AuthData authData = new AuthData("authToken1", "user1");
        authDAO.createAuth(authData);

        AuthData retrievedAuth = authDAO.getAuth("authToken1");
        assertEquals("user1", retrievedAuth.username(), "Usernames should match");
    }

    @Test
    void createAuthNegative() {
        AuthData authData = new AuthData("authToken1", "invalidUser");
        assertThrows(DataAccessException.class, () -> {
            authDAO.createAuth(authData);
        }, "Creating auth for a non-existent user should throw DataAccessException");
    }

    @Test
    void getAuthPositive() throws DataAccessException {
        UserData user = new UserData("user2", "password2", "user2@example.com");
        userDAO.createUser(user);

        AuthData authData = new AuthData("authToken2", "user2");
        authDAO.createAuth(authData);

        AuthData retrievedAuth = authDAO.getAuth("authToken2");
        assertNotNull(retrievedAuth, "Auth data should be retrieved");
        assertEquals("user2", retrievedAuth.username(), "Usernames should match");
    }

    @Test
    void getAuthNegative() throws DataAccessException {
        AuthData retrievedAuth = authDAO.getAuth("invalidToken");
        assertNull(retrievedAuth, "Auth data with an invalid token should return null");
    }

    @Test
    void deleteAuthPositive() throws DataAccessException {
        UserData user = new UserData("user3", "password3", "user3@example.com");
        userDAO.createUser(user);

        AuthData authData = new AuthData("authToken3", "user3");
        authDAO.createAuth(authData);

        authDAO.deleteAuth("authToken3");

        AuthData retrievedAuth = authDAO.getAuth("authToken3");
        assertNull(retrievedAuth, "Auth data should be deleted");
    }


    @Test
    void deleteAuthNegative() throws DataAccessException {
        // since deleteAuth method does not throw an exception in this case,
        // we'll check that no exception is thrown
        assertDoesNotThrow(() -> {
            authDAO.deleteAuth("nonExistentToken");
        }, "Deleting a non-existent auth token should not throw an exception");
    }

    @Test
    void clearPositive() throws DataAccessException {
        UserData user = new UserData("user4", "password4", "user4@example.com");
        userDAO.createUser(user);

        authDAO.createAuth(new AuthData("authToken4a", "user4"));
        authDAO.createAuth(new AuthData("authToken4b", "user4"));

        authDAO.clear();

        AuthData retrievedAuth1 = authDAO.getAuth("authToken4a");
        AuthData retrievedAuth2 = authDAO.getAuth("authToken4b");
        assertNull(retrievedAuth1, "Auth token should be deleted");
        assertNull(retrievedAuth2, "Auth token should be deleted");
    }
}
