package ui;

import dao.AccountDAO;
import dao.TransactionDAO;
import model.Account;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

/**
 * DepositFrame - Deposit money into a bank account.
 */
public class DepositFrame extends JFrame {

    private JTextField      accField, amountField;
    private JLabel          nameLabel, balanceLabel;
    private JButton         lookupBtn, depositBtn, clearBtn;

    private final AccountDAO     accountDAO     = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final JFrame         parent;
    private       Account        currentAccount;

    public DepositFrame(JFrame parent) {
        this.parent = parent;
        initUI();
    }

    private void initUI() {
        setTitle("Deposit");
        setSize(430, 380);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 250, 245));
        mainPanel.add(header("💵  Deposit Money"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(245, 250, 245));
        form.setBorder(BorderFactory.createEmptyBorder(25, 40, 10, 40));
        GridBagConstraints g = gbc();

        // Account number row
        g.gridx = 0; g.gridy = 0; g.weightx = 0;
        form.add(lbl("Account Number *:"), g);
        JPanel accRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        accRow.setBackground(new Color(245, 250, 245));
        accField = tf(12);
        lookupBtn = miniBtn("Lookup", new Color(25, 85, 165));
        accRow.add(accField); accRow.add(lookupBtn);
        g.gridx = 1; g.weightx = 1.0;
        form.add(accRow, g);

        // Customer name (read-only info)
        g.gridx = 0; g.gridy = 1; g.weightx = 0;
        form.add(lbl("Customer Name:"), g);
        nameLabel = infoLabel("–");
        g.gridx = 1; g.weightx = 1.0;
        form.add(nameLabel, g);

        // Current balance (read-only info)
        g.gridx = 0; g.gridy = 2; g.weightx = 0;
        form.add(lbl("Current Balance:"), g);
        balanceLabel = infoLabel("–");
        balanceLabel.setForeground(new Color(25, 100, 25));
        g.gridx = 1; g.weightx = 1.0;
        form.add(balanceLabel, g);

        // Divider
        g.gridx = 0; g.gridy = 3; g.gridwidth = 2;
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(200, 215, 200));
        form.add(sep, g);
        g.gridwidth = 1;

        // Amount
        g.gridx = 0; g.gridy = 4; g.weightx = 0;
        form.add(lbl("Deposit Amount (₹) *:"), g);
        amountField = tf(15);
        g.gridx = 1; g.weightx = 1.0;
        form.add(amountField, g);

        mainPanel.add(form, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        btnPanel.setBackground(new Color(245, 250, 245));
        depositBtn = btn("Deposit", new Color(34, 139, 34), Color.WHITE);
        clearBtn   = btn("Clear",   new Color(180, 180, 185), Color.DARK_GRAY);
        JButton closeBtn = btn("Close", new Color(180, 60, 60), Color.WHITE);
        btnPanel.add(depositBtn); btnPanel.add(clearBtn); btnPanel.add(closeBtn);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);
        add(mainPanel);

        lookupBtn.addActionListener(e  -> lookupAccount());
        depositBtn.addActionListener(e -> doDeposit());
        clearBtn.addActionListener(e   -> clearAll());
        closeBtn.addActionListener(e   -> dispose());

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

    private void doDeposit() {
        if (currentAccount == null) { error("Lookup an account first."); return; }
        String amtStr = amountField.getText().trim();
        if (amtStr.isEmpty()) { error("Enter deposit amount."); return; }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amtStr);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) { error("Amount must be positive."); return; }
        } catch (NumberFormatException ex) {
            error("Invalid amount."); return;
        }

        if (transactionDAO.deposit(currentAccount.getAccountNumber(), amount)) {
            BigDecimal newBal = currentAccount.getBalance().add(amount);
            JOptionPane.showMessageDialog(this,
                    "✅  Deposit Successful!\n" +
                    "Amount Deposited: ₹ " + amount + "\n" +
                    "New Balance: ₹ " + newBal,
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            currentAccount.setBalance(newBal);
            balanceLabel.setText("₹ " + newBal.toPlainString());
            amountField.setText("");
        } else {
            error("Deposit failed. Please try again.");
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
        p.setBackground(new Color(34, 120, 34));
        p.setPreferredSize(new Dimension(430, 52));
        JLabel l = new JLabel(text); l.setFont(new Font("Segoe UI", Font.BOLD, 16)); l.setForeground(Color.WHITE);
        p.add(l); return p;
    }

    private JLabel lbl(String t) {
        JLabel l = new JLabel(t); l.setFont(new Font("Segoe UI", Font.PLAIN, 13)); return l;
    }

    private JLabel infoLabel(String t) {
        JLabel l = new JLabel(t); l.setFont(new Font("Segoe UI", Font.BOLD, 13)); return l;
    }

    private JTextField tf(int cols) {
        JTextField tf = new JTextField(cols); tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 200, 180)),
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
