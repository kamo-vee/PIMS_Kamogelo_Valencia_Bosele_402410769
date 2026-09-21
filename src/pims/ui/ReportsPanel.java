package pims.ui;

import pims.dao.SaleDao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReportsPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private final SaleDao dao = new SaleDao();

    public ReportsPanel() {
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Refresh");
        top.add(btnRefresh);
        add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new Object[]{"Sale ID", "Date", "Total", "Cashier"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        btnRefresh.addActionListener(e -> loadData());
        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        List<Object[]> rows = dao.getSalesReport();
        for (Object[] r : rows) model.addRow(r);
    }
}