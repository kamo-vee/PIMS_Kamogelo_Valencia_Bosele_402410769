package pims;

import pims.db.DBConnection;
import pims.ui.LoginFrame;

import javax.swing.*;

/**
 * PIMS - Pharmacy Information Management System
 * Entry point of the application.
 */
public class Main {

    public static void main(String[] args) {

        // Step 1: Verify DB connection before launching UI
        if (DBConnection.getConnection() == null) {
            JOptionPane.showMessageDialog(null,
                    "Cannot connect to database!\n" +
                    "Please make sure MySQL is running and\n" +
                    "DBConnection credentials are correct.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        System.out.println("✅ Database connected.");

        // Step 2: Use system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }

        // Step 3: Launch login window on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}