package dao;

import db.DBConnection;
import model.Account;
import model.Transaction;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * TransactionDAO - Handles deposit, withdrawal, and transaction history.
 * Uses JDBC transactions to keep account balance and transaction log in sync.
 */
public class TransactionDAO {

    private final AccountDAO accountDAO = new AccountDAO();

    // ─── Deposit ─────────────────────────────────────────────────────

    public boolean deposit(String accountNumber, BigDecimal amount) {
        Account acc = accountDAO.getAccountByNumber(accountNumber);
        if (acc == null) return false;

        BigDecimal newBalance = acc.getBalance().add(amount);
        return executeTransaction(accountNumber, "DEPOSIT", amount, newBalance);
    }

    // ─── Withdraw ────────────────────────────────────────────────────

    /**
     * Returns:
     *   0  - success
     *  -1  - account not found
     *  -2  - insufficient balance
     *  -3  - DB error
     */
    public int withdraw(String accountNumber, BigDecimal amount) {
        Account acc = accountDAO.getAccountByNumber(accountNumber);
        if (acc == null) return -1;

        if (acc.getBalance().compareTo(amount) < 0) return -2;

        BigDecimal newBalance = acc.getBalance().subtract(amount);
        return executeTransaction(accountNumber, "WITHDRAWAL", amount, newBalance) ? 0 : -3;
    }

    // ─── Transaction history ─────────────────────────────────────────

    public List<Transaction> getTransactionsByAccount(String accountNumber) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_number = ? ORDER BY transaction_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Transaction t = new Transaction();
                    t.setTransactionId(rs.getInt("transaction_id"));
                    t.setAccountNumber(rs.getString("account_number"));
                    t.setTransactionType(rs.getString("transaction_type"));
                    t.setAmount(rs.getBigDecimal("amount"));
                    t.setTransactionDate(rs.getTimestamp("transaction_date"));
                    t.setBalanceAfter(rs.getBigDecimal("balance_after"));
                    list.add(t);
                }
            }
        } catch (SQLException e) {
            System.err.println("getTransactions error: " + e.getMessage());
        }
        return list;
    }

    // ─── Private helper ──────────────────────────────────────────────

    private boolean executeTransaction(String accountNumber, String type,
                                       BigDecimal amount, BigDecimal newBalance) {
        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);

            // 1. Update account balance
            accountDAO.updateBalance(conn, accountNumber, newBalance);

            // 2. Insert transaction record
            String sql = "INSERT INTO transactions (account_number, transaction_type, amount, balance_after) "
                       + "VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, accountNumber);
                ps.setString(2, type);
                ps.setBigDecimal(3, amount);
                ps.setBigDecimal(4, newBalance);
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ignored) {}
            System.err.println("executeTransaction error: " + e.getMessage());
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }
}
