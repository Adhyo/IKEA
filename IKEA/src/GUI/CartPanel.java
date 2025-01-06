package GUI;

import Model.User;
import Model.CartObserver;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import Database.DatabaseManager;
import java.time.LocalDate;

public class CartPanel extends JPanel implements CartObserver {
    private final DatabaseManager db = DatabaseManager.getInstance();
    private final User currentUser;
    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JLabel totalLabel;
    private double totalAmount = 0.0;
    private JTextArea addressArea;

    public CartPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());

        // Create gradient background
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
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header Panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setOpaque(false);
        JLabel headerLabel = new JLabel("Shopping Cart");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(new Color(248, 209, 21));
        headerPanel.add(headerLabel);

        // Table Panel
        createTablePanel();
        JScrollPane scrollPane = new JScrollPane(cartTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        // Address Panel
        JPanel addressPanel = new JPanel(new BorderLayout(5, 5));
        addressPanel.setOpaque(false);
        JLabel addressLabel = new JLabel("Delivery Address:");
        addressLabel.setForeground(new Color(248, 209, 21));
        addressArea = new JTextArea(3, 20);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        JScrollPane addressScroll = new JScrollPane(addressArea);
        addressPanel.add(addressLabel, BorderLayout.NORTH);
        addressPanel.add(addressScroll, BorderLayout.CENTER);

        // Total Panel
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.setOpaque(false);
        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalLabel.setForeground(new Color(248, 209, 21));
        totalPanel.add(totalLabel);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton checkoutButton = createStyledButton("Checkout");
        JButton clearCartButton = createStyledButton("Clear Cart");
        JButton removeItemButton = createStyledButton("Remove Selected");
        
        buttonPanel.add(checkoutButton);
        buttonPanel.add(clearCartButton);
        buttonPanel.add(removeItemButton);

        // Add action listeners
        checkoutButton.addActionListener(e -> handleCheckout());
        clearCartButton.addActionListener(e -> clearCart());
        removeItemButton.addActionListener(e -> removeSelectedItem());

        // Combine panels
        JPanel southPanel = new JPanel(new BorderLayout(10, 10));
        southPanel.setOpaque(false);
        southPanel.add(addressPanel, BorderLayout.NORTH);
        southPanel.add(totalPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        add(mainPanel);
        loadCartItems();
    }

    private void createTablePanel() {
        String[] columnNames = {"Product ID", "Name", "Price", "Quantity", "Subtotal"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Only quantity is editable
            }
        };
        
        cartTable = new JTable(tableModel);
        cartTable.setForeground(Color.BLACK);
        cartTable.setFont(new Font("Arial", Font.PLAIN, 14));
        cartTable.setRowHeight(25);
        cartTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
        // Add change listener for quantity updates
        tableModel.addTableModelListener(e -> {
            if (e.getColumn() == 3) { // Quantity column
                updateQuantity(e.getFirstRow());
            }
        });
    }

    private void loadCartItems() {
        if (currentUser == null) return;
        
        try {
            db.connect();
            String query = "SELECT p.product_id, p.name, p.price, cp.quantity " +
                          "FROM cart_products cp " +
                          "JOIN products p ON cp.product_id = p.product_id " +
                          "JOIN carts c ON cp.cart_id = c.cart_id " +
                          "WHERE c.user_id = ? AND c.cart_id NOT IN (SELECT cart_id FROM orders)";
                          
            PreparedStatement pstmt = db.con.prepareStatement(query);
            pstmt.setInt(1, currentUser.getUserId());
            ResultSet rs = pstmt.executeQuery();

            tableModel.setRowCount(0);
            totalAmount = 0.0;

            while (rs.next()) {
                int productId = rs.getInt("product_id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");
                double subtotal = price * quantity;
                totalAmount += subtotal;

                tableModel.addRow(new Object[]{
                    productId, name, price, quantity, subtotal
                });
            }

            updateTotalLabel();
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.disconnect();
        }
    }

    private void handleCheckout() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "Your cart is empty!",
                "Cannot Checkout",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (addressArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a delivery address",
                "Address Required",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Confirm checkout?\nTotal amount: $" + String.format("%.2f", totalAmount),
            "Confirm Checkout",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                db.connect();
                
                // Get cart ID
                String cartQuery = "SELECT cart_id FROM carts WHERE user_id = ? AND cart_id NOT IN (SELECT cart_id FROM orders)";
                PreparedStatement cartStmt = db.con.prepareStatement(cartQuery);
                cartStmt.setInt(1, currentUser.getUserId());
                ResultSet cartRs = cartStmt.executeQuery();
                
                if (cartRs.next()) {
                    int cartId = cartRs.getInt("cart_id");
                    
                    // Create order
                    String orderQuery = "INSERT INTO orders (cart_id, address, price, status) VALUES (?, ?, ?, 'UNPAID')";
                    PreparedStatement orderStmt = db.con.prepareStatement(orderQuery);
                    orderStmt.setInt(1, cartId);
                    orderStmt.setString(2, addressArea.getText().trim());
                    orderStmt.setDouble(3, totalAmount);
                    orderStmt.executeUpdate();

                    // Create transaction record
                    String transQuery = "INSERT INTO transactions (cart_id, sub_total, final_amount, transaction_date) VALUES (?, ?, ?, ?)";
                    PreparedStatement transStmt = db.con.prepareStatement(transQuery);
                    transStmt.setInt(1, cartId);
                    transStmt.setDouble(2, totalAmount);
                    transStmt.setDouble(3, totalAmount); // Without promo applied
                    transStmt.setDate(4, java.sql.Date.valueOf(LocalDate.now()));
                    transStmt.executeUpdate();
                    
                    JOptionPane.showMessageDialog(this,
                        "Order placed successfully!\nPlease proceed with payment.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Clear the cart display
                    tableModel.setRowCount(0);
                    totalAmount = 0.0;
                    updateTotalLabel();
                    addressArea.setText("");
                }
                
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error during checkout: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            } finally {
                db.disconnect();
            }
        }
    }

    private void updateQuantity(int row) {
        try {
            int productId = (int) tableModel.getValueAt(row, 0);
            int quantity = (int) tableModel.getValueAt(row, 3);
            double price = (double) tableModel.getValueAt(row, 2);
            
            db.connect();
            
            // Get cart ID
            String cartQuery = "SELECT cart_id FROM carts WHERE user_id = ? AND cart_id NOT IN (SELECT cart_id FROM orders)";
            PreparedStatement cartStmt = db.con.prepareStatement(cartQuery);
            cartStmt.setInt(1, currentUser.getUserId());
            ResultSet cartRs = cartStmt.executeQuery();
            
            if (cartRs.next()) {
                int cartId = cartRs.getInt("cart_id");
                
                // Update quantity
                String updateQuery = "UPDATE cart_products SET quantity = ? WHERE cart_id = ? AND product_id = ?";
                PreparedStatement updateStmt = db.con.prepareStatement(updateQuery);
                updateStmt.setInt(1, quantity);
                updateStmt.setInt(2, cartId);
                updateStmt.setInt(3, productId);
                updateStmt.executeUpdate();
                
                // Update subtotal in table
                double subtotal = price * quantity;
                tableModel.setValueAt(subtotal, row, 4);
                
                // Update total
                calculateTotal();
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.disconnect();
        }
    }

    private void calculateTotal() {
        totalAmount = 0.0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            totalAmount += (double) tableModel.getValueAt(i, 4);
        }
        updateTotalLabel();
    }

    private void updateTotalLabel() {
        totalLabel.setText(String.format("Total: $%.2f", totalAmount));
    }

    private void clearCart() {
        if (tableModel.getRowCount() == 0) return;

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to clear your cart?",
            "Confirm Clear Cart",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                db.connect();
                
                // Get cart ID
                String cartQuery = "SELECT cart_id FROM carts WHERE user_id = ? AND cart_id NOT IN (SELECT cart_id FROM orders)";
                PreparedStatement cartStmt = db.con.prepareStatement(cartQuery);
                cartStmt.setInt(1, currentUser.getUserId());
                ResultSet cartRs = cartStmt.executeQuery();
                
                if (cartRs.next()) {
                    int cartId = cartRs.getInt("cart_id");
                    
                    // Delete all items
                    String deleteQuery = "DELETE FROM cart_products WHERE cart_id = ?";
                    PreparedStatement deleteStmt = db.con.prepareStatement(deleteQuery);
                    deleteStmt.setInt(1, cartId);
                    deleteStmt.executeUpdate();
                    
                    // Clear the display
                    tableModel.setRowCount(0);
                    totalAmount = 0.0;
                    updateTotalLabel();
                }
                
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                db.disconnect();
            }
        }
    }

    private void removeSelectedItem() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select an item to remove",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            db.connect();
            
            int productId = (int) tableModel.getValueAt(selectedRow, 0);
            
            // Get cart ID
            String cartQuery = "SELECT cart_id FROM carts WHERE user_id = ? AND cart_id NOT IN (SELECT cart_id FROM orders)";
            PreparedStatement cartStmt = db.con.prepareStatement(cartQuery);
            cartStmt.setInt(1, currentUser.getUserId());
            ResultSet cartRs = cartStmt.executeQuery();
            
            if (cartRs.next()) {
                int cartId = cartRs.getInt("cart_id");
                
                // Delete item
                String deleteQuery = "DELETE FROM cart_products WHERE cart_id = ? AND product_id = ?";
                PreparedStatement deleteStmt = db.con.prepareStatement(deleteQuery);
                deleteStmt.setInt(1, cartId);
                deleteStmt.setInt(2, productId);
                deleteStmt.executeUpdate();
                
                // Remove from table
                tableModel.removeRow(selectedRow);
                calculateTotal();
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.disconnect();
        }
    }

    @Override
    public void onCartUpdated() {
        refreshCart(); // Muat ulang data keranjang
    }

    public void refreshCart() {
        loadCartItems(); // Sudah ada di implementasi sebelumnya
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(new Color(0, 51, 153));
        button.setForeground(new Color(248, 209, 21));
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 30));
        return button;
    }
}