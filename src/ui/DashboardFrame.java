package ui;

import dao.AccountDAO;
import dao.TransactionDAO;
import db.DBConnection;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

public class DashboardFrame extends JFrame {

    private final String username;
    private final String role;

    // Palette
    private static final Color NAVY      = new Color(15,  40,  90);
    private static final Color NAVY_MID  = new Color(24,  58, 120);
    private static final Color BG        = new Color(240, 243, 250);
    private static final Color TEXT_DARK = new Color(18,  24,  45);
    private static final Color TEXT_MID  = new Color(90, 105, 140);
    private static final Color FOOTER_BG = new Color(12,  32,  75);

    // Card circle background colors (match reference image)
    private static final Color CIRCLE_BLUE   = new Color(195, 215, 255);
    private static final Color CIRCLE_GREEN  = new Color(185, 235, 210);
    private static final Color CIRCLE_GOLD   = new Color(250, 225, 160);
    private static final Color CIRCLE_PINK   = new Color(255, 195, 195);
    private static final Color CIRCLE_PURPLE = new Color(215, 200, 255);
    private static final Color CIRCLE_LBLUE  = new Color(185, 220, 245);

    // Button/accent colors per card
    private static final Color BLUE_ACC   = new Color(30,  100, 220);
    private static final Color GREEN_ACC  = new Color(22,  155, 100);
    private static final Color GOLD_ACC   = new Color(185, 130,   0);
    private static final Color RED_ACC    = new Color(200,  60,  55);
    private static final Color PURPLE_ACC = new Color(110,  70, 200);
    private static final Color TEAL_ACC   = new Color(30,  130, 190);

    public DashboardFrame(String username, String role) {
        this.username = username;
        this.role     = role;
        initUI();
    }

