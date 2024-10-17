package dataaccess;

import model.GameData;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    private final Map<Integer, GameData> games;

    public MemoryGameDAO() {
        games = new HashMap<>();
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        if (games.containsKey(game.gameID())) {
            throw new DataAccessException("Game already exists.");
        }
        games.put(game.gameID(), game);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        if (!games.containsKey(gameID)) {
            throw new DataAccessException("Game not found.");
        }
        return games.get(gameID);
    }

    @Override
    public List<GameData> listGames() {
        return new ArrayList<>(games.values());
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        if (!games.containsKey(game.gameID())) {
            throw new DataAccessException("Game not found.");
        }
        games.put(game.gameID(), game);  // update existing game
    }

    @Override
    public void clear() {
        games.clear();
    }
}

