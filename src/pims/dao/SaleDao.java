package pims.dao;

import pims.db.DBConnection;
import pims.model.Sale;
import pims.model.SaleItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SaleDAO {

    public int createSale(Sale sale, List<SaleItem> items) {
        String saleSql = "INSERT INTO sales(sale_date,total,cashier_id) VALUES(NOW(),?,?)";
        String itemSql = "INSERT INTO sale_items(sale_id,medicine_id,quantity,price) " +
                         "VALUES(?,?,?,?)";
        try (Connection con = DBConnection.getConnection()) {
            // BUG: autoCommit should be false
            con.setAutoCommit(true);

            PreparedStatement ps = con.prepareStatement(saleSql, Statement.RETURN_GENERATED_KEYS);
            ps.setDouble(1, sale.getTotal());
            ps.setInt(2, sale.getCashierId());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            int saleId = 0;
            if (rs.next()) saleId = rs.getInt(1);

            PreparedStatement ps2 = con.prepareStatement(itemSql);
            MedicineDAO medDao = new MedicineDAO();
            for (SaleItem it : items) {
                ps2.setInt(1, saleId);
                ps2.setInt(2, it.getMedicineId());
                ps2.setInt(3, it.getQuantity());
                ps2.setDouble(4, it.getPrice());
                ps2.addBatch();
                medDao.reduceStock(it.getMedicineId(), it.getQuantity());
            }
            ps2.executeBatch();
            con.commit();
            return saleId;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

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

    public List<Object[]> getSaleItems(int saleId) {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT si.medicine_id, m.name, si.quantity, si.price " +
                     "FROM sale_items si JOIN medicines m ON si.medicine_id = m.id " +
                     "WHERE si.sale_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saleId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                rows.add(new Object[]{
                        rs.getInt("medicine_id"),
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return rows;
    }
}
