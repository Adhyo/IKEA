package Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseController {
    private static DatabaseManager db = new DatabaseManager();

    public boolean checkLogin(String username, String password) {
        try {
            db.connect();
            String query = "SELECT * FROM admin WHERE name = ? AND password = ?";
            try (PreparedStatement pstmt = db.con.prepareStatement(query)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                try (ResultSet rs = pstmt.executeQuery()) {
                    return rs.next(); 
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (db.con != null) {
                    db.con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}