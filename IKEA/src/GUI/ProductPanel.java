package GUI;

import Model.CartObserver;
import Model.User;
import GUI.EditProfileFrame;
import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import Database.DatabaseManager;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProductPanel extends JPanel {

    private final DatabaseManager db = DatabaseManager.getInstance();
    private JPanel productsGrid;
    private User currentUser;
    private final List<CartObserver> observers = new ArrayList<>();
    private static final String IMAGE_PATH = "src/main/resources/product_images/";
    private static final int IMAGE_WIDTH = 200;
    private static final int IMAGE_HEIGHT = 200;
    private static final int CARDS_PER_ROW = 3;

    public ProductPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());

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

        JPanel headerPanel = createHeaderPanel(mainPanel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        productsGrid = new JPanel(new GridBagLayout());
        productsGrid.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(productsGrid);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel);
        loadProducts();
    }

    private JPanel createHeaderPanel(JPanel mainPanel) {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setOpaque(false);

        JLabel headerLabel = new JLabel("Available Products");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(new Color(248, 209, 21));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);

        JButton editProfileButton = createStyledButton("Edit Profile");
        editProfileButton.addActionListener(e -> openEditProfilePanel());
        buttonPanel.add(editProfileButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        JButton custHistoryButton = createStyledButton("Transaction History");
        custHistoryButton.addActionListener(e -> openTransactionHistoryPanel());

        buttonPanel.add(custHistoryButton);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchPanel.setOpaque(false);

        JTextField searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30));

        JButton searchButton = new JButton("Search");
        styleButton(searchButton);

        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        headerPanel.add(headerLabel, BorderLayout.NORTH);
        headerPanel.add(searchPanel, BorderLayout.CENTER);

        searchButton.addActionListener(e -> searchProducts(searchField.getText()));

        return headerPanel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        styleButton(button);
        return button;
    }

    private void openEditProfilePanel() {
        new EditProfileFrame(currentUser.getUserId(), currentUser.getUsername(), currentUser.getEmail());
    }

    private void openTransactionHistoryPanel() {
        new CustTransactionHistory(currentUser.getUserId());
    }

    private void loadProducts() {
        try {
            db.connect();
            String query = "SELECT * FROM products";
            PreparedStatement pstmt = db.con.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            productsGrid.removeAll();
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.anchor = GridBagConstraints.CENTER;

            while (rs.next()) {
                addProductCard(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getInt("stock_quantity"),
                        rs.getString("image_url"),
                        gbc
                );

                gbc.gridx++;
                if (gbc.gridx >= CARDS_PER_ROW) {
                    gbc.gridx = 0;
                    gbc.gridy++;
                }
            }

            rs.close();
            pstmt.close();
            productsGrid.revalidate();
            productsGrid.repaint();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.disconnect();
        }
    }

    private void addProductCard(int productId, String name, String description,
            double price, int stock, String imageUrl, GridBagConstraints gbc) {

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 240));
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            }
        };

        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(300, 450));

        JLabel imageLabel = new JLabel();
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imageLabel.setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));
        imageLabel.setMaximumSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));

        if (imageUrl != null && !imageUrl.isEmpty()) {
            File imageFile = new File(IMAGE_PATH + imageUrl);
            if (imageFile.exists()) {
                ImageIcon originalIcon = new ImageIcon(imageFile.getAbsolutePath());
                Image scaledImage = originalIcon.getImage().getScaledInstance(
                        IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImage));
            }
        }

        if (imageLabel.getIcon() == null) {
            imageLabel.setText("No Image Available");
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        }

        JLabel nameLabel = createStyledLabel(name, 16, Font.BOLD);
        JLabel priceLabel = createStyledLabel(String.format("Rp %.2f", price), 14, Font.BOLD);
        JLabel stockLabel = createStyledLabel("Stock: " + stock, 12, Font.PLAIN);

        JTextArea descArea = new JTextArea(description);
        descArea.setWrapStyleWord(true);
        descArea.setLineWrap(true);
        descArea.setOpaque(false);
        descArea.setEditable(false);
        descArea.setFocusable(false);
        descArea.setFont(new Font("Arial", Font.PLAIN, 12));
        descArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        descArea.setMaximumSize(new Dimension(250, 60));

        SpinnerModel spinnerModel = new SpinnerNumberModel(1, 1, stock, 1);
        JSpinner quantitySpinner = new JSpinner(spinnerModel);
        quantitySpinner.setMaximumSize(new Dimension(80, 25));
        quantitySpinner.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton addToCartBtn = new JButton("Add to Cart");
        styleButton(addToCartBtn);
        addToCartBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        addToCartBtn.addActionListener(e -> {
            if (currentUser == null) {
                JOptionPane.showMessageDialog(this,
                        "Please login to add items to cart!",
                        "Login Required",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            addToCart(productId, (Integer) quantitySpinner.getValue());
        });

        card.add(imageLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(nameLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(descArea);
        card.add(Box.createVerticalStrut(5));
        card.add(priceLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(stockLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(quantitySpinner);
        card.add(Box.createVerticalStrut(10));
        card.add(addToCartBtn);

        productsGrid.add(card, gbc);
    }

    private JLabel createStyledLabel(String text, int size, int style) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", style, size));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(new Color(0, 51, 153));
        button.setForeground(new Color(248, 209, 21));
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 30));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 71, 173));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 51, 153));
            }
        });
    }

    private void searchProducts(String searchTerm) {
        try {
            db.connect();
            String query = "SELECT * FROM products WHERE name LIKE ? OR description LIKE ?";
            PreparedStatement pstmt = db.con.prepareStatement(query);
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            productsGrid.removeAll();
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(10, 10, 10, 10);

            while (rs.next()) {
                addProductCard(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getInt("stock_quantity"),
                        rs.getString("image_url"),
                        gbc
                );

                gbc.gridx++;
                if (gbc.gridx >= CARDS_PER_ROW) {
                    gbc.gridx = 0;
                    gbc.gridy++;
                }
            }

            productsGrid.revalidate();
            productsGrid.repaint();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.disconnect();
        }
    }

    public void addObserver(CartObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(CartObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (CartObserver observer : observers) {
            observer.onCartUpdated();
        }
    }

    private void addToCart(int productId, int quantity) {
        try {
            db.connect();

            String cartQuery = "SELECT cart_id FROM carts WHERE user_id = ? AND cart_id NOT IN (SELECT cart_id FROM orders)";
            PreparedStatement cartStmt = db.con.prepareStatement(cartQuery);
            cartStmt.setInt(1, currentUser.getUserId());
            ResultSet cartRs = cartStmt.executeQuery();

            int cartId;
            if (cartRs.next()) {
                cartId = cartRs.getInt("cart_id");
            } else {
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

            String addProductQuery = "INSERT INTO cart_products (cart_id, product_id, quantity) VALUES (?, ?, ?) "
                    + "ON DUPLICATE KEY UPDATE quantity = quantity + ?";
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

            notifyObservers();

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
}
