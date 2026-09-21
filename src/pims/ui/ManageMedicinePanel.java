package pims.ui;

import pims.dao.MedicineDao;
import pims.dao.SupplierDao;
import pims.model.Medicine;
import pims.model.Supplier;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ManageMedicinePanel extends JPanel {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtName, txtCategory, txtPrice, txtQty, txtExpiry;
    private JComboBox<Supplier> cmbSupplier;
    private int selectedId = -1;
    private final MedicineDao dao = new MedicineDao();
    private final SupplierDao supplierDAO = new SupplierDao();

    public ManageMedicinePanel() {
        setLayout(new BorderLayout());

        // ---- Form ----
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

        g.gridx = 0; g.gridy = 4; form.add(new JLabel("Expiry (yyyy-MM-dd):"), g);
        txtExpiry = new JTextField(15);
        g.gridx = 1; form.add(txtExpiry, g);

        g.gridx = 0; g.gridy = 5; form.add(new JLabel("Supplier:"), g);
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

        g.gridx = 0; g.gridy = 6; g.gridwidth = 2;
        form.add(buttons, g);
        add(form, BorderLayout.NORTH);

        // ---- Table ----
        model = new DefaultTableModel(
                new Object[]{"ID", "Name", "Category", "Price", "Qty", "Expiry", "Supplier", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setDefaultRenderer(Object.class, new StatusRenderer());
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ---- Actions ----
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
                Object expiry = model.getValueAt(r, 5);
                txtExpiry.setText(expiry == null ? "" : expiry.toString());
            }
        });

        loadSuppliers();
        loadMedicines();
    }

    private void loadSuppliers() {
        cmbSupplier.removeAllItems();
        for (Supplier s : supplierDAO.getAllSuppliers()) cmbSupplier.addItem(s);
    }

    private void loadMedicines() {
        model.setRowCount(0);
        List<Medicine> list = dao.getAllMedicines();
        for (Medicine m : list) {
            String status = m.isLowStock() ? "LOW STOCK"
                          : m.isExpiringSoon() ? "EXPIRING"
                          : "OK";
            model.addRow(new Object[]{
                    m.getId(), m.getName(), m.getCategory(),
                    m.getPrice(), m.getQuantity(),
                    m.getExpiryDate(),
                    m.getSupplierName(),
                    status
            });
        }
    }

    private LocalDate parseExpiry(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try { return LocalDate.parse(s.trim(), FMT); }
        catch (Exception e) { return null; }
    }

    private void addMedicine() {
        try {
            Supplier s = (Supplier) cmbSupplier.getSelectedItem();
            if (s == null) { JOptionPane.showMessageDialog(this, "Add a supplier first."); return; }
            Medicine m = new Medicine(0, txtName.getText(), txtCategory.getText(),
                    Double.parseDouble(txtPrice.getText()),
                    Integer.parseInt(txtQty.getText()),
                    parseExpiry(txtExpiry.getText()),
                    s.getId());
            if (dao.addMedicine(m)) { loadMedicines(); clearForm(); }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
        }
    }

    private void updateMedicine() {
        if (selectedId == -1) { JOptionPane.showMessageDialog(this, "Select a row"); return; }
        try {
            Supplier s = (Supplier) cmbSupplier.getSelectedItem();
            Medicine m = new Medicine(selectedId, txtName.getText(), txtCategory.getText(),
                    Double.parseDouble(txtPrice.getText()),
                    Integer.parseInt(txtQty.getText()),
                    parseExpiry(txtExpiry.getText()),
                    s.getId());
            if (dao.updateMedicine(m)) { loadMedicines(); clearForm(); }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
        }
    }

    private void deleteMedicine() {
        if (selectedId == -1) { JOptionPane.showMessageDialog(this, "Select a row"); return; }
        if (JOptionPane.showConfirmDialog(this, "Delete this medicine?") == JOptionPane.YES_OPTION) {
            dao.deleteMedicine(selectedId);
            loadMedicines();
            clearForm();
        }
    }

    private void clearForm() {
        txtName.setText(""); txtCategory.setText(""); txtPrice.setText("");
        txtQty.setText(""); txtExpiry.setText("");
        selectedId = -1;
        table.clearSelection();
    }

    /** Row colouring by status. */
    private class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int row, int col) {
            Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
            String status = String.valueOf(t.getValueAt(row, 7));
            if (!sel) {
                if ("LOW STOCK".equals(status)) c.setBackground(new Color(255, 230, 230));
                else if ("EXPIRING".equals(status)) c.setBackground(new Color(255, 245, 210));
                else c.setBackground(Color.WHITE);
            }
            return c;
        }
    }
}