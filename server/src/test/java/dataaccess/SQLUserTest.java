package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SQLUserTest extends BaseSQLTest {

    private SQLUserDAO userDAO;

    @BeforeEach
    void setup() {
        userDAO = new SQLUserDAO();
    }

    @Test
    void createUserPositive() throws DataAccessException {
        UserData user = new UserData("user1", "password1", "user1@example.com");
        userDAO.createUser(user);

        UserData retrievedUser = userDAO.getUser("user1");
        assertNotNull(retrievedUser, "User should be created and retrieved successfully");
        assertEquals("user1", retrievedUser.username(), "Username should match");
    }

    @Test
    void createUserNegative() throws DataAccessException {
        UserData user1 = new UserData("user1", "password1", "user1@example.com");
        userDAO.createUser(user1);

        // attempt to create user with the same username
        UserData duplicateUser = new UserData("user1", "password2", "user2@example.com");
        assertThrows(DataAccessException.class, () -> userDAO.createUser(duplicateUser), "Can't create a user with a duplicate username");
    }

    @Test
    void getUserPositive() throws DataAccessException {
        UserData user = new UserData("user2", "password2", "user2@example.com");
        userDAO.createUser(user);

        UserData retrievedUser = userDAO.getUser("user2");
        assertNotNull(retrievedUser, "User should be retrieved successfully");
        assertEquals("user2", retrievedUser.username(), "Username should match");
    }

    @Test
    void getUserNegative() throws DataAccessException {
        UserData retrievedUser = userDAO.getUser("nonExistentUser");
        assertNull(retrievedUser, "Retrieving a non-existent user should return null");
    }

    @Test
    void clearPositive() throws DataAccessException {
        UserData user1 = new UserData("user1", "password1", "user1@example.com");
        UserData user2 = new UserData("user2", "password2", "user2@example.com");
        userDAO.createUser(user1);
        userDAO.createUser(user2);

        userDAO.clear();

        UserData retrievedUser1 = userDAO.getUser("user1");
        UserData retrievedUser2 = userDAO.getUser("user2");
        assertNull(retrievedUser1, "User1 should be deleted after clear");
        assertNull(retrievedUser2, "User2 should be deleted after clear");
    }
}

