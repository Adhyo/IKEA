package Database;

import Model.*;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseController {

    private Connection getConnection() throws SQLException {
        db.connect();
        return db.con;
    }
    private static final DatabaseManager db = DatabaseManager.getInstance();

    public User authenticateUser(String username, String password) {
        try {
            db.connect();
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            
            // Debug print
            System.out.println("Attempting login with username: " + username);
            
            try (PreparedStatement pstmt = db.con.prepareStatement(query)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                
                // Debug print the actual SQL query
                System.out.println("Executing query: " + query);
                System.out.println("With parameters: username=" + username + ", password=" + password);
    
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        // Debug print
                        System.out.println("User found in database!");
                        
                        int userId = rs.getInt("user_id");
                        String email = rs.getString("email");
                        String userType = rs.getString("user_type");
                        double income = rs.getDouble("income");
    
                        // Debug print
                        System.out.println("Retrieved user ID: " + userId);
                        System.out.println("User type: " + userType);
    
                        // Create appropriate user object based on user_type
                        if (userType.equals("ADMIN")) {
                            return new Admin(userId, username, password, email, income);
                        } else if (userType.equals("CUSTOMER")) {
                            String name = rs.getString("name");
                            String phone = rs.getString("phone");
                            return new Customer(userId, username, password, email, name, phone);
                        }
                    } else {
                        // Debug print
                        System.out.println("No user found with these credentials");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception occurred: " + e.getMessage());
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
            
            // Debug print
            System.out.println("Attempting to update profile:");
            System.out.println("UserID: " + userId);
            System.out.println("New Username: " + username);
            System.out.println("New Email: " + email);
            System.out.println("Password being updated: " + (password != null && !password.isEmpty() ? "Yes" : "No"));
            
            // Check if the new username exists for a different user
            String checkQuery = "SELECT user_id FROM users WHERE username = ? AND user_id != ?";
            try (PreparedStatement checkStmt = db.con.prepareStatement(checkQuery)) {
                checkStmt.setString(1, username);
                checkStmt.setInt(2, userId);
                
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Update failed: Username already exists");
                        return false;
                    }
                }
            }
    
            // Build update query
            StringBuilder updateQuery = new StringBuilder("UPDATE users SET username = ?, email = ?");
            if (password != null && !password.isEmpty()) {
                updateQuery.append(", password = ?");
            }
            updateQuery.append(" WHERE user_id = ?");
            
            // Debug print
            System.out.println("Executing update query: " + updateQuery.toString());
    
            // Execute the update
            try (PreparedStatement pstmt = db.con.prepareStatement(updateQuery.toString())) {
                int paramIndex = 1;
                pstmt.setString(paramIndex++, username);
                pstmt.setString(paramIndex++, email);
                
                if (password != null && !password.isEmpty()) {
                    pstmt.setString(paramIndex++, password);
                    System.out.println("Setting new password in query");
                }
                pstmt.setInt(paramIndex, userId);
    
                int rowsAffected = pstmt.executeUpdate();
                System.out.println("Rows affected by update: " + rowsAffected);
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception during update: " + e.getMessage());
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

    public List<String> getAllCustomerUsernames() {
        List<String> usernames = new ArrayList<>();
        String query = "SELECT username FROM users WHERE user_type = 'CUSTOMER'";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                usernames.add(rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usernames;
    }

    public boolean sendNotification(String username, String message) {
        String query = "INSERT INTO notifications (user_id, message) " +
                      "SELECT user_id, ? FROM users WHERE username = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, message);
            pstmt.setString(2, username);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String query = "SELECT * FROM categories"; // Update with your actual SQL query
        
        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("category_id");
                String name = resultSet.getString("category_name");
                categories.add(new Category(id, name));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }
}