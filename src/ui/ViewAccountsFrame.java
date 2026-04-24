package ui;

import dao.AccountDAO;
import model.Account;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * ViewAccountsFrame - Displays all accounts in a table, with search,
 * update, and delete capabilities.
 */
public class ViewAccountsFrame extends JFrame {

    private JTable           table;
    private DefaultTableModel tableModel;
    private JTextField       searchField;
    private final AccountDAO accountDAO = new AccountDAO();
    private final JFrame     parent;

    private static final String[] COLUMNS = {
        "Account No", "Customer Name", "Phone", "Email",
        "Account Type", "Balance (₹)", "Created At"
    };

    public ViewAccountsFrame(JFrame parent) {
        this.parent = parent;
        initUI();
        loadAccounts(null);
    }

    private void initUI() {
        setTitle("View Accounts");
        setSize(900, 520);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(new Color(245, 248, 255));

        // Header
        mainPanel.add(header("👥  All Bank Accounts"), BorderLayout.NORTH);

        // Search bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        searchPanel.setBackground(new Color(235, 240, 252));
        searchPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 210, 230)));
        JLabel sl = new JLabel("Search by Account No:");
        sl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField = new JTextField(15);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JButton searchBtn = smallBtn("Search", new Color(25, 85, 165), Color.WHITE);
        JButton refreshBtn = smallBtn("Refresh All", new Color(70, 130, 70), Color.WHITE);
        searchPanel.add(sl); searchPanel.add(searchField);
        searchPanel.add(searchBtn); searchPanel.add(refreshBtn);
        mainPanel.add(searchPanel, BorderLayout.BEFORE_FIRST_LINE);

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(25, 85, 165));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(200, 220, 255));
        table.setGridColor(new Color(220, 225, 235));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane scroll = new JScrollPane(table);
        mainPanel.add(scroll, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        btnPanel.setBackground(new Color(245, 248, 255));
        JButton editBtn   = smallBtn("✏ Edit Selected",   new Color(160, 100, 0),   Color.WHITE);
        JButton deleteBtn = smallBtn("🗑 Delete Selected", new Color(180, 50, 50),   Color.WHITE);
        JButton closeBtn  = smallBtn("Close",              new Color(110, 110, 120), Color.WHITE);
        btnPanel.add(editBtn); btnPanel.add(deleteBtn); btnPanel.add(closeBtn);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        add(mainPanel);

        searchBtn.addActionListener(e  -> loadAccounts(searchField.getText().trim()));
        refreshBtn.addActionListener(e -> { searchField.setText(""); loadAccounts(null); });
        editBtn.addActionListener(e    -> editSelected());
        deleteBtn.addActionListener(e  -> deleteSelected());
        closeBtn.addActionListener(e   -> dispose());

        setVisible(true);
    }

    private void loadAccounts(String accNumber) {
        tableModel.setRowCount(0);
        List<Account> list;
        if (accNumber == null || accNumber.isEmpty()) {
            list = accountDAO.getAllAccounts();
        } else {
            Account a = accountDAO.getAccountByNumber(accNumber);
            list = (a != null) ? List.of(a) : List.of();
            if (list.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No account found with number: " + accNumber,
                        "Not Found", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        for (Account a : list) {
            tableModel.addRow(new Object[]{
                a.getAccountNumber(), a.getCustomerName(), a.getPhone(),
                a.getEmail(), a.getAccountType(),
                "₹ " + a.getBalance().toPlainString(),
                a.getCreatedAt()
            });
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { info("Please select an account to edit."); return; }
        String accNum = (String) tableModel.getValueAt(row, 0);
        Account acc = accountDAO.getAccountByNumber(accNum);
        if (acc == null) { info("Account not found."); return; }
        new EditAccountDialog(this, acc, accountDAO, () -> loadAccounts(null));
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { info("Please select an account to delete."); return; }
        String accNum = (String) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete account " + accNum + "? This will also delete all transactions.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (accountDAO.deleteAccount(accNum)) {
                JOptionPane.showMessageDialog(this, "Account deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAccounts(null);
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────

    private JPanel header(String text) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(new Color(25, 85, 165));
        p.setPreferredSize(new Dimension(900, 50));
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 16));
        l.setForeground(Color.WHITE);
        p.add(l);
        return p;
    }

    private JButton smallBtn(String t, Color bg, Color fg) {
        JButton b = new JButton(t);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(bg); b.setForeground(fg);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(140, 30));
        return b;
    }

    private void info(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    // ── Inner edit dialog ────────────────────────────────────────────

    static class EditAccountDialog extends JDialog {

        public EditAccountDialog(JFrame owner, Account acc, AccountDAO dao, Runnable onSave) {
            super(owner, "Edit Account – " + acc.getAccountNumber(), true);
            setSize(420, 380);
            setLocationRelativeTo(owner);

            JPanel p = new JPanel(new GridBagLayout());
            p.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));
            GridBagConstraints g = new GridBagConstraints();
            g.insets = new Insets(6, 5, 6, 5);
            g.fill   = GridBagConstraints.HORIZONTAL;

            JTextField nameF    = field(acc.getCustomerName());
            JTextField phoneF   = field(acc.getPhone() == null ? "" : acc.getPhone());
            JTextField emailF   = field(acc.getEmail() == null ? "" : acc.getEmail());
            JTextField addressF = field(acc.getAddress() == null ? "" : acc.getAddress());
            JComboBox<String> typeC = new JComboBox<>(new String[]{"Savings","Current","Fixed Deposit"});
            typeC.setSelectedItem(acc.getAccountType());

            row(p, g, 0, "Name *:",    nameF);
            row(p, g, 1, "Phone:",     phoneF);
            row(p, g, 2, "Email:",     emailF);
            row(p, g, 3, "Address:",   addressF);
            row(p, g, 4, "Type:",      typeC);

            JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
            JButton save = btn("Save", new Color(34, 139, 34));
            JButton cancel = btn("Cancel", new Color(150, 150, 155));
            btns.add(save); btns.add(cancel);

            setLayout(new BorderLayout());
            add(p,    BorderLayout.CENTER);
            add(btns, BorderLayout.SOUTH);

            save.addActionListener(e -> {
                if (nameF.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Name is required.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                acc.setCustomerName(nameF.getText().trim());
                acc.setPhone(phoneF.getText().trim());
                acc.setEmail(emailF.getText().trim());
                acc.setAddress(addressF.getText().trim());
                acc.setAccountType((String) typeC.getSelectedItem());
                if (dao.updateAccount(acc)) {
                    JOptionPane.showMessageDialog(this, "Account updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    onSave.run();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Update failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            cancel.addActionListener(e -> dispose());
            setVisible(true);
        }

        private JTextField field(String val) {
            JTextField tf = new JTextField(val);
            tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            tf.setPreferredSize(new Dimension(200, 27));
            return tf;
        }

        private void row(JPanel p, GridBagConstraints g, int row, String label, JComponent comp) {
            g.gridx = 0; g.gridy = row; g.weightx = 0;
            JLabel l = new JLabel(label);
            l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            p.add(l, g);
            g.gridx = 1; g.weightx = 1.0;
            p.add(comp, g);
        }

        private JButton btn(String t, Color bg) {
            JButton b = new JButton(t);
            b.setFont(new Font("Segoe UI", Font.BOLD, 12));
            b.setBackground(bg); b.setForeground(Color.WHITE);
            b.setFocusPainted(false); b.setBorderPainted(false);
            b.setPreferredSize(new Dimension(100, 30));
            return b;
        }
    }
}
