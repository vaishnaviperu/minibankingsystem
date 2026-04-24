package ui;

import dao.AccountDAO;
import dao.TransactionDAO;
import model.Account;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

/**
 * WithdrawFrame - Withdraw money from a bank account.
 */
public class WithdrawFrame extends JFrame {

    private JTextField      accField, amountField;
    private JLabel          nameLabel, balanceLabel;
    private JButton         lookupBtn, withdrawBtn;

    private final AccountDAO     accountDAO     = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final JFrame         parent;
    private       Account        currentAccount;

    public WithdrawFrame(JFrame parent) {
        this.parent = parent;
        initUI();
    }

    private void initUI() {
        setTitle("Withdraw");
        setSize(430, 380);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(255, 247, 245));
        mainPanel.add(header("💸  Withdraw Money"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(255, 247, 245));
        form.setBorder(BorderFactory.createEmptyBorder(25, 40, 10, 40));
        GridBagConstraints g = gbc();

        // Account number row
        g.gridx = 0; g.gridy = 0; g.weightx = 0;
        form.add(lbl("Account Number *:"), g);
        JPanel accRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        accRow.setBackground(new Color(255, 247, 245));
        accField  = tf(12);
        lookupBtn = miniBtn("Lookup", new Color(25, 85, 165));
        accRow.add(accField); accRow.add(lookupBtn);
        g.gridx = 1; g.weightx = 1.0;
        form.add(accRow, g);

        g.gridx = 0; g.gridy = 1; g.weightx = 0;
        form.add(lbl("Customer Name:"), g);
        nameLabel = infoLabel("–");
        g.gridx = 1; g.weightx = 1.0;
        form.add(nameLabel, g);

        g.gridx = 0; g.gridy = 2; g.weightx = 0;
        form.add(lbl("Available Balance:"), g);
        balanceLabel = infoLabel("–");
        balanceLabel.setForeground(new Color(160, 60, 0));
        g.gridx = 1; g.weightx = 1.0;
        form.add(balanceLabel, g);

        g.gridx = 0; g.gridy = 3; g.gridwidth = 2;
        form.add(new JSeparator(), g);
        g.gridwidth = 1;

        g.gridx = 0; g.gridy = 4; g.weightx = 0;
        form.add(lbl("Withdraw Amount (₹) *:"), g);
        amountField = tf(15);
        g.gridx = 1; g.weightx = 1.0;
        form.add(amountField, g);

        mainPanel.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        btnPanel.setBackground(new Color(255, 247, 245));
        withdrawBtn = btn("Withdraw", new Color(200, 60, 40), Color.WHITE);
        JButton clearBtn = btn("Clear", new Color(180, 180, 185), Color.DARK_GRAY);
        JButton closeBtn = btn("Close", new Color(110, 110, 120), Color.WHITE);
        btnPanel.add(withdrawBtn); btnPanel.add(clearBtn); btnPanel.add(closeBtn);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);
        add(mainPanel);

        lookupBtn.addActionListener(e   -> lookupAccount());
        withdrawBtn.addActionListener(e -> doWithdraw());
        clearBtn.addActionListener(e    -> clearAll());
        closeBtn.addActionListener(e    -> dispose());

        setVisible(true);
    }

    private void lookupAccount() {
        String accNum = accField.getText().trim();
        if (accNum.isEmpty()) { error("Enter an account number."); return; }
        currentAccount = accountDAO.getAccountByNumber(accNum);
        if (currentAccount == null) {
            nameLabel.setText("–"); balanceLabel.setText("–");
            error("Account not found: " + accNum);
        } else {
            nameLabel.setText(currentAccount.getCustomerName());
            balanceLabel.setText("₹ " + currentAccount.getBalance().toPlainString());
        }
    }

    private void doWithdraw() {
        if (currentAccount == null) { error("Lookup an account first."); return; }
        String amtStr = amountField.getText().trim();
        if (amtStr.isEmpty()) { error("Enter withdrawal amount."); return; }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amtStr);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) { error("Amount must be positive."); return; }
        } catch (NumberFormatException ex) {
            error("Invalid amount."); return;
        }

        int result = transactionDAO.withdraw(currentAccount.getAccountNumber(), amount);
        switch (result) {
            case 0 -> {
                BigDecimal newBal = currentAccount.getBalance().subtract(amount);
                JOptionPane.showMessageDialog(this,
                        "✅  Withdrawal Successful!\n" +
                        "Amount Withdrawn: ₹ " + amount + "\n" +
                        "New Balance: ₹ " + newBal,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                currentAccount.setBalance(newBal);
                balanceLabel.setText("₹ " + newBal.toPlainString());
                amountField.setText("");
            }
            case -2 -> error("Insufficient balance!\nAvailable: ₹ " + currentAccount.getBalance());
            default -> error("Withdrawal failed. Please try again.");
        }
    }

    private void clearAll() {
        accField.setText(""); amountField.setText("");
        nameLabel.setText("–"); balanceLabel.setText("–");
        currentAccount = null;
    }

    // ── Helpers ──────────────────────────────────────────────────────

    private JPanel header(String text) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(new Color(190, 55, 35));
        p.setPreferredSize(new Dimension(430, 52));
        JLabel l = new JLabel(text); l.setFont(new Font("Segoe UI", Font.BOLD, 16)); l.setForeground(Color.WHITE);
        p.add(l); return p;
    }

    private JLabel lbl(String t) { JLabel l = new JLabel(t); l.setFont(new Font("Segoe UI", Font.PLAIN, 13)); return l; }
    private JLabel infoLabel(String t) { JLabel l = new JLabel(t); l.setFont(new Font("Segoe UI", Font.BOLD, 13)); return l; }

    private JTextField tf(int cols) {
        JTextField tf = new JTextField(cols); tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 180, 180)),
                BorderFactory.createEmptyBorder(3, 5, 3, 5)));
        return tf;
    }

    private JButton btn(String t, Color bg, Color fg) {
        JButton b = new JButton(t);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(bg); b.setForeground(fg);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(105, 32));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton miniBtn(String t, Color bg) {
        JButton b = new JButton(t);
        b.setFont(new Font("Segoe UI", Font.BOLD, 11));
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(75, 26));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private GridBagConstraints gbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(9, 5, 9, 5);
        g.fill   = GridBagConstraints.HORIZONTAL;
        return g;
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
