package GUI;

import Database.DatabaseController;
import Database.DatabaseManager;
import Model.Category;
import Model.Product;
import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddProductFrame extends JFrame {
    private DatabaseController dbController;
    private final DatabaseManager db = DatabaseManager.getInstance();
    private JComboBox<String> categoryComboBox;
    private List<Category> categories;

    public AddProductFrame() {
        dbController = new DatabaseController();
        categories = new ArrayList<>();

        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Frame configuration
        setTitle("IKEA Marketplace - Add Product");
        setSize(500, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel with IKEA-themed gradient background
        JPanel mainPanel = createGradientPanel();
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Title Label
        JLabel titleLabel = createStyledTitleLabel("Add New Product");
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 20));
        formPanel.setOpaque(false);

        // Create form components
        JLabel nameLabel = createStyledLabel("Product Name:");
        JTextField nameField = createStyledTextField();
        
        JLabel descLabel = createStyledLabel("Description:");
        JTextField descField = createStyledTextField();
        
        JLabel priceLabel = createStyledLabel("Price (Rp):");
        JTextField priceField = createStyledTextField();
        
        JLabel stockLabel = createStyledLabel("Stock Quantity:");
        JTextField stockField = createStyledTextField();

        JLabel categoryLabel = createStyledLabel("Category:");
        categoryComboBox = createStyledComboBox();
        loadCategories();
        
        JLabel weightLabel = createStyledLabel("Weight (kg):");
        JTextField weightField = createStyledTextField();
        
        JLabel colorLabel = createStyledLabel("Color:");
        JTextField colorField = createStyledTextField();
        
        JLabel dimensionsLabel = createStyledLabel("Dimensions:");
        JTextField dimensionsField = createStyledTextField();

        // Add components to form panel
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(descLabel);
        formPanel.add(descField);
        formPanel.add(priceLabel);
        formPanel.add(priceField);
        formPanel.add(stockLabel);
        formPanel.add(stockField);
        formPanel.add(categoryLabel);
        formPanel.add(categoryComboBox);
        formPanel.add(weightLabel);
        formPanel.add(weightField);
        formPanel.add(colorLabel);
        formPanel.add(colorField);
        formPanel.add(dimensionsLabel);
        formPanel.add(dimensionsField);

        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        JButton addButton = createStyledButton("Add Product", true);
        JButton cancelButton = createStyledButton("Cancel", false);
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        // Add panels to main panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(formPanel, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Add action listeners
        addButton.addActionListener(e -> handleAddProduct(nameField, descField, priceField, stockField, weightField, colorField, dimensionsField));
        cancelButton.addActionListener(e -> dispose());

        // Add main panel to frame
        add(mainPanel);
        setVisible(true);
    }

    private JPanel createGradientPanel() {
        return new JPanel() {
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
    }

    private JLabel createStyledTitleLabel(String text) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setForeground(Color.WHITE);
        return label;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(Color.WHITE);
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(200, 30));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return field;
    }

    private JComboBox<String> createStyledComboBox() {
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        comboBox.setPreferredSize(new Dimension(200, 30));
        comboBox.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        comboBox.setBackground(Color.WHITE);
        return comboBox;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setOpaque(false);
        return buttonPanel;
    }

    private JButton createStyledButton(String text, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(150, 40));
        
        if (isPrimary) {
            button.setBackground(new Color(0, 51, 153));
            button.setForeground(new Color(248, 209, 21)); // IKEA Yellow
        } else {
            button.setBackground(new Color(0, 51, 153));
            button.setForeground(new Color(4, 52, 140));
        }
        
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 71, 173));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 51, 153));
            }
        });
        
        return button;
    }

    private void handleAddProduct(JTextField nameField, JTextField descField, 
            JTextField priceField, JTextField stockField, JTextField weightField, 
            JTextField colorField, JTextField dimensionsField) {
        
        if (validateInputs(nameField, priceField, stockField)) {
            try {
                String name = nameField.getText().trim();
                String description = descField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                int stock = Integer.parseInt(stockField.getText().trim());
                Double weight = weightField.getText().isEmpty() ? null : Double.parseDouble(weightField.getText().trim());
                String color = colorField.getText().isEmpty() ? null : colorField.getText().trim();
                String dimensions = dimensionsField.getText().isEmpty() ? null : dimensionsField.getText().trim();
                
                String selectedCategory = (String) categoryComboBox.getSelectedItem();
                Category category = getCategoryByName(selectedCategory);

                Product product = new Product(
                    0,
                    name,
                    description,
                    price,
                    stock,
                    category,
                    weight != null ? weight : 0.0,
                    color,
                    dimensions,
                    0.0
                );

                if (addProductToDatabase(product)) {
                    JOptionPane.showMessageDialog(this, 
                        "Product added successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    clearFields(nameField, descField, priceField, stockField, weightField, colorField, dimensionsField);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to add product. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    "Please enter valid numeric values.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadCategories() {
        try {
            db.connect();
            String query = "SELECT * FROM categories";
            PreparedStatement pstmt = db.con.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            categories.clear();
            categoryComboBox.removeAllItems();

            while (rs.next()) {
                Category category = new Category(
                    rs.getString("category_id"),
                    rs.getString("category_name")
                );
                categories.add(category);
                categoryComboBox.addItem(category.getCategoryName());
            }

            if (categories.isEmpty()) {
                Category defaultCategory = new Category("0", "Default");
                categories.add(defaultCategory);
                categoryComboBox.addItem(defaultCategory.getCategoryName());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.disconnect();
        }
    }

    private Category getCategoryByName(String categoryName) {
        return categories.stream()
                .filter(c -> c.getCategoryName().equals(categoryName))
                .findFirst()
                .orElse(new Category("0", "Default"));
    }

    private boolean validateInputs(JTextField nameField, JTextField priceField, JTextField stockField) {
        if (nameField.getText().trim().isEmpty()) {
            showError("Product Name cannot be empty!", nameField);
            return false;
        }

        try {
            double price = Double.parseDouble(priceField.getText().trim());
            if (price <= 0) {
                showError("Price must be a positive number!", priceField);
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Invalid Price Format!", priceField);
            return false;
        }

        try {
            int stock = Integer.parseInt(stockField.getText().trim());
            if (stock < 0) {
                showError("Stock cannot be negative!", stockField);
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Invalid Stock Format!", stockField);
            return false;
        }

        return true;
    }

    private void showError(String message, JTextField field) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
        field.requestFocusInWindow();
    }

    private void clearFields(JTextField... fields) {
        for (JTextField field : fields) {
            field.setText("");
        }
    }

    private boolean addProductToDatabase(Product product) {
        String sql = "INSERT INTO products (name, description, price, stock_quantity, category_id, weight, color, dimensions, discount_price) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try {
            db.connect();
            PreparedStatement pstmt = db.con.prepareStatement(sql);
            
            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getDescription());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setInt(4, product.getStockQuantity());
            pstmt.setString(5, product.getCategory().getCategoryID());
            pstmt.setDouble(6, product.getWeight());
            pstmt.setString(7, product.getColor());
            pstmt.setString(8, product.getDimensions());
            pstmt.setDouble(9, product.getDiscountPrice());

            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            db.disconnect();
        }
    }
}