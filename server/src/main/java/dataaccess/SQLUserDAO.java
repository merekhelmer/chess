package dataaccess;

import model.UserData;

import java.sql.Connection;

public class SQLUserDAO implements UserDAO {
    private final Connection connection;

    public SQLUserDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        throw new DataAccessException("createUser method not implemented yet.");
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        throw new DataAccessException("getUser method not implemented yet.");
    }

    @Override
    public void clear() {
    }
}
