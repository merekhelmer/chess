package dataaccess;

import model.UserData;
import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {
    private final Map<String, UserData> users = new HashMap<>();

    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.username())) {
            throw new DataAccessException("User already exists.");
        }
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public boolean checkUser(String username, String password) throws DataAccessException {
        if (!users.containsKey(username)) {
            throw new DataAccessException("User not found.");
        }

        UserData user = users.get(username);
        return user.password().equals(password);
    }

    @Override
    public void clear() {
        users.clear();
    }
}