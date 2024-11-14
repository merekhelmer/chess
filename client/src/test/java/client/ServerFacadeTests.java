package client;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import service.results.CreateGameResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0); //  random port
        facade = new ServerFacade("http://localhost:" + port); // updates facade to use test server port
        System.out.println("Started test HTTP server on port " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDatabase() {
        try {
            facade.clearDatabase();
        } catch (ResponseException e) {
            System.err.println("Database clearing failed: " + e.getMessage());
        }
    }

    @Test
    public void registerPositive() throws ResponseException {
        AuthData authData = facade.register(new UserData("testUser", "password", "test@example.com"));
        assertNotNull(authData);
        assertTrue(authData.authToken().length() > 10, "Auth token should be generated");
        assertEquals("testUser", authData.username());
    }

    @Test
    public void registerNegative() {
        try {
            facade.register(new UserData("testUser", "password", "test@example.com"));
            facade.register(new UserData("testUser", "password", "test@example.com"));
            fail("Expected ResponseException for duplicate username");
        } catch (ResponseException e) {
            assertEquals(403, e.getStatusCode(), "Expect 403 for duplicate username");
        }
    }

    @Test
    public void loginPositive() throws ResponseException {
        facade.register(new UserData("testUser", "password", "test@example.com"));
        AuthData authData = facade.login(new UserData("testUser", "password", null));
        assertNotNull(authData);
        assertEquals("testUser", authData.username());
    }

    @Test
    public void loginNegative() {
        try {
            facade.register(new UserData("testUser", "password", "test@example.com"));
            facade.login(new UserData("testUser", "wrongpassword", null));
            fail("Expected ResponseException for incorrect password");
        } catch (ResponseException e) {
            assertEquals(401, e.getStatusCode(), "Expect 401 for incorrect password");
        }
    }

    @Test
    public void logoutPositive() throws ResponseException {
        AuthData authData = facade.register(new UserData("testUser", "password", "test@example.com"));
        assertDoesNotThrow(() -> facade.logout(authData.authToken()));
    }

    @Test
    public void logoutNegative() {
        try {
            facade.logout("invalidToken");
            fail("Expected ResponseException for invalid auth token.");
        } catch (ResponseException e) {
            assertEquals(401, e.getStatusCode(), "Expect 401 for invalid auth token");
        }
    }

    @Test
    public void createGamePositive() throws ResponseException {
        AuthData authData = facade.register(new UserData("testUser", "password", "test@example.com"));
        CreateGameResult result = facade.createGame("Test Game", authData.authToken());
        assertNotNull(result);
        assertTrue(result.gameID() > 0, "Game ID should be a positive number");
    }

    @Test
    public void createGameNegative() {
        try {
            facade.createGame("Test Game", null);
            fail("Expected ResponseException for missing auth token.");
        } catch (ResponseException e) {
            assertEquals(401, e.getStatusCode(), "Expect 401 for missing auth token");
        }
    }

    @Test
    public void listGamesPositive() throws ResponseException {
        AuthData authData = facade.register(new UserData("testUser", "password", "test@example.com"));
        facade.createGame("Test Game", authData.authToken());
        List<GameData> games = facade.listGames(authData.authToken());
        assertNotNull(games);
        assertFalse(games.isEmpty(), "Games list should contain at least one game");
    }

    @Test
    public void listGamesNegative() {
        try {
            facade.listGames("invalidToken");
            fail("Expected ResponseException for invalid auth token.");
        } catch (ResponseException e) {
            assertEquals(401, e.getStatusCode(), "Expect 401 for invalid auth token");
        }
    }

    @Test
    public void joinGamePositive() throws ResponseException {
        AuthData authData = facade.register(new UserData("testUser", "password", "test@example.com"));
        int gameID = facade.createGame("Test Game", authData.authToken()).gameID();
        assertDoesNotThrow(() -> facade.joinGame(gameID, ChessGame.TeamColor.WHITE, authData.authToken()));
    }

    @Test
    public void joinGameNegative() {
        AuthData authData = null;
        try {
            authData = facade.register(new UserData("testUser", "password", "test@example.com"));
            facade.joinGame(-1, ChessGame.TeamColor.WHITE, authData.authToken());
            fail("Expected ResponseException for non-existent game ID.");
        } catch (ResponseException e) {
            assertEquals(400, e.getStatusCode(), "Expect 400 for non-existent game ID.");
        }
    }
}
