package pims.ui;

import pims.model.SaleItem;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BillDialog extends JDialog {

    public BillDialog(JFrame parent, int saleId,
                      List<SaleItem> items, double total, String cashier) {
        super(parent, "Receipt", true);
        setSize(420, 520);
        setLocationRelativeTo(parent);

        JTextArea area = new JTextArea();
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setEditable(false);

        StringBuilder sb = new StringBuilder();
        sb.append("=====================================\n");
        sb.append("         PHARMACY RECEIPT\n");
        sb.append("=====================================\n");
        sb.append("Sale ID : ").append(saleId).append("\n");
        sb.append("Cashier : ").append(cashier).append("\n");
        sb.append("-------------------------------------\n");
        sb.append(String.format("%-16s %-5s %-8s %-8s%n",
                "Item", "Qty", "Price", "Subtotal"));
        sb.append("-------------------------------------\n");
        for (SaleItem it : items) {
            sb.append(String.format("%-16s %-5d %-8.2f %-8.2f%n",
                    it.getMedicineName(), it.getQuantity(),
                    it.getPrice(), it.getSubtotal()));
        }
        sb.append("-------------------------------------\n");
        sb.append(String.format("TOTAL: PHP %.2f%n", total));
        sb.append("=====================================\n");
        sb.append("     Thank you for your purchase!\n");
        area.setText(sb.toString());

        add(new JScrollPane(area), BorderLayout.CENTER);

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());

        JPanel p = new JPanel();
        p.add(btnClose);
        add(p, BorderLayout.SOUTH);
    }
}