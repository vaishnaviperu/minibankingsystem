import javax.swing.*;
import ui.LoginFrame;

/**
 * Main - Entry point.
 * Uses Cross-Platform (Metal) L&F so colors are NEVER overridden by macOS.
 * This fixes white-text-on-white-background issues on Mac.
 */
public class Main {
    public static void main(String[] args) {
        try {
            // Cross-platform L&F = consistent colors on ALL operating systems
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(LoginFrame::new);
    }
}