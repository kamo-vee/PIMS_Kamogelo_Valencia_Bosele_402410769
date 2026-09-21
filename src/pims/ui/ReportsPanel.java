package pims.ui;

import pims.dao.ReportDao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReportsPanel extends JPanel {

    private final ReportDao dao = new ReportDao();

    private JTable salesTable;
    private DefaultTableModel salesModel;
    private JLabel lblSummary;

    private JTable itemTable;
    private DefaultTableModel itemModel;

    public ReportsPanel() {
        setLayout(new BorderLayout());

        JTabbedPane inner = new JTabbedPane();
        inner.addTab("Sales", buildSalesTab());
        inner.addTab("Item-wise", buildItemWiseTab());

        add(inner, BorderLayout.CENTER);
    }

    private JPanel buildSalesTab() {
        JPanel p = new JPanel(new BorderLayout());

        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Refresh");
        left.add(btnRefresh);
        top.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblSummary = new JLabel(" ");
        lblSummary.setFont(new Font("Segoe UI", Font.BOLD, 13));
        right.add(lblSummary);
        top.add(right, BorderLayout.EAST);

        p.add(top, BorderLayout.NORTH);

        salesModel = new DefaultTableModel(
                new Object[]{"Sale ID", "Date", "Total", "Cashier"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        salesTable = new JTable(salesModel);
        p.add(new JScrollPane(salesTable), BorderLayout.CENTER);

        btnRefresh.addActionListener(e -> loadSales());
        loadSales();
        return p;
    }

    private JPanel buildItemWiseTab() {
        JPanel p = new JPanel(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Refresh");
        top.add(btnRefresh);
        p.add(top, BorderLayout.NORTH);

        itemModel = new DefaultTableModel(
                new Object[]{"ID", "Medicine", "Category", "Qty Sold", "Revenue"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        itemTable = new JTable(itemModel);
        p.add(new JScrollPane(itemTable), BorderLayout.CENTER);

        btnRefresh.addActionListener(e -> loadItems());
        loadItems();
        return p;
    }

    private void loadSales() {
        salesModel.setRowCount(0);
        List<Object[]> rows = dao.getSalesReport();
        for (Object[] r : rows) salesModel.addRow(r);
        lblSummary.setText(String.format(
                "Total Sales: %d   |   Revenue: PHP %.2f   |   Today: %d",
                rows.size(), dao.getTotalRevenue(), dao.getTodaySalesCount()));
    }

    private void loadItems() {
        itemModel.setRowCount(0);
        List<Object[]> rows = dao.getItemWiseReport();
        for (Object[] r : rows) itemModel.addRow(r);
    }
}