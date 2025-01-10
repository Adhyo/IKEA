package GUI;

import Database.DatabaseManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class RemoveProductFrame extends JFrame {
    private final DatabaseManager db = DatabaseManager.getInstance();
    private JTable productTable;
    private DefaultTableModel tableModel;

    public RemoveProductFrame() {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Frame configuration
        setTitle("IKEA Marketplace - Remove Product");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(0, 51, 153), 
                    getWidth(), getHeight(), new Color(0, 105, 255)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Title Label
        JLabel titleLabel = new JLabel("Remove Product", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Create table panel
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setOpaque(false);

        // Initialize table
        String[] columnNames = {"ID", "Name", "Description", "Price", "Stock"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.setBackground(Color.WHITE);
        productTable.setForeground(new Color(0, 51, 153));
        productTable.setFont(new Font("Arial", Font.PLAIN, 14));
        productTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        productTable.setRowHeight(25);

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setPreferredSize(new Dimension(700, 300));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setOpaque(false);

        // Create buttons
        JButton removeButton = createStyledButton("Remove Selected Product", true);
        JButton refreshButton = createStyledButton("Refresh List", false);
        JButton cancelButton = createStyledButton("Cancel", false);

        buttonPanel.add(removeButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(cancelButton);

        // Add panels to main panel
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        removeButton.addActionListener(e -> removeSelectedProduct());
        refreshButton.addActionListener(e -> refreshProductTable());
        cancelButton.addActionListener(e -> dispose());

        // Initial load of products
        refreshProductTable();

        // Add main panel to frame
        add(mainPanel);
        setVisible(true);
    }

    private JButton createStyledButton(String text, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(200, 40));
        
        if (isPrimary) {
            button.setBackground(new Color(0, 51, 153));
            button.setForeground(new Color(248, 209, 21));
        } else {
            button.setBackground(new Color(0, 51, 153));
            button.setForeground(new Color(4, 52, 140));
        }
        
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void refreshProductTable() {
        tableModel.setRowCount(0); // Clear existing rows
        
        try {
            db.connect();
            String query = "SELECT * FROM products ORDER BY product_id";
            PreparedStatement pstmt = db.con.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("product_id"));
                row.add(rs.getString("name"));
                row.add(rs.getString("description"));
                row.add(String.format("Rp %.2f", rs.getDouble("price")));
                row.add(rs.getInt("stock_quantity"));
                tableModel.addRow(row);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error loading products: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            db.disconnect();
        }
    }

    private void removeSelectedProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a product to remove",
                "Selection Required",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int productId = (int) productTable.getValueAt(selectedRow, 0);
        String productName = (String) productTable.getValueAt(selectedRow, 1);

        // Confirmation dialog
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to remove the following product?\n\n" +
            "ID: " + productId + "\n" +
            "Name: " + productName,
            "Confirm Removal",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (deleteProduct(productId)) {
                    JOptionPane.showMessageDialog(this,
                        "Product removed successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    refreshProductTable(); // Refresh the table
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to remove product. It might be referenced in other tables.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private boolean deleteProduct(int productId) throws SQLException {
        try {
            db.connect();
            // First check if the product is in any cart
            String checkQuery = "SELECT COUNT(*) FROM cart_products WHERE product_id = ?";
            PreparedStatement checkStmt = db.con.prepareStatement(checkQuery);
            checkStmt.setInt(1, productId);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                return false; // Product is in use, cannot delete
            }

            // If product is not in use, proceed with deletion
            String deleteQuery = "DELETE FROM products WHERE product_id = ?";
            PreparedStatement deleteStmt = db.con.prepareStatement(deleteQuery);
            deleteStmt.setInt(1, productId);
            
            int result = deleteStmt.executeUpdate();
            return result > 0;
        } finally {
            db.disconnect();
        }
    }
}