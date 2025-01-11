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

public class CartPanel extends JPanel implements CartObserver {
    private final DatabaseManager db = DatabaseManager.getInstance();
    private final User currentUser;
    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JLabel totalLabel;
    private double totalAmount = 0.0;
    private java.util.List<CartObserver> observers = new java.util.ArrayList<>();
    private JSpinner quantitySpinner;
    private JDialog editDialog;
    private final MainFrame mainFrame;

    public CartPanel(User user, MainFrame mainFrame) {
        this.currentUser = user;
        this.mainFrame = mainFrame;
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

        createTablePanel();
        JScrollPane scrollPane = new JScrollPane(cartTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.setOpaque(false);
        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalLabel.setForeground(new Color(248, 209, 21));
        totalPanel.add(totalLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton editButton = createStyledButton("Edit Item");
        JButton checkoutButton = createStyledButton("Proceed to Checkout");
        JButton clearCartButton = createStyledButton("Clear Cart");
        JButton removeItemButton = createStyledButton("Remove Selected");
        
        buttonPanel.add(editButton);
        buttonPanel.add(checkoutButton);
        buttonPanel.add(clearCartButton);
        buttonPanel.add(removeItemButton);

        editButton.addActionListener(e -> showEditDialog());
        checkoutButton.addActionListener(e -> proceedToCheckout());
        clearCartButton.addActionListener(e -> clearCart());
        removeItemButton.addActionListener(e -> removeSelectedItem());

        JPanel southPanel = new JPanel(new BorderLayout(10, 10));
        southPanel.setOpaque(false);
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
                return false;
            }
        };
        
        cartTable = new JTable(tableModel);
        cartTable.setForeground(Color.BLACK);
        cartTable.setFont(new Font("Arial", Font.PLAIN, 14));
        cartTable.setRowHeight(25);
        cartTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void showEditDialog() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select an item to edit",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int productId = (int) tableModel.getValueAt(selectedRow, 0);
        String productName = (String) tableModel.getValueAt(selectedRow, 1);
        double price = (double) tableModel.getValueAt(selectedRow, 2);
        int currentQuantity = (int) tableModel.getValueAt(selectedRow, 3);

        editDialog = new JDialog(mainFrame, "Edit Cart Item", true);
        editDialog.setLayout(new BorderLayout(10, 10));
        editDialog.setSize(300, 200);
        editDialog.setLocationRelativeTo(this);

        JPanel contentPanel = new JPanel() {
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
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel nameLabel = new JLabel("Product: " + productName);
        nameLabel.setForeground(Color.WHITE);
        contentPanel.add(nameLabel, gbc);

        gbc.gridy++;
        JLabel priceLabel = new JLabel(String.format("Price: $%.2f", price));
        priceLabel.setForeground(Color.WHITE);
        contentPanel.add(priceLabel, gbc);

        gbc.gridy++;
        JLabel quantityLabel = new JLabel("Quantity:");
        quantityLabel.setForeground(Color.WHITE);
        contentPanel.add(quantityLabel, gbc);

        gbc.gridx++;
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(currentQuantity, 1, 99, 1);
        quantitySpinner = new JSpinner(spinnerModel);
        quantitySpinner.setPreferredSize(new Dimension(80, 25));
        contentPanel.add(quantitySpinner, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        
        JButton saveButton = createStyledButton("Save");
        JButton cancelButton = createStyledButton("Cancel");

        saveButton.addActionListener(e -> {
            updateItemQuantity(selectedRow, productId, (int) quantitySpinner.getValue());
            editDialog.dispose();
        });

        cancelButton.addActionListener(e -> editDialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        editDialog.add(contentPanel, BorderLayout.CENTER);
        editDialog.add(buttonPanel, BorderLayout.SOUTH);
        editDialog.setVisible(true);
    }

    private void updateItemQuantity(int row, int productId, int newQuantity) {
        try {
            db.connect();
            
            String cartQuery = "SELECT cart_id FROM carts WHERE user_id = ? AND cart_id NOT IN (SELECT cart_id FROM orders)";
            PreparedStatement cartStmt = db.con.prepareStatement(cartQuery);
            cartStmt.setInt(1, currentUser.getUserId());
            ResultSet cartRs = cartStmt.executeQuery();
            
            if (cartRs.next()) {
                int cartId = cartRs.getInt("cart_id");
                
                String updateQuery = "UPDATE cart_products SET quantity = ? WHERE cart_id = ? AND product_id = ?";
                PreparedStatement updateStmt = db.con.prepareStatement(updateQuery);
                updateStmt.setInt(1, newQuantity);
                updateStmt.setInt(2, cartId);
                updateStmt.setInt(3, productId);
                updateStmt.executeUpdate();

                double price = (double) tableModel.getValueAt(row, 2);
                double newSubtotal = price * newQuantity;
                tableModel.setValueAt(newQuantity, row, 3);
                tableModel.setValueAt(newSubtotal, row, 4);
                
                calculateTotal();
                
                notifyObservers();
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error updating quantity: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            db.disconnect();
        }
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

    private void proceedToCheckout() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "Your cart is empty!",
                "Cannot Checkout",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        notifyObservers();
        MainFrame.getInstance().showCheckoutPanel();
    }

    private void updateQuantity(int row) {
        try {
            int productId = (int) tableModel.getValueAt(row, 0);
            int quantity = (int) tableModel.getValueAt(row, 3);
            double price = (double) tableModel.getValueAt(row, 2);
            
            db.connect();
            
            String cartQuery = "SELECT cart_id FROM carts WHERE user_id = ? AND cart_id NOT IN (SELECT cart_id FROM orders)";
            PreparedStatement cartStmt = db.con.prepareStatement(cartQuery);
            cartStmt.setInt(1, currentUser.getUserId());
            ResultSet cartRs = cartStmt.executeQuery();
            
            if (cartRs.next()) {
                int cartId = cartRs.getInt("cart_id");
                String updateQuery = "UPDATE cart_products SET quantity = ? WHERE cart_id = ? AND product_id = ?";
                PreparedStatement updateStmt = db.con.prepareStatement(updateQuery);
                updateStmt.setInt(1, quantity);
                updateStmt.setInt(2, cartId);
                updateStmt.setInt(3, productId);
                updateStmt.executeUpdate();

                double subtotal = price * quantity;
                tableModel.setValueAt(subtotal, row, 4);
                calculateTotal();
                notifyObservers();
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
                String cartQuery = "SELECT cart_id FROM carts WHERE user_id = ? AND cart_id NOT IN (SELECT cart_id FROM orders)";
                PreparedStatement cartStmt = db.con.prepareStatement(cartQuery);
                cartStmt.setInt(1, currentUser.getUserId());
                ResultSet cartRs = cartStmt.executeQuery();
                if (cartRs.next()) {
                    int cartId = cartRs.getInt("cart_id");
                    String deleteQuery = "DELETE FROM cart_products WHERE cart_id = ?";
                    PreparedStatement deleteStmt = db.con.prepareStatement(deleteQuery);
                    deleteStmt.setInt(1, cartId);
                    deleteStmt.executeUpdate();
                    
                    tableModel.setRowCount(0);
                    totalAmount = 0.0;
                    updateTotalLabel();
                    
                    notifyObservers();
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
            
            String cartQuery = "SELECT cart_id FROM carts WHERE user_id = ? AND cart_id NOT IN (SELECT cart_id FROM orders)";
            PreparedStatement cartStmt = db.con.prepareStatement(cartQuery);
            cartStmt.setInt(1, currentUser.getUserId());
            ResultSet cartRs = cartStmt.executeQuery();
            
            if (cartRs.next()) {
                int cartId = cartRs.getInt("cart_id");
                String deleteQuery = "DELETE FROM cart_products WHERE cart_id = ? AND product_id = ?";
                PreparedStatement deleteStmt = db.con.prepareStatement(deleteQuery);
                deleteStmt.setInt(1, cartId);
                deleteStmt.setInt(2, productId);
                deleteStmt.executeUpdate();
                tableModel.removeRow(selectedRow);
                calculateTotal();
                notifyObservers();
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.disconnect();
        }
    }

    public void addCartObserver(CartObserver observer) {
        observers.add(observer);
    }

    public void removeCartObserver(CartObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (CartObserver observer : observers) {
            observer.onCartUpdated();
        }
    }

    @Override
    public void onCartUpdated() {
        loadCartItems();
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