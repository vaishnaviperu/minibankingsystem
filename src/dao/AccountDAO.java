package dao;

import db.DBConnection;
import model.Account;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AccountDAO - Data Access Object for account management.
 */
public class AccountDAO {

    // ─── Create ──────────────────────────────────────────────────────

    public boolean createAccount(Account acc) {
        String sql = "INSERT INTO accounts (account_number, customer_name, phone, email, address, account_type, balance) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, acc.getAccountNumber());
            ps.setString(2, acc.getCustomerName());
            ps.setString(3, acc.getPhone());
            ps.setString(4, acc.getEmail());
            ps.setString(5, acc.getAddress());
            ps.setString(6, acc.getAccountType());
            ps.setBigDecimal(7, acc.getBalance());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("createAccount error: " + e.getMessage());
            return false;
        }
    }

    // ─── Read all ────────────────────────────────────────────────────

    public List<Account> getAllAccounts() {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM accounts ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("getAllAccounts error: " + e.getMessage());
        }
        return list;
    }

    // ─── Read by account number ──────────────────────────────────────

    public Account getAccountByNumber(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("getAccountByNumber error: " + e.getMessage());
        }
        return null;
    }

    // ─── Update ──────────────────────────────────────────────────────

    public boolean updateAccount(Account acc) {
        String sql = "UPDATE accounts SET customer_name=?, phone=?, email=?, address=?, account_type=? "
                   + "WHERE account_number=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, acc.getCustomerName());
            ps.setString(2, acc.getPhone());
            ps.setString(3, acc.getEmail());
            ps.setString(4, acc.getAddress());
            ps.setString(5, acc.getAccountType());
            ps.setString(6, acc.getAccountNumber());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("updateAccount error: " + e.getMessage());
            return false;
        }
    }

    // ─── Delete ──────────────────────────────────────────────────────

    public boolean deleteAccount(String accountNumber) {
        // Delete transactions first (FK constraint)
        String delTx  = "DELETE FROM transactions WHERE account_number = ?";
        String delAcc = "DELETE FROM accounts WHERE account_number = ?";

        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(delTx)) {
                ps1.setString(1, accountNumber);
                ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = conn.prepareStatement(delAcc)) {
                ps2.setString(1, accountNumber);
                ps2.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ignored) {}
            System.err.println("deleteAccount error: " + e.getMessage());
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }

    // ─── Balance update (used by TransactionDAO) ─────────────────────

    public boolean updateBalance(Connection conn, String accountNumber, BigDecimal newBalance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, newBalance);
            ps.setString(2, accountNumber);
            return ps.executeUpdate() > 0;
        }
    }

    // ─── Helper ──────────────────────────────────────────────────────

    private Account mapRow(ResultSet rs) throws SQLException {
        Account a = new Account();
        a.setAccountId(rs.getInt("account_id"));
        a.setAccountNumber(rs.getString("account_number"));
        a.setCustomerName(rs.getString("customer_name"));
        a.setPhone(rs.getString("phone"));
        a.setEmail(rs.getString("email"));
        a.setAddress(rs.getString("address"));
        a.setAccountType(rs.getString("account_type"));
        a.setBalance(rs.getBigDecimal("balance"));
        a.setCreatedAt(rs.getTimestamp("created_at"));
        return a;
    }
}
