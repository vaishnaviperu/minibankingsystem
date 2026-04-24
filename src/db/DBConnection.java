package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection - Manages JDBC connection to MySQL.
 *
 * !! CHANGE YOUR CREDENTIALS HERE !!
 *   DB_URL  - update host/port/database name if different
 *   DB_USER - your MySQL username
 *   DB_PASS - your MySQL password
 */
public class DBConnection {

    // ─── CONFIGURE THESE ────────────────────────────────────────────
    private static final String DB_URL  = "jdbc:mysql://localhost:3306/MiniBankDB?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "root123";
    // ────────────────────────────────────────────────────────────────

    private static Connection connection = null;

    /** Returns a singleton Connection instance. */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Add connector JAR to classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    /** Closes the connection (call on application exit). */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
