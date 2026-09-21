package pims.ui;

import pims.dao.MedicineDao;
import pims.dao.SaleDao;
import pims.model.Medicine;
import pims.model.Sale;
import pims.model.SaleItem;
import pims.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class POSPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JComboBox<Medicine> cmbMedicine;
    private JTextField txtQty;
    private JLabel lblTotal;

    private final List<SaleItem> cart = new ArrayList<>();
    private final MedicineDao medicineDAO = new MedicineDao();
    private final SaleDao saleDAO = new SaleDao();
    private final User cashier;

    public POSPanel(User cashier) {
        this.cashier = cashier;
        setLayout(new BorderLayout());

        // ---------- Top: add-to-cart controls ----------
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBorder(BorderFactory.createTitledBorder("Add to Cart"));

        cmbMedicine = new JComboBox<>();
        cmbMedicine.setPreferredSize(new Dimension(280, 28));
        txtQty = new JTextField(5);

        JButton btnAdd      = new JButton("Add");
        JButton btnRemove   = new JButton("Remove Selected");
        JButton btnCheckout = new JButton("Checkout");

        top.add(new JLabel("Medicine:"));
        top.add(cmbMedicine);
        top.add(new JLabel("Qty:"));
        top.add(txtQty);
        top.add(btnAdd);
        top.add(btnRemove);
        top.add(btnCheckout);
        add(top, BorderLayout.NORTH);

        // ---------- Center: cart table ----------
        model = new DefaultTableModel(
                new Object[]{"Medicine ID", "Name", "Qty", "Price", "Subtotal"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ---------- Bottom: total ----------
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTotal = new JLabel("Total: ₱0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        bottom.add(lblTotal);
        add(bottom, BorderLayout.SOUTH);

        // ---------- Actions ----------
        btnAdd.addActionListener(e -> addToCart());
        btnRemove.addActionListener(e -> removeSelected());
        btnCheckout.addActionListener(e -> checkout());

        loadMedicines();
    }

    private void loadMedicines() {
        cmbMedicine.removeAllItems();
        for (Medicine m : medicineDAO.getAllMedicines()) cmbMedicine.addItem(m);
    }

    private void addToCart() {
        Medicine m = (Medicine) cmbMedicine.getSelectedItem();
        if (m == null) return;

        int qty;
        try {
            qty = Integer.parseInt(txtQty.getText());
            if (qty <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter valid quantity");
            return;
        }

        if (qty > m.getQuantity()) {
            JOptionPane.showMessageDialog(this,
                    "Not enough stock! Available: " + m.getQuantity());
            return;
        }

        cart.add(new SaleItem(m.getId(), m.getName(), qty, m.getPrice()));
        refreshCart();
        txtQty.setText("");
    }

    private void removeSelected() {
        int r = table.getSelectedRow();
        if (r == -1) return;
        cart.remove(r);
        refreshCart();
    }

    private void refreshCart() {
        model.setRowCount(0);
        double total = 0;
        for (SaleItem it : cart) {
            model.addRow(new Object[]{
                    it.getMedicineId(), it.getMedicineName(),
                    it.getQuantity(), it.getPrice(), it.getSubtotal()
            });
            total += it.getSubtotal();
        }
        lblTotal.setText(String.format("Total: ₱%.2f", total));
    }

    private void checkout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty");
            return;
        }

        double total = 0;
        for (SaleItem it : cart) total += it.getSubtotal();

        Sale sale = new Sale();
        sale.setTotal(total);
        sale.setCashierId(cashier.getId());

        int saleId = saleDAO.createSale(sale, cart);
        if (saleId > 0) {
            new BillDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    saleId, cart, total, cashier.getUsername()
            ).setVisible(true);

            cart.clear();
            refreshCart();
            loadMedicines();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save sale!");
        }
    }
}