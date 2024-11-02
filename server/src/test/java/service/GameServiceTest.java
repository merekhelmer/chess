package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private GameService gameService;
    private Map<String, AuthData> authDataMap;
    private Map<Integer, GameData> gameDataMap;

    @BeforeEach
    public void setUp() {
        // initialize in-memory data stores
        authDataMap = new HashMap<>();
        gameDataMap = new HashMap<>();

        // initialize DAOs
        authDAO = new AuthDAO() {
            @Override
            public AuthData getAuth(String authToken) {
                return authDataMap.get(authToken);
            }

            @Override
            public void createAuth(AuthData auth) {
            }

            @Override
            public void deleteAuth(String authToken) {
            }

            @Override
            public void clear() {
                authDataMap.clear();
            }
        };

        gameDAO = new GameDAO() {
            @Override
            public GameData getGame(int gameID) {
                return gameDataMap.get(gameID);
            }

            @Override
            public void updateGame(GameData game) {
                gameDataMap.put(game.gameID(), game);
            }

            @Override
            public void createGame(GameData gameData) {
                gameDataMap.put(gameData.gameID(), gameData);
            }

            @Override
            public List<GameData> listGames() {
                return new ArrayList<>(gameDataMap.values());
            }

            @Override
            public void clear() {
                gameDataMap.clear();
            }
        };

        // initialize GameService with the DAOs
        gameService = new GameService(gameDAO, authDAO);
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        // clear after each test
        authDAO.clear();
        gameDAO.clear();
    }

    @Test
    public void testCreateGamePositive() throws DataAccessException {
        String authToken = "validToken";
        AuthData authData = new AuthData("user1", authToken);
        authDataMap.put(authToken, authData);

        GameData gameData = gameService.createGame("Test Game", authToken);

        assertNotNull(gameData);
    }

    @Test
    public void testCreateGameNegative() {
        String invalidAuthToken = "invalidToken";
        assertThrows(DataAccessException.class, () -> gameService.createGame("Test Game", invalidAuthToken));
    }

    @Test
    public void testJoinGamePositive() throws DataAccessException {
        String authToken = "validToken";
        AuthData authData = new AuthData("user1", authToken);
        authDataMap.put(authToken, authData);

        GameData gameData = gameService.createGame("Test Game", authToken);

        GameData updatedGameData = gameService.joinGame(gameData.gameID(), ChessGame.TeamColor.WHITE, authToken);

        assertNotNull(updatedGameData);
    }

    @Test
    public void testJoinGameNegative() {
        String invalidAuthToken = "invalidToken";

        assertThrows(DataAccessException.class, () -> gameService.joinGame(1, ChessGame.TeamColor.WHITE, invalidAuthToken));
    }

    @Test
    public void testListGamesPositive() throws DataAccessException {
        String authToken = "validToken";
        AuthData authData = new AuthData("user1", authToken);
        authDataMap.put(authToken, authData);

        gameService.createGame("Game1", authToken);

        List<GameData> games = gameService.listGames(authToken);

        assertNotNull(games);
    }

    @Test
    public void testListGamesNegative() {
        String invalidAuthToken = "invalidToken";

        assertThrows(DataAccessException.class, () -> gameService.listGames(invalidAuthToken));
    }

    @Test
    public void testClearPositive() throws DataAccessException {
        String authToken = "validToken";
        AuthData authData = new AuthData("user1", authToken);
        authDataMap.put(authToken, authData);

        gameService.createGame("Game1", authToken);
        gameService.clear();

        assertTrue(gameDAO.listGames().isEmpty());
    }
}
