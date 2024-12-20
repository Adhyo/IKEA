package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

public class DatabaseManager {
    // private static final String URL = "jdbc:mysql://localhost:3306/ikea";
    // private static final String USER = "root";
    // private static final String PASSWORD = "password";

    // public static Connection getConnection() throws SQLException {
    //     return DriverManager.getConnection(URL, USER, PASSWORD);
    // }

    public Connection con;
    private String driver = "com.mysql.cj.jdbc.Driver";
    private String url = "jdbc:mysql://localhost/ikea";
    // private String url = "jdbc:mysql://localhost/db_test?serverTimezone=" +
    // TimeZone.getDefault().getID();
    private String username = "root";
    private String password = "";

    private Connection logOn() {
        try {
            // Load JDBC Driver
            Class.forName(driver);
            // Buat Object Connection
            con = DriverManager.getConnection(url, username, password);
        } catch (Exception ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getLocalizedMessage());
            JOptionPane.showMessageDialog(null, "Error Ocurred when login" + ex);
        }
        return con;
    }

    private void logOff() {
        try {
            // tutup koneksi
            con.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error Ocurred when login" + ex);
        }
    }

    public void connect() {
        try {
            con = logOn();
        } catch (Exception ex) {
            System.out.println("Error occured when connecting to database");
        }
    }
    public void disconnect() {
        try {
            logOff();
        } catch (Exception ex) {
            System.out.println("Error occured when connecting to database");
        }
    }
}
