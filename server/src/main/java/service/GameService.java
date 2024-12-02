package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;

import java.util.List;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public GameData joinGame(int gameID, ChessGame.TeamColor playerColor, String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("Error: Invalid auth token");
        }

        GameData game = gameDAO.getGame(gameID);
        if (game == null) {
            throw new DataAccessException("Error: Game not found");
        }

        // check if the requested color is available
        if (playerColor == ChessGame.TeamColor.WHITE && game.whiteUsername() == null) {
            game = new GameData(game.gameID(), authData.username(), game.blackUsername(), game.gameName(), game.game());
        } else if (playerColor == ChessGame.TeamColor.BLACK && game.blackUsername() == null) {
            game = new GameData(game.gameID(), game.whiteUsername(), authData.username(), game.gameName(), game.game());
        } else {
            throw new DataAccessException("Error: Requested team color is already taken");
        }

        gameDAO.updateGame(game);
        return game;
    }

    public GameData createGame(String gameName, String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("Error: Invalid auth token");
        }

        int gameID = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);

        GameData gameData = new GameData(gameID, null, null, gameName, new ChessGame());
        gameDAO.createGame(gameData);

        return gameData;
    }

    public List<GameData> listGames(String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("Error: Invalid auth token.");
        }
        return gameDAO.listGames();
    }

    public ChessGame getGame(int gameID) throws DataAccessException {
        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            throw new DataAccessException("Error: Game not found");
        }
        return gameData.game(); //ChessGame object
    }

    public void updateGame(int gameID, ChessGame updatedGame) throws DataAccessException {
        GameData existingGame = gameDAO.getGame(gameID);
        if (existingGame == null) {
            throw new DataAccessException("Error: Game not found");
        }
        GameData updatedGameData = new GameData(
                existingGame.gameID(),
                existingGame.whiteUsername(),
                existingGame.blackUsername(),
                existingGame.gameName(),
                updatedGame
        );
        gameDAO.updateGame(updatedGameData);
    }

    public void markGameAsResigned(int gameID) throws DataAccessException {
        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            throw new DataAccessException("Error: Game not found");
        }
        ChessGame game = gameData.game();
        game.setTeamTurn(null); //indicates game is over
        updateGame(gameID, game);
    }

    public void clear() throws DataAccessException {
        gameDAO.clear();
    }
}

