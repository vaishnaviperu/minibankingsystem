package ui;

import dao.UserDAO;
import db.DBConnection;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import javax.swing.*;

/**
 * LoginFrame - Fixed version.
 * Forces text colors explicitly so macOS system L&F cannot override them.
 * Includes a DB connection test on startup to catch config issues early.
 */
public class LoginFrame extends JFrame {

    private JTextField     usernameField;
    private JPasswordField passwordField;
    private final UserDAO  userDAO = new UserDAO();

    // Colors
    private static final Color NAVY       = new Color(15,  40,  90);
    private static final Color NAVY_MID   = new Color(24,  58, 120);
    private static final Color BG         = new Color(245, 248, 255);
    private static final Color TEXT_DARK  = new Color(18,  24,  45);   // near-black
    private static final Color TEXT_MID   = new Color(80,  95, 130);
    private static final Color BORDER_CLR = new Color(180, 195, 220);
    private static final Color BLUE_BTN   = new Color(30, 100, 220);

    public LoginFrame() {
        // Force cross-platform L&F so colors are never overridden by macOS
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}

        initUI();
        checkDBConnection(); // warn if DB is unreachable
    }

    private void initUI() {
        setTitle("Mini Banking System – Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(440, 420);
        setLocationRelativeTo(null);
        setResizable(false);

        // ── Root ─────────────────────────────────────────────────────
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        // ── Header ───────────────────────────────────────────────────
        JPanel header = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, NAVY, getWidth(), 0, NAVY_MID));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setPreferredSize(new Dimension(440, 100));

        JPanel headerText = new JPanel(new GridLayout(3, 1, 0, 4));
        headerText.setOpaque(false);

        JLabel bankIcon = new JLabel("🏦", SwingConstants.CENTER);
        bankIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));

        JLabel titleLbl = new JLabel("Mini Banking System", SwingConstants.CENTER);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLbl.setForeground(Color.WHITE);

        JLabel subLbl = new JLabel("Secure • Fast • Reliable", SwingConstants.CENTER);
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLbl.setForeground(new Color(175, 200, 240));

        headerText.add(bankIcon);
        headerText.add(titleLbl);
        headerText.add(subLbl);
        header.add(headerText);

        // ── Card panel ────────────────────────────────────────────────
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // shadow
                for (int s = 5; s >= 1; s--) {
                    g2.setColor(new Color(0, 0, 0, 4));
                    g2.fillRoundRect(s, s, getWidth() - s, getHeight() - s, 16, 16);
                }
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);

        // ── Form inside card ──────────────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(30, 36, 20, 36));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 4, 8, 4);
        g.fill   = GridBagConstraints.HORIZONTAL;

        // ── "Sign In" heading ─────────────────────────────────────────
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        JLabel signIn = new JLabel("Sign In");
        signIn.setFont(new Font("Segoe UI", Font.BOLD, 22));
        signIn.setForeground(TEXT_DARK);          // ← explicit dark color
        form.add(signIn, g);

        g.gridy = 1;
        JLabel hint = new JLabel("Enter your credentials to continue");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        hint.setForeground(TEXT_MID);             // ← explicit mid color
        form.add(hint, g);

        // Spacer
        g.gridy = 2;
        form.add(Box.createVerticalStrut(6), g);

        g.gridwidth = 1;

        // Username label
        g.gridx = 0; g.gridy = 3; g.weightx = 0;
        form.add(fieldLabel("Username"), g);

        // Username field
        g.gridx = 0; g.gridy = 4; g.weightx = 1; g.gridwidth = 2;
        usernameField = buildTextField(false);
        form.add(usernameField, g);

        // Password label
        g.gridx = 0; g.gridy = 5; g.weightx = 0; g.gridwidth = 1;
        form.add(fieldLabel("Password"), g);

        // Password field
        g.gridx = 0; g.gridy = 6; g.weightx = 1; g.gridwidth = 2;
        passwordField = (JPasswordField) buildTextField(true);
        form.add(passwordField, g);

        // Login button
        g.gridx = 0; g.gridy = 7; g.gridwidth = 2;
        g.insets = new Insets(18, 4, 6, 4);
        JButton loginBtn = buildLoginButton();
        form.add(loginBtn, g);

        // Default admin hint
        g.gridy = 8;
        g.insets = new Insets(4, 4, 4, 4);
        JLabel hintLbl = new JLabel("Default: admin / admin123", SwingConstants.CENTER);
        hintLbl.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hintLbl.setForeground(new Color(150, 165, 195));
        form.add(hintLbl, g);

        card.add(form);

        // ── Outer wrapper with padding ────────────────────────────────
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(BG);
        center.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        center.add(card);

        root.add(header, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        setContentPane(root);

        // ── Listeners ────────────────────────────────────────────────
        loginBtn.addActionListener(e -> doLogin());
        passwordField.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin();
            }
        });
        usernameField.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) passwordField.requestFocus();
            }
        });

        setVisible(true);
        usernameField.requestFocus();
    }

    // ── Login logic ───────────────────────────────────────────────────

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter both username and password.",
                "Missing Fields", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String role = userDAO.validateLogin(username, password);
        if (role != null) {
            dispose();
            new DashboardFrame(username, role);
        } else {
            JOptionPane.showMessageDialog(this,
                "Invalid username or password.\n\nDefault login: admin / admin123",
                "Login Failed", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }

    // ── DB check on startup ───────────────────────────────────────────

    private void checkDBConnection() {
        SwingUtilities.invokeLater(() -> {
            try {
                Connection conn = DBConnection.getConnection();
                if (conn == null || conn.isClosed()) {
                    showDBError();
                }
            } catch (Exception e) {
                showDBError();
            }
        });
    }

    private void showDBError() {
        JOptionPane.showMessageDialog(this,
            "⚠️  Cannot connect to MySQL database!\n\n" +
            "Please check:\n" +
            "1. MySQL is running:  brew services start mysql\n" +
            "2. Credentials in DBConnection.java are correct\n" +
            "   (DB_USER = root, DB_PASS = root123)\n" +
            "3. Database exists:  run minibankdb.sql first\n\n" +
            "Without a DB connection, login will fail.",
            "Database Connection Error", JOptionPane.ERROR_MESSAGE);
    }

    // ── UI builders ───────────────────────────────────────────────────

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(TEXT_MID);        // ← explicit color, never white
        return l;
    }

    private JTextField buildTextField(boolean isPassword) {
        JTextField tf = isPassword ? new JPasswordField() : new JTextField();

        // Force foreground + background explicitly
        tf.setForeground(TEXT_DARK);      // ← BLACK text
        tf.setBackground(Color.WHITE);    // ← WHITE background
        tf.setCaretColor(TEXT_DARK);      // ← visible caret
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setPreferredSize(new Dimension(260, 38));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        tf.setOpaque(true);

        // Focus highlight
        tf.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BLUE_BTN, 2),
                    BorderFactory.createEmptyBorder(5, 9, 5, 9)
                ));
            }
            @Override public void focusLost(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_CLR, 1),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)
                ));
            }
        });
        return tf;
    }

    private JButton buildLoginButton() {
        JButton b = new JButton("Login") {
            private boolean hovered = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? new Color(20, 80, 190) : BLUE_BTN);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(260, 42));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}