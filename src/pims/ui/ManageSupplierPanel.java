package pims.ui;

import pims.dao.SupplierDao;
import pims.model.Supplier;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ManageSupplierPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtName, txtContact, txtAddress;
    private int selectedId = -1;
    private final SupplierDao dao = new SupplierDao();

    public ManageSupplierPanel() {
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Supplier Details"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0; form.add(new JLabel("Name:"), g);
        txtName = new JTextField(15);
        g.gridx = 1; form.add(txtName, g);

        g.gridx = 0; g.gridy = 1; form.add(new JLabel("Contact:"), g);
        txtContact = new JTextField(15);
        g.gridx = 1; form.add(txtContact, g);

        g.gridx = 0; g.gridy = 2; form.add(new JLabel("Address:"), g);
        txtAddress = new JTextField(15);
        g.gridx = 1; form.add(txtAddress, g);

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
                new Object[]{"ID", "Name", "Contact", "Address"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        btnAdd.addActionListener(e -> {
            dao.addSupplier(new Supplier(0, txtName.getText(),
                    txtContact.getText(), txtAddress.getText()));
            loadData(); clearForm();
        });
        btnUpdate.addActionListener(e -> {
            if (selectedId == -1) return;
            dao.updateSupplier(new Supplier(selectedId, txtName.getText(),
                    txtContact.getText(), txtAddress.getText()));
            loadData(); clearForm();
        });
        btnDelete.addActionListener(e -> {
            if (selectedId == -1) return;
            if (JOptionPane.showConfirmDialog(this, "Delete?") == JOptionPane.YES_OPTION) {
                dao.deleteSupplier(selectedId);
                loadData(); clearForm();
            }
        });
        btnClear.addActionListener(e -> clearForm());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int r = table.getSelectedRow();
                selectedId = Integer.parseInt(model.getValueAt(r, 0).toString());
                txtName.setText(model.getValueAt(r, 1).toString());
                txtContact.setText(model.getValueAt(r, 2).toString());
                txtAddress.setText(model.getValueAt(r, 3).toString());
            }
        });

        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        for (Supplier s : dao.getAllSuppliers()) {
            model.addRow(new Object[]{s.getId(), s.getName(), s.getContact(), s.getAddress()});
        }
    }

    private void clearForm() {
        txtName.setText("");
        txtContact.setText("");
        txtAddress.setText("");
        selectedId = -1;
        table.clearSelection();
    }
}