package pims.dao;

import pims.db.DBConnection;
import pims.model.Medicine;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MedicineDao {

    public List<Medicine> getAllMedicines() {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT m.*, s.name AS supplier_name FROM medicines m " +
                     "LEFT JOIN suppliers s ON m.supplier_id = s.id";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** Only medicines with quantity < threshold. */
    public List<Medicine> getLowStockMedicines(int threshold) {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT m.*, s.name AS supplier_name FROM medicines m " +
                     "LEFT JOIN suppliers s ON m.supplier_id = s.id " +
                     "WHERE m.quantity < ? ORDER BY m.quantity ASC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, threshold);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** Medicines expiring within the given number of days. */
    public List<Medicine> getExpiringMedicines(int days) {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT m.*, s.name AS supplier_name FROM medicines m " +
                     "LEFT JOIN suppliers s ON m.supplier_id = s.id " +
                     "WHERE m.expiry_date IS NOT NULL " +
                     "AND m.expiry_date <= DATE_ADD(CURDATE(), INTERVAL ? DAY) " +
                     "ORDER BY m.expiry_date ASC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, days);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addMedicine(Medicine m) {
        String sql = "INSERT INTO medicines(name,category,price,quantity,expiry_date,supplier_id) " +
                     "VALUES(?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getName());
            ps.setString(2, m.getCategory());
            ps.setDouble(3, m.getPrice());
            ps.setInt(4, m.getQuantity());
            if (m.getExpiryDate() != null) ps.setDate(5, Date.valueOf(m.getExpiryDate()));
            else ps.setNull(5, Types.DATE);
            ps.setInt(6, m.getSupplierId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateMedicine(Medicine m) {
        String sql = "UPDATE medicines SET name=?, category=?, price=?, quantity=?, " +
                     "expiry_date=?, supplier_id=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getName());
            ps.setString(2, m.getCategory());
            ps.setDouble(3, m.getPrice());
            ps.setInt(4, m.getQuantity());
            if (m.getExpiryDate() != null) ps.setDate(5, Date.valueOf(m.getExpiryDate()));
            else ps.setNull(5, Types.DATE);
            ps.setInt(6, m.getSupplierId());
            ps.setInt(7, m.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteMedicine(int id) {
        String sql = "DELETE FROM medicines WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean reduceStock(int medicineId, int qty) {
        String sql = "UPDATE medicines SET quantity = quantity - ? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, qty);
            ps.setInt(2, medicineId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** Shared row-mapper. */
    private Medicine mapRow(ResultSet rs) throws SQLException {
        Date d = rs.getDate("expiry_date");
        LocalDate expiry = (d == null) ? null : d.toLocalDate();
        Medicine m = new Medicine(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getDouble("price"),
                rs.getInt("quantity"),
                expiry,
                rs.getInt("supplier_id"));
        m.setSupplierName(rs.getString("supplier_name"));
        return m;
    }
}
