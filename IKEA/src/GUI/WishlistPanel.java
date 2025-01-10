package GUI;

import Model.User;
import Database.DatabaseManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WishlistPanel extends JPanel {
    private final DatabaseManager db = DatabaseManager.getInstance();
    private final User currentUser;
    private JTable wishlistTable;
    private DefaultTableModel tableModel;
    private JPanel mainPanel;

    public WishlistPanel(User user) {
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

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setOpaque(false);
        JLabel headerLabel = new JLabel("My Wishlist");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(new Color(248, 209, 21));
        headerPanel.add(headerLabel);

        // Table
        createTablePanel();
        JScrollPane scrollPane = new JScrollPane(wishlistTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);

        JButton removeButton = createStyledButton("Remove from Wishlist");
        JButton addToCartButton = createStyledButton("Add to Cart");

        removeButton.addActionListener(e -> removeFromWishlist());
        addToCartButton.addActionListener(e -> addToCart());

        buttonPanel.add(removeButton);
        buttonPanel.add(addToCartButton);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        loadWishlistItems();
    }

    private void createTablePanel() {
        String[] columnNames = {"Product ID", "Name", "Price", "Description"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        wishlistTable = new JTable(tableModel);
        wishlistTable.setForeground(Color.BLACK);
        wishlistTable.setFont(new Font("Arial", Font.PLAIN, 14));
        wishlistTable.setRowHeight(25);
        wishlistTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        wishlistTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public void loadWishlistItems() {
        try {
            db.connect();
            String query = "SELECT p.product_id, p.name, p.price, p.description " +
                          "FROM wishlists w " +
                          "JOIN products p ON w.product_id = p.product_id " +
                          "WHERE w.user_id = ?";

            PreparedStatement pstmt = db.con.prepareStatement(query);
            pstmt.setInt(1, currentUser.getUserId());
            ResultSet rs = pstmt.executeQuery();

            tableModel.setRowCount(0);

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getString("description")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading wishlist: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            db.disconnect();
        }
    }

    private void removeFromWishlist() {
        int selectedRow = wishlistTable.getSelectedRow();
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

            String query = "DELETE FROM wishlists WHERE user_id = ? AND product_id = ?";
            PreparedStatement pstmt = db.con.prepareStatement(query);
            pstmt.setInt(1, currentUser.getUserId());
            pstmt.setInt(2, productId);

            pstmt.executeUpdate();
            tableModel.removeRow(selectedRow);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error removing item: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            db.disconnect();
        }
    }

    private void addToCart() {
        int selectedRow = wishlistTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select an item to add to cart",
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
                    throw new SQLException("Failed to create new cart");
                }
            }

            String addQuery = "INSERT INTO cart_products (cart_id, product_id, quantity) VALUES (?, ?, 1) " +
                            "ON DUPLICATE KEY UPDATE quantity = quantity + 1";
            PreparedStatement addStmt = db.con.prepareStatement(addQuery);
            addStmt.setInt(1, cartId);
            addStmt.setInt(2, productId);
            addStmt.executeUpdate();

            JOptionPane.showMessageDialog(this,
                "Item added to cart successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error adding to cart: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            db.disconnect();
        }
    }

    public void addNavigationButton() {
        JButton backToProducts = createStyledButton("Back to Products");
        backToProducts.addActionListener(e -> {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (parentFrame != null) {
                parentFrame.getContentPane().removeAll();
                parentFrame.add(new ProductPanel(currentUser));
                parentFrame.revalidate();
                parentFrame.repaint();
            }
        });
        
        // Add the button to the existing button panel
        Component[] components = mainPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel && ((JPanel) comp).getLayout() instanceof FlowLayout) {
                ((JPanel) comp).add(backToProducts);
                break;
            }
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
        button.setPreferredSize(new Dimension(150, 30));
        return button;
    }
}
