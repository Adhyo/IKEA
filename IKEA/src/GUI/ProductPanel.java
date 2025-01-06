package GUI;

import Model.User;
import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import Database.DatabaseManager;

public class ProductPanel extends JPanel {
    private final DatabaseManager db = DatabaseManager.getInstance();
    private JPanel productsGrid;
    private User currentUser;
    
    public ProductPanel(User user) {
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
        JLabel headerLabel = new JLabel("Available Products");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(new Color(248, 209, 21));
        headerPanel.add(headerLabel);

        // Products Grid Panel
        productsGrid = new JPanel(new GridLayout(0, 3, 15, 15));
        productsGrid.setOpaque(false);

        // Scroll Pane for Products
        JScrollPane scrollPane = new JScrollPane(productsGrid);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
        loadProducts();
    }

    private void loadProducts() {
        try {
            db.connect();
            String query = "SELECT * FROM products";
            PreparedStatement pstmt = db.con.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                addProductCard(
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDouble("price"),
                    rs.getInt("stock_quantity")
                );
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.disconnect();
        }
    }

    private void addProductCard(int productId, String name, String description, double price, int stock) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 200));
                g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
            }
        };
        card.setLayout(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        card.setOpaque(false);

        // Product Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel("<html><div style='text-align: center; width: 150px;'>" 
            + description + "</div></html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel priceLabel = new JLabel(String.format("$%.2f", price));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        priceLabel.setForeground(new Color(0, 51, 153));
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel stockLabel = new JLabel("Stock: " + stock);
        stockLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        stockLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Quantity spinner
        SpinnerModel spinnerModel = new SpinnerNumberModel(1, 1, stock, 1);
        JSpinner quantitySpinner = new JSpinner(spinnerModel);
        quantitySpinner.setMaximumSize(new Dimension(80, 25));
        quantitySpinner.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton addToCartBtn = createStyledButton("Add to Cart");
        addToCartBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add to cart action
        addToCartBtn.addActionListener(e -> {
            if (currentUser == null) {
                JOptionPane.showMessageDialog(this,
                    "Please login to add items to cart!",
                    "Login Required",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            int quantity = (Integer) quantitySpinner.getValue();
            addToCart(productId, quantity);
        });

        infoPanel.add(Box.createVerticalGlue());
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(descLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(priceLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(stockLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(quantitySpinner);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(addToCartBtn);
        infoPanel.add(Box.createVerticalGlue());

        card.add(infoPanel, BorderLayout.CENTER);
        productsGrid.add(card);
    }

    private void addToCart(int productId, int quantity) {
        try {
            db.connect();
            
            // Check if user has an active cart
            String cartQuery = "SELECT cart_id FROM carts WHERE user_id = ? AND cart_id NOT IN (SELECT cart_id FROM orders)";
            PreparedStatement cartStmt = db.con.prepareStatement(cartQuery);
            cartStmt.setInt(1, currentUser.getUserId());
            ResultSet cartRs = cartStmt.executeQuery();
            
            int cartId;
            if (cartRs.next()) {
                cartId = cartRs.getInt("cart_id");
            } else {
                // Create new cart
                String createCartQuery = "INSERT INTO carts (user_id) VALUES (?)";
                PreparedStatement createCartStmt = db.con.prepareStatement(createCartQuery, PreparedStatement.RETURN_GENERATED_KEYS);
                createCartStmt.setInt(1, currentUser.getUserId());
                createCartStmt.executeUpdate();
                
                ResultSet generatedKeys = createCartStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    cartId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Failed to create cart");
                }
            }
            
            // Add product to cart
            String addProductQuery = "INSERT INTO cart_products (cart_id, product_id, quantity) VALUES (?, ?, ?) " +
                                   "ON DUPLICATE KEY UPDATE quantity = quantity + ?";
            PreparedStatement addProductStmt = db.con.prepareStatement(addProductQuery);
            addProductStmt.setInt(1, cartId);
            addProductStmt.setInt(2, productId);
            addProductStmt.setInt(3, quantity);
            addProductStmt.setInt(4, quantity);
            addProductStmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this,
                "Product added to cart successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Failed to add product to cart: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            db.disconnect();
        }
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