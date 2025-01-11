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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CheckoutPanel extends JPanel implements CartObserver {
    private final DatabaseManager db = DatabaseManager.getInstance();
    private final User currentUser;
    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JLabel subtotalLabel;
    private JLabel discountLabel;
    private JLabel finalTotalLabel;
    private double subtotalAmount = 0.0;
    private double discountAmount = 0.0;
    private double finalAmount = 0.0;
    private JTextArea addressArea;
    private JComboBox<String> promoComboBox;
    private Map<String, Double> promoDiscounts = new HashMap<>();
    private Map<String, String> promoDescriptions = new HashMap<>();

    public CheckoutPanel(User user) {
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
        JPanel headerPanel = createHeaderPanel();

        // Cart Table Panel
        JPanel cartPanel = createCartPanel();

        // Checkout Details Panel
        JPanel checkoutPanel = createCheckoutPanel();

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(cartPanel, BorderLayout.CENTER);
        mainPanel.add(checkoutPanel, BorderLayout.SOUTH);

        add(mainPanel);
        loadCartItems();
        loadPromos();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setOpaque(false);
        JLabel headerLabel = new JLabel("Checkout");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(new Color(248, 209, 21));
        headerPanel.add(headerLabel);
        return headerPanel;
    }

    private JPanel createCartPanel() {
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
        
        JScrollPane scrollPane = new JScrollPane(cartTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setOpaque(false);
        cartPanel.add(scrollPane, BorderLayout.CENTER);
        return cartPanel;
    }

    private JPanel createCheckoutPanel() {
        JPanel checkoutPanel = new JPanel(new BorderLayout(10, 10));
        checkoutPanel.setOpaque(false);

        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setOpaque(false);
        JLabel addressLabel = new JLabel("Delivery Address:");
        addressLabel.setForeground(new Color(248, 209, 21));
        addressArea = new JTextArea(4, 30);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        JScrollPane addressScroll = new JScrollPane(addressArea);
        leftPanel.add(addressLabel, BorderLayout.NORTH);
        leftPanel.add(addressScroll, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);

        JPanel promoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        promoPanel.setOpaque(false);
        JLabel promoLabel = new JLabel("Select Promo:");
        promoLabel.setForeground(new Color(248, 209, 21));
        promoComboBox = new JComboBox<>();
        promoComboBox.addItem("No Promo");
        promoComboBox.addActionListener(e -> applyPromo());
        promoPanel.add(promoLabel);
        promoPanel.add(promoComboBox);

        JPanel totalsPanel = new JPanel();
        totalsPanel.setLayout(new BoxLayout(totalsPanel, BoxLayout.Y_AXIS));
        totalsPanel.setOpaque(false);
        
        subtotalLabel = new JLabel("Subtotal: $0.00");
        discountLabel = new JLabel("Discount: $0.00");
        finalTotalLabel = new JLabel("Total: $0.00");
        
        for (JLabel label : new JLabel[]{subtotalLabel, discountLabel, finalTotalLabel}) {
            label.setFont(new Font("Arial", Font.BOLD, 16));
            label.setForeground(new Color(248, 209, 21));
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            totalsPanel.add(label);
            totalsPanel.add(Box.createVerticalStrut(5));
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        JButton confirmButton = createStyledButton("Confirm Order");
        JButton cancelButton = createStyledButton("Cancel");
        
        confirmButton.addActionListener(e -> handleConfirmOrder());
        cancelButton.addActionListener(e -> handleCancel());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);

        rightPanel.add(promoPanel);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(totalsPanel);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(buttonPanel);

        checkoutPanel.add(leftPanel, BorderLayout.WEST);
        checkoutPanel.add(rightPanel, BorderLayout.EAST);
        
        return checkoutPanel;
    }

    private void loadPromos() {
        try {
            db.connect();
            String query = "SELECT * FROM promos WHERE expiration_date >= CURRENT_DATE";
            PreparedStatement pstmt = db.con.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String promoId = rs.getString("promo_id");
                String promoName = rs.getString("promo_name");
                String description = rs.getString("description");
                double discount = rs.getDouble("discount");

                promoComboBox.addItem(promoName);
                promoDiscounts.put(promoName, discount);
                promoDescriptions.put(promoName, description);
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
            subtotalAmount = 0.0;

            while (rs.next()) {
                int productId = rs.getInt("product_id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");
                double subtotal = price * quantity;
                subtotalAmount += subtotal;

                tableModel.addRow(new Object[]{
                    productId, name, price, quantity, subtotal
                });
            }

            updateTotals();
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.disconnect();
        }
    }

    private void applyPromo() {
        String selectedPromo = (String) promoComboBox.getSelectedItem();
        if (selectedPromo == null || selectedPromo.equals("No Promo")) {
            discountAmount = 0.0;
        } else {
            double discountPercentage = promoDiscounts.get(selectedPromo);
            discountAmount = subtotalAmount * (discountPercentage / 100);
        }
        updateTotals();
    }

    private void updateTotals() {
        finalAmount = subtotalAmount - discountAmount;
        subtotalLabel.setText(String.format("Subtotal: $%.2f", subtotalAmount));
        discountLabel.setText(String.format("Discount: $%.2f", discountAmount));
        finalTotalLabel.setText(String.format("Total: $%.2f", finalAmount));
    }

    private void handleConfirmOrder() {
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
    
        try {
            db.connect();
    
            String cartQuery = "SELECT cart_id FROM carts WHERE user_id = ? AND cart_id NOT IN (SELECT cart_id FROM orders)";
            PreparedStatement cartStmt = db.con.prepareStatement(cartQuery);
            cartStmt.setInt(1, currentUser.getUserId());
            ResultSet cartRs = cartStmt.executeQuery();
    
            if (cartRs.next()) {
                int cartId = cartRs.getInt("cart_id");
                
                String orderQuery = "INSERT INTO orders (cart_id, address, price, status, promo_applied) VALUES (?, ?, ?, 'UNPAID', ?)";
                PreparedStatement orderStmt = db.con.prepareStatement(orderQuery, PreparedStatement.RETURN_GENERATED_KEYS);
                orderStmt.setInt(1, cartId);
                orderStmt.setString(2, addressArea.getText().trim());
                orderStmt.setDouble(3, finalAmount);
                orderStmt.setString(4, (String) promoComboBox.getSelectedItem());
                orderStmt.executeUpdate();
                ResultSet orderRs = orderStmt.getGeneratedKeys();
                if (orderRs.next()) {
                    int orderId = orderRs.getInt(1);
                    String transQuery = "INSERT INTO transactions (cart_id, user_id, sub_total, discount_amount, final_amount, transaction_date) VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement transStmt = db.con.prepareStatement(transQuery);
                    transStmt.setInt(1, cartId);
                    transStmt.setInt(2, currentUser.getUserId());
                    transStmt.setDouble(3, subtotalAmount);
                    transStmt.setDouble(4, discountAmount);
                    transStmt.setDouble(5, finalAmount);
                    transStmt.setDate(6, java.sql.Date.valueOf(LocalDate.now()));
                    transStmt.executeUpdate();
                    PaymentFrame paymentFrame = new PaymentFrame(currentUser, orderId, finalAmount);
                    paymentFrame.setVisible(true);
                    clearCheckout();
                }
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

    private void handleCancel() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel checkout?",
            "Confirm Cancel",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            clearCheckout();
        }
    }

    private void clearCheckout() {
        tableModel.setRowCount(0);
        addressArea.setText("");
        promoComboBox.setSelectedItem("No Promo");
        subtotalAmount = 0.0;
        discountAmount = 0.0;
        finalAmount = 0.0;
        updateTotals();
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