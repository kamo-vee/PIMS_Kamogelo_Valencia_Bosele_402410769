package pims.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // BUG: port should be 3307, driver should be com.mysql.cj.jdbc.Driver
    private static final String URL      = "jdbc:mysql://127.0.0.1:3306/pims_db";
    private static final String USER     = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
