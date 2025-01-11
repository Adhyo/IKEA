package GUI;

import Model.*;
import Database.DatabaseController;
import Database.DatabaseManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ManageUserFrame extends JFrame {
    private JTable userTable;
    private DefaultTableModel tableModel;
    private DatabaseController dbController;
    
    public ManageUserFrame() {
        this.dbController = new DatabaseController();
        
        setTitle("Manage Users");
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, new Color(0, 51, 153), getWidth(), getHeight(), new Color(0, 105, 255));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        tableModel = new DefaultTableModel(new String[]{"ID", "Username", "Email", "Type", "Status"}, 0);
        userTable = new JTable(tableModel);
        userTable.setFillsViewportHeight(true);
        userTable.setBackground(Color.WHITE);
        userTable.setForeground(new Color(0, 51, 153));
        userTable.setFont(new Font("Arial", Font.PLAIN, 14));
        userTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
        JScrollPane scrollPane = new JScrollPane(userTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        
        JButton activateButton = createStyledButton("Activate", true);
        JButton deactivateButton = createStyledButton("Deactivate", false);
        JButton refreshButton = createStyledButton("Refresh", true);
        
        buttonPanel.add(activateButton);
        buttonPanel.add(deactivateButton);
        buttonPanel.add(refreshButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        activateButton.addActionListener(e -> updateUserStatus(true));
        deactivateButton.addActionListener(e -> updateUserStatus(false));
        refreshButton.addActionListener(e -> loadUsers());
        
        add(mainPanel);
        loadUsers();
        setVisible(true);
    }
    
    private JButton createStyledButton(String text, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(120, 40));
        button.setBackground(isPrimary ? new Color(0, 51, 153) : new Color(220, 220, 220));
        button.setForeground(isPrimary ? new Color(248, 209, 21) : new Color(0, 51, 153));
        button.setFocusPainted(false);
        return button;
    }
    
    private void loadUsers() {
        tableModel.setRowCount(0);
        try {
            DatabaseManager.getInstance().connect();
            Statement stmt = DatabaseManager.getInstance().con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE user_type != 'ADMIN'");
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("user_type"),
                    rs.getBoolean("is_active") ? "Active" : "Inactive"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + ex.getMessage());
        } finally {
            DatabaseManager.getInstance().disconnect();
        }
    }
    
    private void updateUserStatus(boolean active) {
        int row = userTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user");
            return;
        }
        
        try {
            DatabaseManager.getInstance().connect();
            PreparedStatement pstmt = DatabaseManager.getInstance().con.prepareStatement(
                "UPDATE users SET is_active = ? WHERE user_id = ?"
            );
            pstmt.setBoolean(1, active);
            pstmt.setInt(2, (Integer)userTable.getValueAt(row, 0));
            pstmt.executeUpdate();
            loadUsers();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating user: " + ex.getMessage());
        } finally {
            DatabaseManager.getInstance().disconnect();
        }
    }
}