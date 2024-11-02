package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SQLAuthTest {

    private SQLAuthDAO authDAO;
    private SQLUserDAO userDAO;

    @BeforeAll
    static void setupDatabase() throws SQLException, DataAccessException {
        // drop and recreate tables to ensure schema changes
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.createStatement()) {
            stmt.executeUpdate("DROP TABLE IF EXISTS Game");
            stmt.executeUpdate("DROP TABLE IF EXISTS Auth");
            stmt.executeUpdate("DROP TABLE IF EXISTS User");
        }
        DatabaseManager.createTable();
    }

    @BeforeEach
    void setup() throws SQLException {
        authDAO = new SQLAuthDAO();
        userDAO = new SQLUserDAO();
        clearDatabase();
    }

    private void clearDatabase() throws SQLException {
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.createStatement()) {

            // clear tables in the correct order
            stmt.executeUpdate("DELETE FROM Game");
            stmt.executeUpdate("DELETE FROM Auth");
            stmt.executeUpdate("DELETE FROM User");
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(1)
    void createAuthPositive() throws DataAccessException {
        UserData user = new UserData("user1", "password1", "user1@example.com");
        userDAO.createUser(user);

        AuthData authData = new AuthData("authToken1", "user1");
        authDAO.createAuth(authData);

        // verify that the auth token was successful
        AuthData retrievedAuth = authDAO.getAuth("authToken1");
        assertNotNull(retrievedAuth, "Auth token should be created successfully");
        assertEquals("user1", retrievedAuth.username(), "Usernames should match");
    }

    @Test
    @Order(2)
    void createAuthNegativeInvalidUser() {
        // attempt to create Auth token for non-existent user
        AuthData authData = new AuthData("authToken1", "invalidUser");
        assertThrows(DataAccessException.class, () -> authDAO.createAuth(authData), "Should throw exception for non-existent user");
    }

    @Test
    @Order(3)
    void getAuthInvalidToken() throws DataAccessException {
        // try to retrieve an auth token that does not exist
        AuthData authData = authDAO.getAuth("invalidToken");
        assertNull(authData, "No auth data should be found for an invalid token");
    }

    @Test
    @Order(4)
    void deleteAuthPositive() throws DataAccessException {
        UserData user = new UserData("testUser", "password", "test@example.com");
        userDAO.createUser(user);

        authDAO.createAuth(new AuthData("authToken1", "testUser"));
        authDAO.deleteAuth("authToken1");

        assertNull(authDAO.getAuth("authToken1"), "Auth token should be deleted");
    }
}

