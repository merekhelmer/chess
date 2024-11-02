package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SQLGameTest {

    private SQLGameDAO gameDAO;
    private SQLUserDAO userDAO; // Added to handle user creation

    @BeforeAll
    static void setupDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        DatabaseManager.createTable();
    }

    @BeforeEach
    void setup() throws SQLException {
        gameDAO = new SQLGameDAO();
        userDAO = new SQLUserDAO();
        clearDatabase(); // clear database before each test
    }

    private void clearDatabase() throws SQLException {
        try (var conn = DatabaseManager.getConnection()) {
            // clear each table in reverse dependency order to avoid foreign key issues
            try (var stmt = conn.createStatement()) {
                stmt.executeUpdate("DELETE FROM Game");
                stmt.executeUpdate("DELETE FROM Auth");
                stmt.executeUpdate("DELETE FROM User");
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(1)
    void createGamePositive() throws DataAccessException {
        userDAO.createUser(new UserData("whitePlayer", "password", "white@example.com"));
        userDAO.createUser(new UserData("blackPlayer", "password", "black@example.com"));

        GameData game = new GameData(1, "whitePlayer", "blackPlayer", "Game1", new ChessGame());
        assertDoesNotThrow(() -> gameDAO.createGame(game));

        GameData retrievedGame = gameDAO.getGame(1);
        assertEquals(1, retrievedGame.gameID());
        assertEquals("Game1", retrievedGame.gameName());
    }

    @Test
    @Order(2)
    void createGameNegativeDuplicate() throws DataAccessException {
        userDAO.createUser(new UserData("whitePlayer", "password", "white@example.com"));
        userDAO.createUser(new UserData("blackPlayer", "password", "black@example.com"));

        GameData game = new GameData(1, "whitePlayer", "blackPlayer", "Game1", new ChessGame());
        gameDAO.createGame(game);

        assertThrows(DataAccessException.class, () -> gameDAO.createGame(game), "Should throw on duplicate gameID");
    }

    @Test
    @Order(3)
    void listGamesPositive() throws DataAccessException {
        userDAO.createUser(new UserData("whitePlayer1", "password", "white1@example.com"));
        userDAO.createUser(new UserData("whitePlayer2", "password", "white2@example.com"));

        gameDAO.createGame(new GameData(1, "whitePlayer1", null, "Game1", new ChessGame()));
        gameDAO.createGame(new GameData(2, "whitePlayer2", null, "Game2", new ChessGame()));

        List<GameData> games = gameDAO.listGames();
        assertEquals(2, games.size(), "Should list two games");
    }

    @Test
    @Order(4)
    void updateGamePositive() throws DataAccessException {
        userDAO.createUser(new UserData("whitePlayer", "password", "white@example.com"));
        userDAO.createUser(new UserData("blackPlayer", "password", "black@example.com"));
        userDAO.createUser(new UserData("whitePlayerUpdated", "password", "whiteUpdated@example.com"));
        userDAO.createUser(new UserData("blackPlayerUpdated", "password", "blackUpdated@example.com"));

        GameData game = new GameData(1, "whitePlayer", "blackPlayer", "Game1", new ChessGame());
        gameDAO.createGame(game);

        GameData updatedGame = new GameData(1, "whitePlayerUpdated", "blackPlayerUpdated", "Game1Updated", new ChessGame());
        gameDAO.updateGame(updatedGame);

        GameData retrievedGame = gameDAO.getGame(1);
        assertEquals("whitePlayerUpdated", retrievedGame.whiteUsername());
        assertEquals("Game1Updated", retrievedGame.gameName());
    }
}

