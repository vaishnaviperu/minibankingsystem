package dao;

import db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * UserDAO - Data Access Object for user authentication.
 */
public class UserDAO {

    /**
     * Validates login credentials.
     *
     * @param username entered username
     * @param password entered password (plain-text; hash in production)
     * @return role string ("ADMIN" / "USER") if valid, null otherwise
     */
    public String validateLogin(String username, String password) {
        String sql = "SELECT role FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role");
                }
            }
        } catch (SQLException e) {
            System.err.println("Login validation error: " + e.getMessage());
        }
        return null; // invalid credentials
    }
}
