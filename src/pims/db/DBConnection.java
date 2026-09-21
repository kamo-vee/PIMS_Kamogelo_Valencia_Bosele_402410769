package pims.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Handles the MySQL database connection.
 */
public class DBConnection {

    private static final String URL      = "jdbc:mysql://127.0.0.1:3307/pims_db";
    private static final String USER     = "root";
    private static final String PASSWORD = "Kamo2004"; // <-- change if needed

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
