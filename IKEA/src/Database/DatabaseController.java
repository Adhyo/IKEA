package Database;

import Model.User;
import Model.Admin;
import Model.Customer;
import Model.UserType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseController {
    private static final DatabaseManager db = DatabaseManager.getInstance();

    public User authenticateUser(String username, String password) {
        try {
            db.connect();
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement pstmt = db.con.prepareStatement(query)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        int userId = rs.getInt("user_id");
                        String email = rs.getString("email");
                        String userType = rs.getString("user_type");
                        double income = rs.getDouble("income");

                        // Create appropriate user object based on user_type
                        if (userType.equals("ADMIN")) {
                            return new Admin(userId, username, password, email, income);
                        } else if (userType.equals("CUSTOMER")) {
                            String name = rs.getString("name");
                            String phone = rs.getString("phone");
                            return new Customer(userId, username, password, email, name, phone);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (db.con != null) {
                    db.con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public UserType getUserType(String username) {
        try {
            db.connect();
            String query = "SELECT user_type FROM users WHERE username = ?";
            try (PreparedStatement pstmt = db.con.prepareStatement(query)) {
                pstmt.setString(1, username);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String userType = rs.getString("user_type");
                        return userType.equals("ADMIN") ? UserType.ADMIN : UserType.CUSTOMER;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (db.con != null) {
                    db.con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean isUsernameExists(String username) {
        try {
            db.connect();
            String query = "SELECT username FROM users WHERE username = ?";
            try (PreparedStatement pstmt = db.con.prepareStatement(query)) {
                pstmt.setString(1, username);
                try (ResultSet rs = pstmt.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            db.disconnect();
        }
    }

    public boolean registerCustomer(String username, String password, String email,
            String name, String phone) {
        if (isUsernameExists(username)) {
            return false;
        }

        try {
            db.connect();
            String query = "INSERT INTO users (username, password, email, user_type, name, phone) " +
                    "VALUES (?, ?, ?, 'CUSTOMER', ?, ?)";

            try (PreparedStatement pstmt = db.con.prepareStatement(query)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                pstmt.setString(3, email);
                pstmt.setString(4, name);
                pstmt.setString(5, phone);

                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            db.disconnect();
        }
    }
}