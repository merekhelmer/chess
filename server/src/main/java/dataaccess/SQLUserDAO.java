package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUserDAO implements UserDAO {

    @Override
    public void createUser(UserData user) throws DataAccessException {
        var hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        var sql = "INSERT INTO User (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.username());
            stmt.setString(2, hashedPassword);
            stmt.setString(3, user.email());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error inserting user: " + e.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        var sql = "SELECT * FROM User WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // UserData object returned with hashed password stored database
                return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error fetching user: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM User";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing users: " + e.getMessage());
        }
    }
}
