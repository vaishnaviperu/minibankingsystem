package ui;

import dao.AccountDAO;
import model.Account;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Random;

/**
 * CreateAccountFrame - Form to open a new bank account.
 */
public class CreateAccountFrame extends JFrame {

    private JTextField   nameField, phoneField, emailField, addressField;
    private JComboBox<String> typeCombo;
    private JTextField   balanceField;
    private JLabel       accNumLabel;

    private final AccountDAO accountDAO = new AccountDAO();
    private final JFrame     parent;
    private       String     generatedAccNum;

    public CreateAccountFrame(JFrame parent) {
        this.parent = parent;
        initUI();
    }

    private void initUI() {
        setTitle("Create New Account");
        setSize(480, 500);
        setLocationRelativeTo(parent);
        setResizable(false);

        generatedAccNum = generateAccountNumber();

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 248, 255));

        // Header
        JPanel header = header("➕  Create New Account");
        mainPanel.add(header, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(245, 248, 255));
        form.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        GridBagConstraints gbc = defaultGbc();

        // Account number (auto)
        addRow(form, gbc, 0, "Account Number:", accNumLabel = new JLabel(generatedAccNum));
        accNumLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        accNumLabel.setForeground(new Color(25, 85, 165));

        // Customer name
        nameField = tf(); addRow(form, gbc, 1, "Customer Name *:", nameField);

        // Phone
        phoneField = tf(); addRow(form, gbc, 2, "Phone Number:", phoneField);

        // Email
        emailField = tf(); addRow(form, gbc, 3, "Email:", emailField);

        // Address
        addressField = tf(); addRow(form, gbc, 4, "Address:", addressField);

        // Account type
        typeCombo = new JComboBox<>(new String[]{"Savings", "Current", "Fixed Deposit"});
        styleCombo(typeCombo);
        addRow(form, gbc, 5, "Account Type:", typeCombo);

        // Initial balance
        balanceField = tf(); addRow(form, gbc, 6, "Initial Balance (₹) *:", balanceField);

        mainPanel.add(form, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setBackground(new Color(245, 248, 255));
        JButton saveBtn   = btn("Save Account", new Color(34, 139, 34), Color.WHITE);
        JButton clearBtn  = btn("Clear",        new Color(180, 180, 185), Color.DARK_GRAY);
        JButton closeBtn  = btn("Close",        new Color(180, 60, 60),   Color.WHITE);
        btnPanel.add(saveBtn); btnPanel.add(clearBtn); btnPanel.add(closeBtn);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);
        add(mainPanel);

        saveBtn.addActionListener(e  -> saveAccount());
        clearBtn.addActionListener(e -> clearFields());
        closeBtn.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void saveAccount() {
        String name    = nameField.getText().trim();
        String phone   = phoneField.getText().trim();
        String email   = emailField.getText().trim();
        String address = addressField.getText().trim();
        String type    = (String) typeCombo.getSelectedItem();
        String balStr  = balanceField.getText().trim();

        if (name.isEmpty()) {
            error("Customer name is required."); return;
        }
        if (balStr.isEmpty()) {
            error("Initial balance is required."); return;
        }

        BigDecimal balance;
        try {
            balance = new BigDecimal(balStr);
            if (balance.compareTo(BigDecimal.ZERO) < 0) {
                error("Balance cannot be negative."); return;
            }
        } catch (NumberFormatException ex) {
            error("Invalid balance amount."); return;
        }

        Account acc = new Account(generatedAccNum, name, phone, email, address, type, balance);
        if (accountDAO.createAccount(acc)) {
            JOptionPane.showMessageDialog(this,
                    "Account created successfully!\nAccount Number: " + generatedAccNum,
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            generatedAccNum = generateAccountNumber();
            accNumLabel.setText(generatedAccNum);
        } else {
            error("Failed to create account. Please try again.");
        }
    }

    private void clearFields() {
        nameField.setText(""); phoneField.setText(""); emailField.setText("");
        addressField.setText(""); balanceField.setText("");
        typeCombo.setSelectedIndex(0);
    }

    // ── Helpers ──────────────────────────────────────────────────────

    private String generateAccountNumber() {
        return "ACC" + (10000 + new Random().nextInt(89999));
    }

    private JPanel header(String text) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(new Color(25, 85, 165));
        p.setPreferredSize(new Dimension(480, 55));
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 16));
        l.setForeground(Color.WHITE);
        p.add(l);
        return p;
    }

    private void addRow(JPanel p, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel l = new JLabel(labelText);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        p.add(l, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        p.add(field, gbc);
    }

    private JTextField tf() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setPreferredSize(new Dimension(220, 28));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 195, 220)),
                BorderFactory.createEmptyBorder(3, 5, 3, 5)));
        return tf;
    }

    private void styleCombo(JComboBox<?> c) {
        c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        c.setPreferredSize(new Dimension(220, 28));
    }

    private JButton btn(String t, Color bg, Color fg) {
        JButton b = new JButton(t);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(bg); b.setForeground(fg);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(120, 32));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private GridBagConstraints defaultGbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(7, 5, 7, 5);
        g.fill   = GridBagConstraints.HORIZONTAL;
        return g;
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
}
