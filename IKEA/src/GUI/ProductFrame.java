package GUI;

import Model.CartObserver;
import Model.Category;
import Model.Product;
import Model.User;
import Database.DatabaseController;
import Database.DatabaseManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductFrame extends JFrame {
    private Category category;
    private final List<CartObserver> observers = new ArrayList<>();
    private DatabaseController dbController;
    private DatabaseManager db;
    private User currentUser;

    public ProductFrame(Category category, User user) {
        this.category = category;
        this.currentUser = user;
        this.dbController = new DatabaseController();
        this.db = DatabaseManager.getInstance();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("IKEA Marketplace - " + category.getCategoryName());
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(0, 51, 153),
                        getWidth(), getHeight(), new Color(0, 105, 255));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(new BorderLayout(20, 20));
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        createCustomMenuBar();

        JPanel productPanel = new JPanel(new GridLayout(0, 2, 20, 20));
        productPanel.setOpaque(false);

        List<Product> products = dbController.getProductsForCategory(category);

        for (Product product : products) {
            JPanel productCard = createProductCard(product);
            productPanel.add(productCard);
        }

        JScrollPane scrollPane = new JScrollPane(productPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        backgroundPanel.add(scrollPane, BorderLayout.CENTER);

        JLabel categoryLabel = new JLabel(category.getCategoryName(), SwingConstants.CENTER);
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 28));
        categoryLabel.setForeground(new Color(248, 209, 21));
        categoryLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        backgroundPanel.add(categoryLabel, BorderLayout.NORTH);

        add(backgroundPanel);
        setVisible(true);
    }

    private JPanel createProductCard(Product product) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(10, 10));
        card.setBackground(new Color(255, 255, 255, 30));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(248, 209, 21), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        card.setPreferredSize(new Dimension(400, 500));

        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon productImage = loadProductImage(product);
        if (productImage != null) {
            imageLabel.setIcon(productImage);
        }
        card.add(imageLabel, BorderLayout.NORTH);

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        detailsPanel.add(nameLabel);
        detailsPanel.add(Box.createVerticalStrut(10));

        JTextArea descArea = new JTextArea(product.getDescription());
        descArea.setWrapStyleWord(true);
        descArea.setLineWrap(true);
        descArea.setOpaque(false);
        descArea.setEditable(false);
        descArea.setForeground(Color.WHITE);
        descArea.setFont(new Font("Arial", Font.PLAIN, 14));
        descArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        detailsPanel.add(descArea);
        detailsPanel.add(Box.createVerticalStrut(10));

        JLabel priceLabel = new JLabel(String.format("Price: $%.2f", product.getPrice()));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        priceLabel.setForeground(Color.WHITE);
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        detailsPanel.add(priceLabel);
        detailsPanel.add(Box.createVerticalStrut(10));

        card.add(detailsPanel, BorderLayout.CENTER);

        JButton addToCartButton = new JButton("Add to Cart");
        styleButton(addToCartButton);
        addToCartButton.addActionListener(e -> {
            if (currentUser == null) {
                JOptionPane.showMessageDialog(this,
                        "Please login to add items to cart",
                        "Login Required",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

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
                    PreparedStatement createCartStmt = db.con.prepareStatement(createCartQuery,
                            PreparedStatement.RETURN_GENERATED_KEYS);
                    createCartStmt.setInt(1, currentUser.getUserId());
                    createCartStmt.executeUpdate();

                    ResultSet generatedKeys = createCartStmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        cartId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Failed to create cart");
                    }
                }

                String addProductQuery = "INSERT INTO cart_products (cart_id, product_id, quantity) VALUES (?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE quantity = quantity + ?";
                PreparedStatement addProductStmt = db.con.prepareStatement(addProductQuery);
                addProductStmt.setInt(1, cartId);
                addProductStmt.setInt(2, product.getProductId());
                addProductStmt.setInt(3, 1);
                addProductStmt.setInt(4, 1);
                addProductStmt.executeUpdate();

                JOptionPane.showMessageDialog(this,
                        "Product added to cart successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                notifyObservers();

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error adding product to cart: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } finally {
                db.disconnect();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(addToCartButton);
        card.add(buttonPanel, BorderLayout.SOUTH);

        return card;
    }

    private ImageIcon loadProductImage(Product product) {
        try {
            String imagePath = "/images/products/" + product.getProductId() + ".jpg";
            java.net.URL imageURL = getClass().getResource(imagePath);

            if (imageURL != null) {
                ImageIcon originalIcon = new ImageIcon(imageURL);
                Image scaledImage = originalIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            }

            return createPlaceholder(200, 200);
        } catch (Exception e) {
            return createPlaceholder(200, 200);
        }
    }

    private void notifyObservers() {
        for (CartObserver observer : observers) {
            observer.onCartUpdated();
        }
    }

    private ImageIcon createPlaceholder(int width, int height) {
        JPanel placeholderPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(200, 200, 200));
                g.fillRect(0, 0, width, height);
                g.setColor(new Color(0, 51, 153));
                g.setFont(new Font("Arial", Font.BOLD, 24));
                String text = "IKEA";
                FontMetrics fm = g.getFontMetrics();
                g.drawString(text,
                        (width - fm.stringWidth(text)) / 2,
                        (height + fm.getAscent()) / 2);
            }
        };
        placeholderPanel.setPreferredSize(new Dimension(width, height));

        placeholderPanel.setSize(width, height);
        placeholderPanel.setBackground(Color.WHITE);

        Image image = new java.awt.image.BufferedImage(
                width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        placeholderPanel.paint(g2d);
        g2d.dispose();

        return new ImageIcon(image);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(new Color(248, 209, 21));
        button.setBackground(new Color(0, 51, 153));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 71, 173));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 51, 153));
            }
        });
    }

    private void createCustomMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(0, 51, 153));
        menuBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel titleLabel = new JLabel("IKEA Marketplace");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(248, 209, 21));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 20));
        menuBar.add(titleLabel);

        menuBar.add(createMenuButton("Categories", KeyEvent.VK_C, e -> {
            new CategoryFrame(currentUser);
            dispose();
        }));

        menuBar.add(createMenuButton("Home", KeyEvent.VK_H, e -> {
            new MainFrame(currentUser);
            dispose();
        }));

        if (currentUser != null) {
            menuBar.add(createMenuButton("Cart", KeyEvent.VK_T, e -> {
                MainFrame mainFrame = MainFrame.getInstance();
                if (mainFrame != null) {
                    mainFrame.showCartPanel();
                }
            }));
        }

        menuBar.add(Box.createHorizontalGlue());
        setJMenuBar(menuBar);
    }

    private JButton createMenuButton(String text, int mnemonic, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(new Color(4, 52, 140));
        button.setBackground(new Color(0, 51, 153));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setMnemonic(mnemonic);
        if (listener != null) {
            button.addActionListener(listener);
        }

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(new Color(248, 209, 21));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(new Color(4, 52, 140));
            }
        });

        return button;
    }
}