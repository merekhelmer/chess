package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SQLUserTest {
    private SQLUserDAO userDAO;

    @BeforeAll
    static void setupDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        DatabaseManager.createTable();
    }

    @BeforeEach
    void setup() throws SQLException {
        userDAO = new SQLUserDAO();
        clearDatabase();
    }

    private void clearDatabase() throws SQLException {
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.createStatement()) {

            // Clear tables in the correct order
            stmt.executeUpdate("DELETE FROM Game");
            stmt.executeUpdate("DELETE FROM Auth");
            stmt.executeUpdate("DELETE FROM User");
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(1)
    public void createUserPositive() throws DataAccessException {
        UserData user = new UserData("user1", "password1", "user1@example.com");
        userDAO.createUser(user);

        UserData retrievedUser = userDAO.getUser("user1");
        assertNotNull(retrievedUser, "User should be created successfully");
        assertEquals("user1", retrievedUser.username(), "Usernames should match");
    }

    @Test
    @Order(2)
    public void createUserNegativeDuplicate() throws DataAccessException {
        UserData user = new UserData("user1", "password1", "user1@example.com");
        userDAO.createUser(user);

        UserData duplicateUser = new UserData("user1", "password2", "user2@example.com");
        assertThrows(DataAccessException.class, () -> userDAO.createUser(duplicateUser), "Should throw exception for duplicate username");
    }

    @Test
    @Order(3)
    public void getUserInvalid() throws DataAccessException {
        UserData user = userDAO.getUser("nonexistentUser");
        assertNull(user, "No user should be found for a non-existent username");
    }

    @Test
    @Order(4)
    public void clearUserTable() throws DataAccessException, SQLException {
        UserData user = new UserData("user1", "password1", "user1@example.com");
        userDAO.createUser(user);

        clearDatabase();

        UserData retrievedUser = userDAO.getUser("user1");
        assertNull(retrievedUser, "User table should be empty");
    }
}

