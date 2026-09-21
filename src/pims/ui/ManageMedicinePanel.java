package pims.ui;

import pims.dao.MedicineDao;
import pims.dao.SupplierDao;
import pims.model.Medicine;
import pims.model.Supplier;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageMedicinePanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtName, txtCategory, txtPrice, txtQty;
    private JComboBox<Supplier> cmbSupplier;
    private int selectedId = -1;

    private final MedicineDao medicineDAO = new MedicineDao();
    private final SupplierDao supplierDAO = new SupplierDao();

    public ManageMedicinePanel() {
        setLayout(new BorderLayout());

        // ---------- Form ----------
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Medicine Details"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0; form.add(new JLabel("Name:"), g);
        txtName = new JTextField(15);
        g.gridx = 1; form.add(txtName, g);

        g.gridx = 0; g.gridy = 1; form.add(new JLabel("Category:"), g);
        txtCategory = new JTextField(15);
        g.gridx = 1; form.add(txtCategory, g);

        g.gridx = 0; g.gridy = 2; form.add(new JLabel("Price:"), g);
        txtPrice = new JTextField(15);
        g.gridx = 1; form.add(txtPrice, g);

        g.gridx = 0; g.gridy = 3; form.add(new JLabel("Quantity:"), g);
        txtQty = new JTextField(15);
        g.gridx = 1; form.add(txtQty, g);

        g.gridx = 0; g.gridy = 4; form.add(new JLabel("Supplier:"), g);
        cmbSupplier = new JComboBox<>();
        g.gridx = 1; form.add(cmbSupplier, g);

        JPanel buttons = new JPanel();
        JButton btnAdd    = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear  = new JButton("Clear");
        buttons.add(btnAdd);
        buttons.add(btnUpdate);
        buttons.add(btnDelete);
        buttons.add(btnClear);

        g.gridx = 0; g.gridy = 5; g.gridwidth = 2;
        form.add(buttons, g);

        add(form, BorderLayout.NORTH);

        // ---------- Table ----------
        model = new DefaultTableModel(
                new Object[]{"ID", "Name", "Category", "Price", "Qty", "Supplier"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ---------- Actions ----------
        btnAdd.addActionListener(e -> addMedicine());
        btnUpdate.addActionListener(e -> updateMedicine());
        btnDelete.addActionListener(e -> deleteMedicine());
        btnClear.addActionListener(e -> clearForm());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int r = table.getSelectedRow();
                selectedId = Integer.parseInt(model.getValueAt(r, 0).toString());
                txtName.setText(model.getValueAt(r, 1).toString());
                txtCategory.setText(model.getValueAt(r, 2).toString());
                txtPrice.setText(model.getValueAt(r, 3).toString());
                txtQty.setText(model.getValueAt(r, 4).toString());
                Object supp = model.getValueAt(r, 5);
                if (supp != null) {
                    for (int i = 0; i < cmbSupplier.getItemCount(); i++) {
                        if (cmbSupplier.getItemAt(i).getName().equals(supp.toString())) {
                            cmbSupplier.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
        });

        loadSuppliers();
        loadMedicines();
    }

    private void loadSuppliers() {
        cmbSupplier.removeAllItems();
        List<Supplier> list = supplierDAO.getAllSuppliers();
        for (Supplier s : list) cmbSupplier.addItem(s);
    }

    private void loadMedicines() {
        model.setRowCount(0);
        for (Medicine m : medicineDAO.getAllMedicines()) {
            model.addRow(new Object[]{
                    m.getId(), m.getName(), m.getCategory(),
                    m.getPrice(), m.getQuantity(), m.getSupplierName()
            });
        }
    }

    private void addMedicine() {
        try {
            Supplier s = (Supplier) cmbSupplier.getSelectedItem();
            if (s == null) {
                JOptionPane.showMessageDialog(this, "Add a supplier first.");
                return;
            }
            Medicine m = new Medicine(0, txtName.getText(), txtCategory.getText(),
                    Double.parseDouble(txtPrice.getText()),
                    Integer.parseInt(txtQty.getText()), s.getId());
            if (medicineDAO.addMedicine(m)) { loadMedicines(); clearForm(); }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
        }
    }

    private void updateMedicine() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Select a row");
            return;
        }
        try {
            Supplier s = (Supplier) cmbSupplier.getSelectedItem();
            Medicine m = new Medicine(selectedId, txtName.getText(), txtCategory.getText(),
                    Double.parseDouble(txtPrice.getText()),
                    Integer.parseInt(txtQty.getText()), s.getId());
            if (medicineDAO.updateMedicine(m)) { loadMedicines(); clearForm(); }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
        }
    }

    private void deleteMedicine() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Select a row");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Delete this medicine?") == JOptionPane.YES_OPTION) {
            medicineDAO.deleteMedicine(selectedId);
            loadMedicines();
            clearForm();
        }
    }

    private void clearForm() {
        txtName.setText("");
        txtCategory.setText("");
        txtPrice.setText("");
        txtQty.setText("");
        selectedId = -1;
        table.clearSelection();
    }
}