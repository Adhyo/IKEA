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

    public boolean updateUserProfile(int userId, String username, String email, String password) {
        try {
            db.connect();
            
            // Check if the new username already exists for a different user
            String checkQuery = "SELECT user_id FROM users WHERE username = ? AND user_id != ?";
            try (PreparedStatement checkStmt = db.con.prepareStatement(checkQuery)) {
                checkStmt.setString(1, username);
                checkStmt.setInt(2, userId);
                
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        return false; // Username already exists for another user
                    }
                }
            }

            // Prepare update query based on whether a new password is provided
            String updateQuery;
            if (password != null && !password.isEmpty()) {
                updateQuery = "UPDATE users SET username = ?, email = ?, password = ? WHERE user_id = ?";
            } else {
                updateQuery = "UPDATE users SET username = ?, email = ? WHERE user_id = ?";
            }

            // Execute the update
            try (PreparedStatement pstmt = db.con.prepareStatement(updateQuery)) {
                pstmt.setString(1, username);
                pstmt.setString(2, email);
                
                if (password != null && !password.isEmpty()) {
                    pstmt.setString(3, password);
                    pstmt.setInt(4, userId);
                } else {
                    pstmt.setInt(3, userId);
                }

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

    public User getUserById(int userId) {
        try {
            db.connect();
            String query = "SELECT * FROM users WHERE user_id = ?";
            try (PreparedStatement pstmt = db.con.prepareStatement(query)) {
                pstmt.setInt(1, userId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String username = rs.getString("username");
                        String password = rs.getString("password");
                        String email = rs.getString("email");
                        String userType = rs.getString("user_type");
                        
                        if (userType.equals("ADMIN")) {
                            double income = rs.getDouble("income");
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
            db.disconnect();
        }
        return null;
    }
}