package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private UserService userService;
    private Map<String, UserData> userDataMap;
    private Map<String, AuthData> authDataMap;

    @BeforeEach
    public void setUp() {
        // initialize in-memory data stores
        userDataMap = new HashMap<>();
        authDataMap = new HashMap<>();

        // DAOs with in-memory data
        userDAO = new UserDAO() {
            @Override
            public UserData getUser(String username) {
                return userDataMap.get(username);
            }

            @Override
            public void createUser(UserData user) {
                userDataMap.put(user.username(), user);
            }

            @Override
            public void clear() {
                userDataMap.clear();
            }
        };

        authDAO = new AuthDAO() {
            @Override
            public AuthData getAuth(String authToken) {
                return authDataMap.get(authToken);
            }

            @Override
            public void createAuth(AuthData authData) {
                authDataMap.put(authData.authToken(), authData);
            }

            @Override
            public void deleteAuth(String authToken) {
                authDataMap.remove(authToken);
            }

            @Override
            public void clear() {
                authDataMap.clear();
            }
        };

        // initialize the UserService
        userService = new UserService(userDAO, authDAO);
    }

    @AfterEach
    public void tearDown() {
        userDAO.clear();
        authDAO.clear();
    }

    @Test
    public void testRegisterPositive() throws DataAccessException {
        UserData newUser = new UserData("user1", "password1", "user1@example.com");
        AuthData authData = userService.register(newUser);

        assertNotNull(authData);
    }

    @Test
    public void testRegisterNegative() {
        UserData existingUser = new UserData("user1", "password1", "user1@example.com");
        userDataMap.put(existingUser.username(), existingUser);

        assertThrows(DataAccessException.class, () -> {
            userService.register(existingUser);
        });
    }

    @Test
    public void testLoginPositive() throws DataAccessException {
        UserData existingUser = new UserData("user1", "password1", "user1@example.com");
        userDataMap.put(existingUser.username(), existingUser);

        AuthData authData = userService.login(existingUser);
        assertNotNull(authData);
    }

    @Test
    public void testLoginNegative() {
        UserData existingUser = new UserData("user1", "password1", "user1@example.com");
        userDataMap.put(existingUser.username(), existingUser);

        UserData loginAttempt = new UserData("user1", "wrongPassword", "user1@example.com");

        assertThrows(DataAccessException.class, () -> {
            userService.login(loginAttempt);
        });
    }

    @Test
    public void testLogoutPositive() throws DataAccessException {
        UserData existingUser = new UserData("user1", "password1", "user1@example.com");
        userDataMap.put(existingUser.username(), existingUser);

        AuthData authData = new AuthData("authToken1", existingUser.username());
        authDataMap.put(authData.authToken(), authData);

        userService.logout(authData.authToken());

        assertFalse(authDataMap.containsKey(authData.authToken()));
    }

    @Test
    public void testLogoutNegative() {
        String invalidAuthToken = "invalidAuthToken";

        assertThrows(DataAccessException.class, () -> {
            userService.logout(invalidAuthToken);
        });
    }

    @Test
    public void testClearPositive() {
        userDataMap.put("user1", new UserData("user1", "password1", "user1@example.com"));
        authDataMap.put("authToken1", new AuthData("authToken1", "user1"));

        userService.clear();

        assertTrue(userDataMap.isEmpty());
    }
}
