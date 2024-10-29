package dataaccess;

import model.GameData;

import java.sql.Connection;
import java.util.List;

public class SQLGameDAO implements GameDAO {
    private final Connection connection;

    public SQLGameDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        throw new DataAccessException("createGame method not implemented yet.");
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        throw new DataAccessException("getGame method not implemented yet.");
    }

    @Override
    public List<GameData> listGames() {
        return List.of();
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        // Stubbed method - will eventually add SQL update logic here
        throw new DataAccessException("updateGame method not implemented yet.");
    }

    @Override
    public void clear() {
    }
}
