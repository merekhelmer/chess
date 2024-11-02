package dataaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {
    private static final String DATABASE_NAME;
    private static final String USER;
    private static final String PASSWORD;
    private static final String CONNECTION_URL;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        try {
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) {
                    throw new Exception("Unable to load db.properties");
                }
                Properties props = new Properties();
                props.load(propStream);
                DATABASE_NAME = props.getProperty("db.name");
                USER = props.getProperty("db.user");
                PASSWORD = props.getProperty("db.password");

                var host = props.getProperty("db.host");
                var port = Integer.parseInt(props.getProperty("db.port"));
                CONNECTION_URL = String.format("jdbc:mysql://%s:%d", host, port);
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
        }
    }

    // SQL statements for creating tables
    private static final String[] CREATE_TABLE_STATEMENTS = {
            """
        CREATE TABLE IF NOT EXISTS User (
            username VARCHAR(255) PRIMARY KEY,
            password VARCHAR(255) NOT NULL,
            email VARCHAR(255)
        );
        """,
            """
        CREATE TABLE IF NOT EXISTS Auth (
            authToken VARCHAR(255) PRIMARY KEY,
            username VARCHAR(255),
            FOREIGN KEY (username) REFERENCES User(username) ON DELETE CASCADE
        );
        """,
            """
        CREATE TABLE IF NOT EXISTS Game (
            gameID INT AUTO_INCREMENT PRIMARY KEY,
            whiteUsername VARCHAR(255),
            blackUsername VARCHAR(255),
            gameName VARCHAR(255) NOT NULL,
            gameData JSON,
            FOREIGN KEY (whiteUsername) REFERENCES User(username),
            FOREIGN KEY (blackUsername) REFERENCES User(username)
        );
        """
    };

    /**
     * Creates the database if it does not already exist.
     */
    public static void createDatabase() throws DataAccessException {
        try {
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            var statement = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error creating database: " + e.getMessage());
        }
    }


    public static void createTables() throws DataAccessException {
        try (var conn = getConnection()) {
            for (String sql : CREATE_TABLE_STATEMENTS) {
                try (var statement = conn.prepareStatement(sql)) {
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error creating tables: " + e.getMessage());
        }
    }


    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DbInfo.getConnection(databaseName)) {
     * // execute SQL statements.
     * }
     * </code>
     */
    static Connection getConnection() throws DataAccessException {
        try {
            String dbUrl = CONNECTION_URL + "/" + DATABASE_NAME;
            var conn = DriverManager.getConnection(dbUrl, USER, PASSWORD);
            return conn;
        } catch (SQLException e) {
            throw new DataAccessException("Error connecting to database: " + e.getMessage());
        }
    }

}
