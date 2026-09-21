package pims.ui;

import pims.dao.UserDao;
import pims.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ManageUsersPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtUsername, txtPassword;
    private JComboBox<String> cmbRole;
    private int selectedId = -1;
    private final UserDao dao = new UserDao();

    public ManageUsersPanel() {
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("User Details"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0; form.add(new JLabel("Username:"), g);
        txtUsername = new JTextField(15);
        g.gridx = 1; form.add(txtUsername, g);

        g.gridx = 0; g.gridy = 1; form.add(new JLabel("Password:"), g);
        txtPassword = new JTextField(15);
        g.gridx = 1; form.add(txtPassword, g);

        g.gridx = 0; g.gridy = 2; form.add(new JLabel("Role:"), g);
        cmbRole = new JComboBox<>(new String[]{"ADMIN", "CASHIER"});
        g.gridx = 1; form.add(cmbRole, g);

        JPanel buttons = new JPanel();
        JButton btnAdd    = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear  = new JButton("Clear");
        buttons.add(btnAdd);
        buttons.add(btnUpdate);
        buttons.add(btnDelete);
        buttons.add(btnClear);

        g.gridx = 0; g.gridy = 3; g.gridwidth = 2;
        form.add(buttons, g);
        add(form, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new Object[]{"ID", "Username", "Password", "Role"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        btnAdd.addActionListener(e -> {
            dao.addUser(new User(0, txtUsername.getText(), txtPassword.getText(),
                    cmbRole.getSelectedItem().toString()));
            loadData(); clearForm();
        });
        btnUpdate.addActionListener(e -> {
            if (selectedId == -1) return;
            dao.updateUser(new User(selectedId, txtUsername.getText(), txtPassword.getText(),
                    cmbRole.getSelectedItem().toString()));
            loadData(); clearForm();
        });
        btnDelete.addActionListener(e -> {
            if (selectedId == -1) return;
            if (JOptionPane.showConfirmDialog(this, "Delete user?") == JOptionPane.YES_OPTION) {
                dao.deleteUser(selectedId);
                loadData(); clearForm();
            }
        });
        btnClear.addActionListener(e -> clearForm());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int r = table.getSelectedRow();
                selectedId = Integer.parseInt(model.getValueAt(r, 0).toString());
                txtUsername.setText(model.getValueAt(r, 1).toString());
                txtPassword.setText(model.getValueAt(r, 2).toString());
                cmbRole.setSelectedItem(model.getValueAt(r, 3).toString());
            }
        });

        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        for (User u : dao.getAllUsers()) {
            model.addRow(new Object[]{u.getId(), u.getUsername(), u.getPassword(), u.getRole()});
        }
    }

    private void clearForm() {
        txtUsername.setText("");
        txtPassword.setText("");
        selectedId = -1;
        table.clearSelection();
    }
}