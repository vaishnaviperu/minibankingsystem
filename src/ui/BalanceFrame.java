package ui;

import dao.AccountDAO;
import model.Account;

import javax.swing.*;
import java.awt.*;

/**
 * BalanceFrame - Check balance for a given account number.
 */
public class BalanceFrame extends JFrame {

    private JTextField  accField;
    private JPanel      infoPanel;
    private JLabel      nameVal, typeVal, balanceVal, dateVal;

    private final AccountDAO accountDAO = new AccountDAO();
    private final JFrame     parent;

    public BalanceFrame(JFrame parent) {
        this.parent = parent;
        initUI();
    }

    private void initUI() {
        setTitle("Check Balance");
        setSize(420, 380);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(new Color(248, 248, 235));
        mainPanel.add(header("💰  Check Balance"), BorderLayout.NORTH);

        // Search row
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 18));
        searchPanel.setBackground(new Color(248, 248, 235));
        JLabel l = new JLabel("Account Number:");
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        accField = new JTextField(14);
        accField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        accField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 150)),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)));
        JButton checkBtn = btn("Check", new Color(160, 110, 0), Color.WHITE);
        JButton clearBtn = btn("Clear", new Color(180, 180, 185), Color.DARK_GRAY);
        searchPanel.add(l); searchPanel.add(accField);
        searchPanel.add(checkBtn); searchPanel.add(clearBtn);
        mainPanel.add(searchPanel, BorderLayout.BEFORE_FIRST_LINE);

        // Info card
        infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(new Color(255, 255, 240));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 200, 150), 1),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)));
        infoPanel.setVisible(false);

        GridBagConstraints g = gbc();
        nameVal    = valLabel("–");
        typeVal    = valLabel("–");
        balanceVal = valLabel("–");
        balanceVal.setFont(new Font("Segoe UI", Font.BOLD, 20));
        balanceVal.setForeground(new Color(20, 120, 20));
        dateVal    = valLabel("–");

        addRow(infoPanel, g, 0, "Customer Name:",   nameVal);
        addRow(infoPanel, g, 1, "Account Type:",    typeVal);
        addRow(infoPanel, g, 3, "Member Since:",    dateVal);

        // Balance row spans full width
        g.gridx = 0; g.gridy = 2; g.gridwidth = 2; g.anchor = GridBagConstraints.CENTER;
        JPanel balRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        balRow.setBackground(new Color(255, 255, 240));
        JLabel balLbl = new JLabel("Available Balance:");
        balLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        balRow.add(balLbl); balRow.add(balanceVal);
        infoPanel.add(balRow, g);
        g.gridwidth = 1;

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(new Color(248, 248, 235));
        center.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH; gc.weightx = 1; gc.weighty = 1;
        center.add(infoPanel, gc);
        mainPanel.add(center, BorderLayout.CENTER);

        JPanel btmPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        btmPanel.setBackground(new Color(248, 248, 235));
        JButton closeBtn = btn("Close", new Color(110, 110, 120), Color.WHITE);
        btmPanel.add(closeBtn);
        mainPanel.add(btmPanel, BorderLayout.SOUTH);
        add(mainPanel);

        checkBtn.addActionListener(e -> checkBalance());
        clearBtn.addActionListener(e -> { accField.setText(""); infoPanel.setVisible(false); });
        closeBtn.addActionListener(e -> dispose());
        accField.addActionListener(e -> checkBalance()); // press Enter

        setVisible(true);
    }

    private void checkBalance() {
        String accNum = accField.getText().trim();
        if (accNum.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter an account number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Account acc = accountDAO.getAccountByNumber(accNum);
        if (acc == null) {
            infoPanel.setVisible(false);
            JOptionPane.showMessageDialog(this, "Account not found: " + accNum, "Not Found", JOptionPane.WARNING_MESSAGE);
            return;
        }
        nameVal.setText(acc.getCustomerName());
        typeVal.setText(acc.getAccountType());
        balanceVal.setText("₹ " + acc.getBalance().toPlainString());
        dateVal.setText(acc.getCreatedAt() == null ? "N/A" : acc.getCreatedAt().toString().substring(0, 10));
        infoPanel.setVisible(true);
        pack();
        setLocationRelativeTo(parent);
    }

    // ── Helpers ──────────────────────────────────────────────────────

    private JPanel header(String text) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(new Color(155, 110, 0));
        p.setPreferredSize(new Dimension(420, 52));
        JLabel l = new JLabel(text); l.setFont(new Font("Segoe UI", Font.BOLD, 16)); l.setForeground(Color.WHITE);
        p.add(l); return p;
    }

    private JLabel valLabel(String t) { JLabel l = new JLabel(t); l.setFont(new Font("Segoe UI", Font.BOLD, 13)); return l; }

    private void addRow(JPanel p, GridBagConstraints g, int row, String labelText, JLabel val) {
        g.gridx = 0; g.gridy = row; g.weightx = 0; g.anchor = GridBagConstraints.WEST;
        JLabel k = new JLabel(labelText); k.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        p.add(k, g);
        g.gridx = 1; g.weightx = 1.0;
        p.add(val, g);
    }

    private JButton btn(String t, Color bg, Color fg) {
        JButton b = new JButton(t);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(bg); b.setForeground(fg);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(90, 30));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private GridBagConstraints gbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.fill   = GridBagConstraints.HORIZONTAL;
        return g;
    }
}
