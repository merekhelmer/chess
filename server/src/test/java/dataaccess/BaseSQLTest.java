package dataaccess;

import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class BaseSQLTest {

    @BeforeEach
    void clearDatabase() throws SQLException {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {

            // clear tables in the correct order to prevent foreign key issues
            stmt.executeUpdate("DELETE FROM Game");
            stmt.executeUpdate("DELETE FROM Auth");
            stmt.executeUpdate("DELETE FROM User");
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
