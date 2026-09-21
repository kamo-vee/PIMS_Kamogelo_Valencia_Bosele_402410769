package pims.ui;

import pims.model.User;
import pims.util.UITheme;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    private final User user;

    public AdminDashboard(User user) {
        this.user = user;
        setTitle("PIMS - Admin Dashboard (" + user.getUsername() + ")");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Medicines", new ManageMedicinePanel());
        tabs.addTab("Suppliers", new ManageSupplierPanel());
        tabs.addTab("Users",     new ManageUsersPanel());
        tabs.addTab("POS",       new POSPanel(user));
        tabs.addTab("Reports",   new ReportsPanel());

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(UITheme.PRIMARY);

        JLabel lbl = new JLabel("  Admin Dashboard - " + user.getUsername());
        lbl.setForeground(Color.WHITE);
        lbl.setFont(UITheme.TITLE_FONT);
        top.add(lbl, BorderLayout.WEST);

        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.add(btnLogout);
        top.add(right, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }
}