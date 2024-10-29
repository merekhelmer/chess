package dataaccess;

import model.AuthData;

import java.sql.Connection;

public class SQLAuthDAO implements AuthDAO {
    private final Connection connection;

    public SQLAuthDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void createAuth(AuthData auth) {
    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) {
    }

    @Override
    public void clear() {
    }
}
