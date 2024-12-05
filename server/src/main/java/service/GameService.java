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

        // joining if the color is open
        if (playerColor == ChessGame.TeamColor.WHITE && (game.whiteUsername() == null || game.whiteUsername().isEmpty())) {
            game = new GameData(game.gameID(), authData.username(), game.blackUsername(), game.gameName(), game.game());
        } else if (playerColor == ChessGame.TeamColor.BLACK && (game.blackUsername() == null || game.blackUsername().isEmpty())) {
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
        game.setOver(true); // Use a boolean flag to indicate the game is over
        updateGame(gameID, game);
    }

    public void clear() throws DataAccessException {
        gameDAO.clear();
    }

    public String getWhitePlayer(int gameID) throws DataAccessException {
        GameData game = gameDAO.getGame(gameID);
        if (game == null) {
            throw new DataAccessException("Error: Game not found");
        }
        return game.whiteUsername();
    }

    public String getBlackPlayer(int gameID) throws DataAccessException {
        GameData game = gameDAO.getGame(gameID);
        if (game == null) {
            throw new DataAccessException("Error: Game not found");
        }
        return game.blackUsername();
    }

    public String getUsernameFromAuthToken(String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("Invalid authToken.");
        }
        return authData.username();
    }

    public void removePlayerFromGame(int gameID, String username) throws DataAccessException {
        GameData game = gameDAO.getGame(gameID);
        if (game == null) {
            throw new DataAccessException("Error: Game not found");
        }

        // clear the player's slot
        String whitePlayer = game.whiteUsername();
        String blackPlayer = game.blackUsername();

        if (username.equals(whitePlayer)) {
            game = new GameData(game.gameID(), null, blackPlayer, game.gameName(), game.game());
        } else if (username.equals(blackPlayer)) {
            game = new GameData(game.gameID(), whitePlayer, null, game.gameName(), game.game());
        }

        gameDAO.updateGame(game);
    }
}

