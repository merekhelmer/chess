package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import chess.ChessGame;

import java.util.List;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    // Join Game logic
    public GameData joinGame(int gameID, String playerColor, String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("Invalid auth token.");
        }

        // retrieve the game by gameID
        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            throw new DataAccessException("Game not found.");
        }

        // check if the requested playerColor is available
        if ((playerColor.equals("white") && gameData.whiteUsername() != null) ||
                (playerColor.equals("black") && gameData.blackUsername() != null)) {
            throw new DataAccessException("Player color already taken.");
        }

        // update game with the new player
        GameData updatedGameData;
        if (playerColor.equals("white")) {
            updatedGameData = new GameData(gameData.gameID(), authData.username(), gameData.blackUsername(), gameData.gameName(), gameData.game());
        } else {
            updatedGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), authData.username(), gameData.gameName(), gameData.game());
        }
        gameDAO.updateGame(updatedGameData);

        return updatedGameData;
    }

    // Create Game logic
    public GameData createGame(String gameName, String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("Invalid auth token.");
        }

        // Create new game with the user as the white player
        ChessGame chessGame = new ChessGame();
        GameData gameData = new GameData(0, authData.username(), null, gameName, chessGame);
        gameDAO.createGame(gameData);

        return gameData;
    }

    // List Games logic
    public List<GameData> listGames(String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("Invalid auth token.");
        }
        // retrieve all games
        return gameDAO.listGames();
    }

    public void clear() {
        gameDAO.clear();  // Clear the in-memory game DAO or SQL DAO
    }
}
