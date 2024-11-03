package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SQLGameTest extends BaseSQLTest {

    private SQLGameDAO gameDAO;
    private SQLUserDAO userDAO;

    @BeforeEach
    void setup() {
        gameDAO = new SQLGameDAO();
        userDAO = new SQLUserDAO();
    }


    @Test
    void createGamePositive() throws DataAccessException {
        userDAO.createUser(new UserData("whitePlayer", "password", "white@example.com"));
        userDAO.createUser(new UserData("blackPlayer", "password", "black@example.com"));

        GameData game = new GameData(1, "whitePlayer", "blackPlayer", "Game1", new ChessGame());
        gameDAO.createGame(game);

        GameData retrievedGame = gameDAO.getGame(1);
        assertNotNull(retrievedGame, "Game should be created and retrieved successfully");
        assertEquals("Game1", retrievedGame.gameName(), "Game names should match");
    }

    @Test
    void createGameNegative() throws DataAccessException {
        userDAO.createUser(new UserData("player1", "password", "player1@example.com"));
        userDAO.createUser(new UserData("player2", "password", "player2@example.com"));

        GameData game1 = new GameData(1, "player1", "player2", "Game1", new ChessGame());
        gameDAO.createGame(game1);

        GameData gameDuplicate = new GameData(1, "player1", "player2", "GameDuplicate", new ChessGame());
        assertThrows(DataAccessException.class, () -> gameDAO.createGame(gameDuplicate), "Cannot create a game with duplicate gameID");
    }

    @Test
    void getGamePositive() throws DataAccessException {
        userDAO.createUser(new UserData("player1", "password", "player1@example.com"));
        GameData game = new GameData(1, "player1", null, "SoloGame", new ChessGame());
        gameDAO.createGame(game);

        GameData retrievedGame = gameDAO.getGame(1);
        assertNotNull(retrievedGame, "Game should be retrieved successfully");
        assertEquals("SoloGame", retrievedGame.gameName(), "Game names should match");
    }

    @Test
    void getGameNegative() throws DataAccessException {
        GameData retrievedGame = gameDAO.getGame(9999);
        assertNull(retrievedGame, "Retrieving a non-existent game should return null");
    }

    @Test
    void listGamesPositive() throws DataAccessException {
        userDAO.createUser(new UserData("player1", "password", "player1@example.com"));
        userDAO.createUser(new UserData("player2", "password", "player2@example.com"));

        gameDAO.createGame(new GameData(1, "player1", null, "Game1", new ChessGame()));
        gameDAO.createGame(new GameData(2, "player2", null, "Game2", new ChessGame()));

        List<GameData> games = gameDAO.listGames();
        assertEquals(2, games.size(), "Should list two games");
    }

    @Test
    void listGamesNegative() throws DataAccessException {
        List<GameData> games = gameDAO.listGames();
        assertTrue(games.isEmpty(), "Listing games when none exist should return empty list");
    }

    @Test
    void updateGamePositive() throws DataAccessException {
        userDAO.createUser(new UserData("player1", "password", "player1@example.com"));
        userDAO.createUser(new UserData("player2", "password", "player2@example.com"));
        GameData game = new GameData(1, "player1", null, "OriginalGame", new ChessGame());
        gameDAO.createGame(game);

        // update the game
        GameData updatedGame = new GameData(1, "player1", "player2", "UpdatedGame", new ChessGame());
        gameDAO.updateGame(updatedGame);

        // verify the update
        GameData retrievedGame = gameDAO.getGame(1);
        assertEquals("UpdatedGame", retrievedGame.gameName(), "Game name should be updated");
        assertEquals("player2", retrievedGame.blackUsername(), "Black player username should be updated");
    }

    @Test
    void updateGameNegative() throws DataAccessException {
        GameData nonExistentGame = new GameData(9999, "player1", "player2", "NonExistentGame", new ChessGame());
        assertThrows(DataAccessException.class, () -> gameDAO.updateGame(nonExistentGame), "Updating a non-existent game should throw DataAccessException");
    }


    @Test
    void clearPositive() throws DataAccessException {
        userDAO.createUser(new UserData("player1", "password", "player1@example.com"));
        gameDAO.createGame(new GameData(1, "player1", null, "Game1", new ChessGame()));

        gameDAO.clear();

        List<GameData> games = gameDAO.listGames();
        assertTrue(games.isEmpty(), "Game table should be empty after clear");
    }
}