    private void initUI() {
        setTitle("Mini Banking System - Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1100, 700));

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.add(buildHeader(),  BorderLayout.NORTH);
        root.add(buildScroll(),  BorderLayout.CENTER);
        root.add(buildFooter(),  BorderLayout.SOUTH);
        setContentPane(root);
        setVisible(true);
    }

    // =========================================================
    // HEADER
    // =========================================================
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, NAVY, getWidth(), 0, new Color(20, 52, 110)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setPreferredSize(new Dimension(0, 80));
        header.setBorder(BorderFactory.createEmptyBorder(0, 32, 0, 32));

        // LEFT: bank icon + title
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        left.setOpaque(false);

        // Drawn bank building icon
        JPanel bankIcon = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                // Base
                g2.fillRect(4, 26, 36, 14);
                // Columns
                g2.fillRect(5,  14, 5, 14);
                g2.fillRect(13, 14, 5, 14);
                g2.fillRect(21, 14, 5, 14);
                g2.fillRect(29, 14, 5, 14);
                // Roof triangle
                int[] rx = {2, 22, 42}; int[] ry = {14, 4, 14};
                g2.fillPolygon(rx, ry, 3);
                // Bottom bar
                g2.fillRect(2, 40, 40, 4);
                g2.dispose();
            }
        };
        bankIcon.setOpaque(false);
        bankIcon.setPreferredSize(new Dimension(44, 48));

        JPanel titlesBox = new JPanel(new GridLayout(2, 1, 0, 3));
        titlesBox.setOpaque(false);
        JLabel title = new JLabel("Mini Banking System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 21));
        title.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Manage your accounts and transactions easily");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(170, 195, 240));
        titlesBox.add(title); titlesBox.add(sub);
        left.add(bankIcon); left.add(titlesBox);

        // RIGHT: avatar + user info + logout
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        right.setOpaque(false);

        // Avatar with drawn person icon
        JPanel avatarBox = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Circle bg
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillOval(0, 0, 46, 46);
                g2.setColor(new Color(255, 255, 255, 60));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(0, 0, 46, 46);
                // Person head
                g2.setColor(new Color(190, 205, 230));
                g2.fillOval(15, 7, 16, 16);
                // Person body
                g2.fillArc(7, 26, 32, 22, 0, 180);
                // Suit collar (dark)
                g2.setColor(new Color(50, 70, 110));
                g2.fillRect(20, 28, 6, 10);
                g2.dispose();
            }
        };
        avatarBox.setOpaque(false);
        avatarBox.setPreferredSize(new Dimension(48, 48));

        JPanel userBox = new JPanel(new GridLayout(2, 1, 0, 2));
        userBox.setOpaque(false);
        JLabel welcomeLbl = new JLabel("Welcome, " + username);
        welcomeLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        welcomeLbl.setForeground(Color.WHITE);
        JLabel roleLbl = new JLabel("Role: " + role);
        roleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        roleLbl.setForeground(new Color(170, 195, 235));
        userBox.add(welcomeLbl); userBox.add(roleLbl);

        // Vertical separator
        JPanel vLine = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(255,255,255,55));
                g.drawLine(0, 6, 0, getHeight()-6);
            }
        };
        vLine.setOpaque(false);
        vLine.setPreferredSize(new Dimension(1, 40));

        JButton logoutBtn = buildLogoutBtn();
        right.add(avatarBox);
        right.add(userBox);
        right.add(vLine);
        right.add(logoutBtn);

        header.add(vcenter(left),  BorderLayout.WEST);
        header.add(vcenter(right), BorderLayout.EAST);
        return header;
    }

    private JButton buildLogoutBtn() {
        JButton b = new JButton() {
            private boolean hov = false;
            { addMouseListener(new MouseAdapter(){
                public void mouseEntered(MouseEvent e){hov=true;repaint();}
                public void mouseExited(MouseEvent e){hov=false;repaint();}
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (hov) { g2.setColor(new Color(255,255,255,25)); g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8); }
                // Draw logout arrow icon
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // Door rectangle
                g2.drawRoundRect(6, 6, 18, 20, 3, 3);
                // Arrow pointing right
                g2.drawLine(18, 16, 28, 16);
                g2.drawLine(24, 12, 28, 16);
                g2.drawLine(24, 20, 28, 16);
                // Text
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2.drawString("Logout", 34, 19);
                g2.dispose();
            }
        };
        b.setContentAreaFilled(false); b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(new Color(255,255,255,80), 8, 1),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        b.setPreferredSize(new Dimension(105, 38));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> logout());
        return b;
    }

    // =========================================================
    // SCROLL + CENTER
    // =========================================================
    private JScrollPane buildScroll() {
        JPanel center = new JPanel(new BorderLayout(0, 24));
        center.setBackground(BG);
        center.setBorder(BorderFactory.createEmptyBorder(32, 44, 28, 44));
        center.add(buildCardGrid(), BorderLayout.CENTER);
        center.add(buildStatsBar(), BorderLayout.SOUTH);

        JScrollPane sp = new JScrollPane(center);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setBackground(BG);
        return sp;
    }

    // =========================================================
    // CARD GRID
    // =========================================================
    private JPanel buildCardGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 3, 22, 22));
        grid.setOpaque(false);

        grid.add(buildCard("Create Account",      "Create a new bank account\nfor a customer",     CIRCLE_BLUE,   BLUE_ACC,   0));
        grid.add(buildCard("View Accounts",        "View and manage all\ncustomer accounts",         CIRCLE_GREEN,  GREEN_ACC,  1));
        grid.add(buildCard("Deposit",              "Deposit money into\nan account",                 CIRCLE_GOLD,   GOLD_ACC,   2));
        grid.add(buildCard("Withdraw",             "Withdraw money from\nan account",                CIRCLE_PINK,   RED_ACC,    3));
        grid.add(buildCard("Check Balance",        "Check the balance of\nan account",               CIRCLE_PURPLE, PURPLE_ACC, 4));
        grid.add(buildCard("Transaction History",  "View all transactions of\nan account",           CIRCLE_LBLUE,  TEAL_ACC,   5));
        return grid;
    }

    private JPanel buildCard(String title, String desc, Color circleColor, Color accentColor, int iconType) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                for (int s = 6; s >= 1; s--) {
                    g2.setColor(new Color(0,0,0,3));
                    g2.fillRoundRect(s, s, getWidth()-s+2, getHeight()-s+2, 18, 18);
                }
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth()-5, getHeight()-5, 18, 18);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(28, 28, 24, 28));

        // Icon circle
        JPanel iconCircle = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(circleColor);
                g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
                drawIcon(g2, iconType, accentColor, getWidth(), getHeight());
                g2.dispose();
            }
        };
        iconCircle.setOpaque(false);
        iconCircle.setPreferredSize(new Dimension(70, 70));

        JPanel iconWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        iconWrap.setOpaque(false);
        iconWrap.add(iconCircle);

        // Text block
        JPanel textBlock = new JPanel();
        textBlock.setLayout(new BoxLayout(textBlock, BoxLayout.Y_AXIS));
        textBlock.setOpaque(false);
        textBlock.setBorder(BorderFactory.createEmptyBorder(16, 0, 14, 0));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titleLbl.setForeground(TEXT_DARK);
        titleLbl.setAlignmentX(LEFT_ALIGNMENT);
        textBlock.add(titleLbl);
        textBlock.add(Box.createVerticalStrut(6));
        for (String line : desc.split("\n")) {
            JLabel dl = new JLabel(line);
            dl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            dl.setForeground(TEXT_MID);
            dl.setAlignmentX(LEFT_ALIGNMENT);
            textBlock.add(dl);
        }

        // Open button
        JButton openBtn = buildOpenBtn(accentColor);
        openBtn.addActionListener(e -> handleNav(title));
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnRow.setOpaque(false);
        btnRow.add(openBtn);

        card.add(iconWrap,  BorderLayout.NORTH);
        card.add(textBlock, BorderLayout.CENTER);
        card.add(btnRow,    BorderLayout.SOUTH);

        // Hover border
        Border plain = BorderFactory.createEmptyBorder(28, 28, 24, 28);
        Border hover = BorderFactory.createCompoundBorder(
            new RoundedBorder(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 100), 18, 1),
            BorderFactory.createEmptyBorder(27, 27, 23, 27));
        card.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e){ card.setBorder(hover); card.repaint(); }
            public void mouseExited(MouseEvent e){ card.setBorder(plain); card.repaint(); }
        });
        return card;
    }

    // =========================================================
    // ICON DRAWING — matches reference image exactly
    // =========================================================
    private void drawIcon(Graphics2D g2, int type, Color accent, int w, int h) {
        int cx = w / 2, cy = h / 2;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        switch (type) {
            case 0 -> drawCreateAccountIcon(g2, cx, cy, accent);
            case 1 -> drawViewAccountsIcon(g2, cx, cy, accent);
            case 2 -> drawDepositIcon(g2, cx, cy, accent);
            case 3 -> drawWithdrawIcon(g2, cx, cy, accent);
            case 4 -> drawCheckBalanceIcon(g2, cx, cy, accent);
            case 5 -> drawTransactionHistoryIcon(g2, cx, cy, accent);
        }
    }

    /** Person silhouette + plus sign (blue) */
    private void drawCreateAccountIcon(Graphics2D g2, int cx, int cy, Color accent) {
        g2.setColor(accent);
        // Head
        g2.fillOval(cx - 10, cy - 18, 14, 14);
        // Body
        g2.fillArc(cx - 16, cy - 4, 22, 18, 0, 180);
        // Plus sign
        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(cx + 8, cy - 14, cx + 8, cy - 4);
        g2.drawLine(cx + 3, cy - 9,  cx + 13, cy - 9);
    }

    /** Three people silhouettes (green) */
    private void drawViewAccountsIcon(Graphics2D g2, int cx, int cy, Color accent) {
        g2.setColor(accent);
        // Back-left person (smaller, darker)
        g2.setColor(accent.darker());
        g2.fillOval(cx - 22, cy - 16, 11, 11);
        g2.fillArc(cx - 27, cy - 5, 18, 14, 0, 180);
        // Back-right person (smaller, darker)
        g2.fillOval(cx + 11, cy - 16, 11, 11);
        g2.fillArc(cx + 9,   cy - 5, 18, 14, 0, 180);
        // Front center person (brighter)
        g2.setColor(accent);
        g2.fillOval(cx - 8, cy - 18, 14, 14);
        g2.fillArc(cx - 14, cy - 5, 26, 18, 0, 180);
    }

    /** Banknote with dollar sign (gold/green on gold circle) */
    private void drawDepositIcon(Graphics2D g2, int cx, int cy, Color accent) {
        // Note background
        g2.setColor(new Color(50, 160, 70));
        g2.fillRoundRect(cx - 20, cy - 10, 40, 22, 6, 6);
        // Note border (lighter)
        g2.setColor(new Color(80, 190, 100));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(cx - 20, cy - 10, 40, 22, 6, 6);
        // Dollar circle
        g2.setColor(new Color(180, 230, 80));
        g2.fillOval(cx - 8, cy - 8, 16, 16);
        // Dollar sign
        g2.setColor(new Color(40, 130, 50));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString("$", cx - fm.stringWidth("$")/2, cy + fm.getAscent()/2 - 1);
        // Small circles each side
        g2.setColor(new Color(80, 190, 100));
        g2.fillOval(cx - 19, cy - 4, 8, 8);
        g2.fillOval(cx + 11, cy - 4, 8, 8);
    }

    /** Hand receiving money (red/pink) */
    private void drawWithdrawIcon(Graphics2D g2, int cx, int cy, Color accent) {
        g2.setColor(accent);
        // Banknote
        g2.fillRoundRect(cx - 16, cy - 18, 30, 16, 4, 4);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
        g2.drawString("$", cx - 3, cy - 7);
        // Hand / palm
        g2.setColor(accent);
        g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        // Palm base
        g2.fillRoundRect(cx - 14, cy, 26, 12, 6, 6);
        // Fingers
        g2.fillRoundRect(cx - 14, cy - 6, 5, 10, 3, 3);
        g2.fillRoundRect(cx - 7,  cy - 8, 5, 12, 3, 3);
        g2.fillRoundRect(cx,      cy - 8, 5, 12, 3, 3);
        g2.fillRoundRect(cx + 7,  cy - 6, 5, 10, 3, 3);
        // Arrow down
        g2.setColor(accent.darker());
        g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(cx + 14, cy - 20, cx + 14, cy - 10);
        g2.drawLine(cx + 10, cy - 14, cx + 14, cy - 10);
        g2.drawLine(cx + 18, cy - 14, cx + 14, cy - 10);
    }

    /** Wallet (purple) */
    private void drawCheckBalanceIcon(Graphics2D g2, int cx, int cy, Color accent) {
        g2.setColor(accent);
        // Wallet body
        g2.fillRoundRect(cx - 18, cy - 10, 36, 24, 6, 6);
        // Wallet flap (top)
        g2.fillRoundRect(cx - 18, cy - 14, 36, 12, 6, 6);
        // Coin pocket
        g2.setColor(accent.brighter());
        g2.fillRoundRect(cx + 4, cy - 4, 12, 14, 8, 8);
        // Coin dot
        g2.setColor(new Color(255, 215, 80));
        g2.fillOval(cx + 7, cy + 1, 6, 6);
        // Card lines
        g2.setColor(new Color(255,255,255,120));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(cx - 14, cy - 2, cx,      cy - 2);
        g2.drawLine(cx - 14, cy + 3, cx - 4,  cy + 3);
    }

    /** Document with clock/history lines (teal/blue) */
    private void drawTransactionHistoryIcon(Graphics2D g2, int cx, int cy, Color accent) {
        g2.setColor(accent);
        // Document body
        g2.fillRoundRect(cx - 16, cy - 20, 26, 32, 5, 5);
        // Folded corner
        g2.setColor(new Color(255,255,255,60));
        g2.fillPolygon(new int[]{cx+4, cx+10, cx+10}, new int[]{cy-20, cy-20, cy-14}, 3);
        // Document lines (white)
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(cx - 12, cy - 10, cx + 4,  cy - 10);
        g2.drawLine(cx - 12, cy - 4,  cx + 4,  cy - 4);
        g2.drawLine(cx - 12, cy + 2,  cx,      cy + 2);
        // Clock circle overlay (bottom right)
        g2.setColor(new Color(255, 200, 50));
        g2.fillOval(cx + 4, cy + 4, 16, 16);
        g2.setColor(accent.darker());
        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(cx + 12, cy + 8,  cx + 12, cy + 12);
        g2.drawLine(cx + 12, cy + 12, cx + 16, cy + 14);
    }

    // =========================================================
    // OPEN BUTTON
    // =========================================================
    private JButton buildOpenBtn(Color color) {
        JButton b = new JButton("Open  \u2192") {
            private boolean hov = false;
            { addMouseListener(new MouseAdapter(){
                public void mouseEntered(MouseEvent e){hov=true;repaint();}
                public void mouseExited(MouseEvent e){hov=false;repaint();}
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (hov) {
                    g2.setColor(color);
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                    g2.setColor(Color.WHITE);
                } else {
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                    g2.setColor(color);
                }
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2,
                              (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setContentAreaFilled(false); b.setFocusPainted(false);
        b.setBorder(new RoundedBorder(color, 8, 1));
        b.setPreferredSize(new Dimension(105, 34));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // =========================================================
    // STATS BAR
    // =========================================================
    private JPanel buildStatsBar() {
        String accCount = "0", totalBal = "0", txCount = "0";
        try {
            AccountDAO aDAO = new AccountDAO();
            List<model.Account> all = aDAO.getAllAccounts();
            accCount = String.valueOf(all.size());
            BigDecimal sum = all.stream().map(model.Account::getBalance)
                               .reduce(BigDecimal.ZERO, BigDecimal::add);
            totalBal = String.format("%,.2f", sum);
            TransactionDAO tDAO = new TransactionDAO();
        } catch (Exception ignored) {}
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));

        JPanel card = new JPanel(new GridLayout(1, 4, 0, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                for (int s=4;s>=1;s--){g2.setColor(new Color(0,0,0,4));g2.fillRoundRect(s,s,getWidth()-s,getHeight()-s,14,14);}
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0,0,getWidth()-3,getHeight()-3,14,14);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(18, 8, 18, 8));
        card.setPreferredSize(new Dimension(0, 96));

        // Header "Quick Overview"
        JPanel outer = new JPanel(new BorderLayout(0, 10));
        outer.setOpaque(false);

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        titleRow.setOpaque(false);
        // Bar chart icon (drawn)
        JPanel chartIcon = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setColor(BLUE_ACC);
                g2.fillRect(0, 10, 5, 6);
                g2.fillRect(7, 5,  5, 11);
                g2.fillRect(14,8,  5, 8);
                g2.dispose();
            }
        };
        chartIcon.setOpaque(false);
        chartIcon.setPreferredSize(new Dimension(20, 16));
        JLabel overviewLbl = new JLabel("Quick Overview");
        overviewLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        overviewLbl.setForeground(TEXT_DARK);
        titleRow.add(chartIcon); titleRow.add(overviewLbl);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(215, 225, 242));

        card.add(statCell(buildPeopleIcon(), "Total Accounts",    accCount,            new Color(30,100,220)));
        card.add(statCell(buildMoneyIcon(),  "Total Balance",     "\u20B9 " + totalBal, new Color(22,155,100)));
        card.add(statCell(buildArrowIcon(),  "Total Transactions", txCount,             new Color(220,120,0)));
        card.add(statCell(buildCalIcon(),    "Today's Date",      today,                new Color(110,70,200)));

        JPanel wrapper = new JPanel(new BorderLayout(0, 8));
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        wrapper.add(titleRow, BorderLayout.NORTH);
        wrapper.add(sep,      BorderLayout.CENTER);
        wrapper.add(card,     BorderLayout.SOUTH);
        return wrapper;
    }

    private JPanel statCell(JPanel icon, String label, String value, Color accent) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220,228,242)));

        JPanel textBox = new JPanel(new GridLayout(2, 1, 0, 3));
        textBox.setOpaque(false);
        JLabel valLbl = new JLabel(value);
        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 17));
        valLbl.setForeground(TEXT_DARK);
        JLabel lblLbl = new JLabel(label);
        lblLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblLbl.setForeground(TEXT_MID);
        textBox.add(valLbl); textBox.add(lblLbl);
        p.add(icon); p.add(textBox);
        return p;
    }

    // Small stat icons
    private JPanel buildPeopleIcon() { return miniIcon(g2 -> {
        g2.setColor(BLUE_ACC);
        g2.fillOval(2,0,10,10); g2.fillArc(0,10,14,10,0,180);
        g2.setColor(new Color(100,145,220));
        g2.fillOval(13,2,8,8); g2.fillArc(11,10,12,9,0,180);
    }); }

    private JPanel buildMoneyIcon() { return miniIcon(g2 -> {
        g2.setColor(new Color(22,155,100));
        g2.fillRoundRect(0,3,28,18,5,5);
        g2.setColor(new Color(180,230,80));
        g2.fillOval(9,5,10,10);
        g2.setColor(new Color(22,155,100));
        g2.setFont(new Font("Segoe UI",Font.BOLD,8));
        g2.drawString("$",12,13);
    }); }

    private JPanel buildArrowIcon() { return miniIcon(g2 -> {
        g2.setColor(new Color(220,120,0));
        g2.setStroke(new BasicStroke(2.5f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        g2.drawLine(4,5,18,5); g2.drawLine(14,1,18,5); g2.drawLine(14,9,18,5);
        g2.setColor(new Color(180,80,0));
        g2.drawLine(4,15,18,15); g2.drawLine(8,11,4,15); g2.drawLine(8,19,4,15);
    }); }

    private JPanel buildCalIcon() { return miniIcon(g2 -> {
        g2.setColor(new Color(110,70,200));
        g2.fillRoundRect(0,3,24,20,4,4);
        g2.setColor(Color.WHITE);
        g2.fillRect(1,8,22,14);
        g2.setColor(new Color(110,70,200));
        g2.setFont(new Font("Segoe UI",Font.BOLD,7));
        g2.drawString("31",7,18);
        g2.fillRect(5,1,3,6); g2.fillRect(16,1,3,6);
    }); }

    @FunctionalInterface interface Painter { void paint(Graphics2D g2); }
    private JPanel miniIcon(Painter p) {
        JPanel icon = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                p.paint(g2); g2.dispose();
            }
        };
        icon.setOpaque(false);
        icon.setPreferredSize(new Dimension(30, 24));
        return icon;
    }

    // =========================================================
    // FOOTER
    // =========================================================
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(FOOTER_BG); g.fillRect(0,0,getWidth(),getHeight());
            }
        };
        footer.setPreferredSize(new Dimension(0, 54));
        footer.setBorder(BorderFactory.createEmptyBorder(0,36,0,36));

        // Left: shield icon + security text
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);
        JPanel shieldIcon = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(100,160,240));
                int[] sx = {10,20,20,10,5,5}; int[] sy = {2,2,14,20,14,2};
                g2.fillPolygon(sx,sy,6);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
                g2.drawLine(8,11,11,14); g2.drawLine(11,14,17,8);
                g2.dispose();
            }
        };
        shieldIcon.setOpaque(false); shieldIcon.setPreferredSize(new Dimension(24,22));

        JPanel secBox = new JPanel(new GridLayout(2,1,0,1)); secBox.setOpaque(false);
        JLabel s1 = new JLabel("Your security is our priority.");
        s1.setFont(new Font("Segoe UI",Font.BOLD,11)); s1.setForeground(new Color(200,215,245));
        JLabel s2 = new JLabel("All transactions are safe and encrypted.");
        s2.setFont(new Font("Segoe UI",Font.PLAIN,10)); s2.setForeground(new Color(135,158,208));
        secBox.add(s1); secBox.add(s2);
        left.add(shieldIcon); left.add(secBox);

        // Right: app name + copyright
        JPanel right = new JPanel(new GridLayout(2,1,0,1)); right.setOpaque(false);
        JLabel appName = new JLabel("Mini Banking System");
        appName.setFont(new Font("Segoe UI",Font.BOLD,11));
        appName.setForeground(new Color(200,215,245));
        appName.setHorizontalAlignment(SwingConstants.RIGHT);
        JLabel copy = new JLabel("\u00A9 2025 All Rights Reserved");
        copy.setFont(new Font("Segoe UI",Font.PLAIN,10));
        copy.setForeground(new Color(130,150,200));
        copy.setHorizontalAlignment(SwingConstants.RIGHT);
        right.add(appName); right.add(copy);

        footer.add(vcenter(left),  BorderLayout.WEST);
        footer.add(vcenter(right), BorderLayout.EAST);
        return footer;
    }

    // =========================================================
    // NAVIGATION
    // =========================================================
    private void handleNav(String label) {
        SwingUtilities.invokeLater(() -> {
            if      (label.contains("Create Account"))      new CreateAccountFrame(this);
            else if (label.contains("View Accounts"))       new ViewAccountsFrame(this);
            else if (label.contains("Deposit"))             new DepositFrame(this);
            else if (label.contains("Withdraw"))            new WithdrawFrame(this);
            else if (label.contains("Check Balance"))       new BalanceFrame(this);
            else if (label.contains("Transaction History")) new TransactionHistoryFrame(this);
        });
    }

    private void logout() {
        int c = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?","Confirm Logout",JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            DBConnection.closeConnection(); dispose(); new LoginFrame();
        }
    }

    // =========================================================
    // UTILITIES
    // =========================================================
    private JPanel vcenter(JPanel p) {
        JPanel w = new JPanel(new GridBagLayout()); w.setOpaque(false); w.add(p); return w;
    }

    static class RoundedBorder extends AbstractBorder {
        private final Color color; private final int radius, thickness;
        RoundedBorder(Color c, int r, int t){ color=c; radius=r; thickness=t; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color); g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x,y,w-1,h-1,radius,radius); g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c){ int t=thickness+2; return new Insets(t,t,t,t); }
    }
}