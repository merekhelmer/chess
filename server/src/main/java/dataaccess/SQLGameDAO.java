package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLGameDAO implements GameDAO {
    private final Gson gson = new Gson();

    @Override
    public void createGame(GameData game) throws DataAccessException {
        String sql = "INSERT INTO Game (gameID, gameName, whiteUsername, blackUsername, gameData) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, game.gameID());
            stmt.setString(2, game.gameName());

            if (game.whiteUsername() != null) {
                stmt.setString(3, game.whiteUsername());
            } else {
                stmt.setNull(3, java.sql.Types.VARCHAR);
            }

            if (game.blackUsername() != null) {
                stmt.setString(4, game.blackUsername());
            } else {
                stmt.setNull(4, java.sql.Types.VARCHAR);
            }

            stmt.setString(5, gson.toJson(game.game()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error creating game: " + e.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String sql = "SELECT * FROM Game WHERE gameID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new GameData(
                            rs.getInt("gameID"),
                            rs.getString("whiteUsername"),
                            rs.getString("blackUsername"),
                            rs.getString("gameName"),
                            gson.fromJson(rs.getString("gameData"), ChessGame.class)  // deserialize JSON to ChessGame
                    );
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving game: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        String sql = "SELECT * FROM Game";
        List<GameData> games = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                games.add(new GameData(
                        rs.getInt("gameID"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        gson.fromJson(rs.getString("gameData"), ChessGame.class)
                ));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error listing games: " + e.getMessage());
        }
        return games;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        String sql = "UPDATE Game SET whiteUsername = ?, blackUsername = ?, gameName = ?, gameData = ? WHERE gameID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (game.whiteUsername() != null) {
                stmt.setString(1, game.whiteUsername());
            } else {
                stmt.setNull(1, java.sql.Types.VARCHAR);
            }

            if (game.blackUsername() != null) {
                stmt.setString(2, game.blackUsername());
            } else {
                stmt.setNull(2, java.sql.Types.VARCHAR);
            }

            stmt.setString(3, game.gameName());
            stmt.setString(4, gson.toJson(game.game()));
            stmt.setInt(5, game.gameID());

            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated == 0) {
                throw new DataAccessException("No game found with gameID: " + game.gameID());
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error updating game: " + e.getMessage());
        }
    }


    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM Game";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing games: " + e.getMessage());
        }
    }
}
