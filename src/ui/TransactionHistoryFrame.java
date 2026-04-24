package ui;

import dao.TransactionDAO;
import model.Transaction;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * TransactionHistoryFrame - Displays full transaction history for an account.
 */
public class TransactionHistoryFrame extends JFrame {

    private JTextField       accField;
    private JTable           table;
    private DefaultTableModel tableModel;
    private JLabel           summaryLabel;

    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final JFrame         parent;

    private static final String[] COLUMNS = {
        "Txn ID", "Account No", "Type", "Amount (₹)", "Date & Time", "Balance After (₹)"
    };

    public TransactionHistoryFrame(JFrame parent) {
        this.parent = parent;
        initUI();
    }

    private void initUI() {
        setTitle("Transaction History");
        setSize(820, 520);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(new Color(245, 244, 255));
        mainPanel.add(header("📋  Transaction History"), BorderLayout.NORTH);

        // Search row
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        searchPanel.setBackground(new Color(235, 233, 252));
        searchPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 195, 230)));
        JLabel sl = new JLabel("Account Number:");
        sl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        accField = new JTextField(15);
        accField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JButton viewBtn  = smallBtn("View History", new Color(90, 60, 160), Color.WHITE);
        JButton clearBtn = smallBtn("Clear",        new Color(150, 150, 155), Color.WHITE);
        summaryLabel = new JLabel("");
        summaryLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        summaryLabel.setForeground(new Color(80, 80, 100));
        searchPanel.add(sl); searchPanel.add(accField);
        searchPanel.add(viewBtn); searchPanel.add(clearBtn);
        searchPanel.add(summaryLabel);
        mainPanel.add(searchPanel, BorderLayout.BEFORE_FIRST_LINE);

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(90, 60, 160));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(220, 215, 255));
        table.setGridColor(new Color(220, 215, 235));

        // Color-code DEPOSIT vs WITHDRAWAL rows
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                           boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) {
                    String type = (String) tableModel.getValueAt(row, 2);
                    setBackground("DEPOSIT".equals(type)
                            ? new Color(240, 255, 240)
                            : new Color(255, 243, 240));
                }
                return this;
            }
        });

        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btmPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        btmPanel.setBackground(new Color(245, 244, 255));
        JButton closeBtn = smallBtn("Close", new Color(110, 110, 120), Color.WHITE);
        btmPanel.add(closeBtn);
        mainPanel.add(btmPanel, BorderLayout.SOUTH);
        add(mainPanel);

        viewBtn.addActionListener(e  -> loadHistory());
        clearBtn.addActionListener(e -> { accField.setText(""); tableModel.setRowCount(0); summaryLabel.setText(""); });
        closeBtn.addActionListener(e -> dispose());
        accField.addActionListener(e -> loadHistory());

        setVisible(true);
    }

    private void loadHistory() {
        String accNum = accField.getText().trim();
        if (accNum.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter an account number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        tableModel.setRowCount(0);
        List<Transaction> list = transactionDAO.getTransactionsByAccount(accNum);

        if (list.isEmpty()) {
            summaryLabel.setText("No transactions found for: " + accNum);
            return;
        }

        for (Transaction tx : list) {
            tableModel.addRow(new Object[]{
                tx.getTransactionId(),
                tx.getAccountNumber(),
                tx.getTransactionType(),
                "₹ " + tx.getAmount().toPlainString(),
                tx.getTransactionDate(),
                "₹ " + tx.getBalanceAfter().toPlainString()
            });
        }
        summaryLabel.setText("Showing " + list.size() + " transaction(s) for account: " + accNum);
    }

    // ── Helpers ──────────────────────────────────────────────────────

    private JPanel header(String text) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(new Color(80, 50, 155));
        p.setPreferredSize(new Dimension(820, 50));
        JLabel l = new JLabel(text); l.setFont(new Font("Segoe UI", Font.BOLD, 16)); l.setForeground(Color.WHITE);
        p.add(l); return p;
    }

    private JButton smallBtn(String t, Color bg, Color fg) {
        JButton b = new JButton(t);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(bg); b.setForeground(fg);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(115, 30));
        return b;
    }
}
