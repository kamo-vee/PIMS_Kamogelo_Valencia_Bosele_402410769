package pims.dao;

import pims.db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDao {

    public List<Object[]> getSalesReport() {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT s.id, s.sale_date, s.total, u.username " +
                     "FROM sales s LEFT JOIN users u ON s.cashier_id = u.id " +
                     "ORDER BY s.id DESC";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                rows.add(new Object[]{
                        rs.getInt("id"),
                        rs.getTimestamp("sale_date"),
                        rs.getDouble("total"),
                        rs.getString("username")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return rows;
    }

    /** ITEM-WISE report: totals per medicine sold. */
    public List<Object[]> getItemWiseReport() {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT m.id, m.name, m.category, " +
                     "COALESCE(SUM(si.quantity), 0) AS qty_sold, " +
                     "COALESCE(SUM(si.quantity * si.price), 0) AS revenue " +
                     "FROM medicines m " +
                     "LEFT JOIN sale_items si ON m.id = si.medicine_id " +
                     "GROUP BY m.id, m.name, m.category " +
                     "ORDER BY qty_sold DESC";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                rows.add(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getInt("qty_sold"),
                        rs.getDouble("revenue")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return rows;
    }

    public double getTotalRevenue() {
        String sql = "SELECT COALESCE(SUM(total), 0) FROM sales";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int getTodaySalesCount() {
        String sql = "SELECT COUNT(*) FROM sales WHERE DATE(sale_date) = CURDATE()";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
}