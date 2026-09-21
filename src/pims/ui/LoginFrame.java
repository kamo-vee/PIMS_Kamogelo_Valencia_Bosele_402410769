package pims.ui;

import pims.dao.UserDao;
import pims.model.User;
import pims.util.UITheme;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public LoginFrame() {
        setTitle("PIMS - Login");
        setSize(420, 340);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(UITheme.SECONDARY);

        // Title
        JLabel lblTitle = new JLabel("Pharmacy Information System", SwingConstants.CENTER);
        lblTitle.setFont(UITheme.TITLE_FONT);
        lblTitle.setForeground(UITheme.PRIMARY);
        lblTitle.setBounds(20, 20, 380, 30);
        add(lblTitle);

        // Username
        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(UITheme.LABEL_FONT);
        lblUser.setForeground(Color.BLACK);
        lblUser.setBounds(50, 90, 100, 25);
        add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setBounds(150, 90, 200, 28);
        txtUsername.setFont(UITheme.LABEL_FONT);
        add(txtUsername);

        // Password
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(UITheme.LABEL_FONT);
        lblPass.setForeground(Color.BLACK);
        lblPass.setBounds(50, 140, 100, 25);
        add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(150, 140, 200, 28);
        txtPassword.setFont(UITheme.LABEL_FONT);
        add(txtPassword);

        // Login button — FIXED so it's always visible
        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(150, 200, 200, 35);
        btnLogin.setFont(UITheme.BUTTON_FONT);

        // Force custom colors (needed on Windows)
        btnLogin.setOpaque(true);
        btnLogin.setContentAreaFilled(true);
        btnLogin.setBorderPainted(false);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Background + text
        btnLogin.setBackground(UITheme.PRIMARY);   // blue background
        btnLogin.setForeground(Color.WHITE);       // white text on blue

        add(btnLogin);

        btnLogin.addActionListener(e -> doLogin());
        getRootPane().setDefaultButton(btnLogin);
    }

    private void doLogin() {
        String u = txtUsername.getText().trim();
        String p = new String(txtPassword.getPassword()).trim();

        if (u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter username and password");
            return;
        }

        User user = new UserDao().authenticate(u, p);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Invalid credentials!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        dispose();
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            new AdminDashboard(user).setVisible(true);
        } else {
            new CashierDashboard(user).setVisible(true);
        }
    }
}