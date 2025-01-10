package GUI;

import Database.DatabaseController;
import Database.DatabaseManager;
import Model.Category;
import Model.Product;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.nio.file.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddProductFrame extends JFrame {
    private DatabaseController dbController;
    private final DatabaseManager db = DatabaseManager.getInstance();
    private JComboBox<String> categoryComboBox;
    private List<Category> categories;
    private String selectedImagePath;
    private JLabel imagePreview;
    private final int PREVIEW_WIDTH = 200;
    private final int PREVIEW_HEIGHT = 200;
    private static final String UPLOAD_DIR = "src/main/resources/product_images/";
    
    // Add fields as class members for easy access
    private JTextField nameField;
    private JTextField descriptionField;
    private JTextField priceField;
    private JTextField stockField;

    public AddProductFrame() {
        dbController = new DatabaseController();
        categories = new ArrayList<>();
        
        // Ensure upload directory exists
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Frame configuration
        setTitle("IKEA Marketplace - Add Product");
        setSize(700, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize text fields
        nameField = createStyledTextField();
        descriptionField = createStyledTextField();
        priceField = createStyledTextField();
        stockField = createStyledTextField();

        // Main panel with IKEA-themed gradient background
        JPanel mainPanel = createGradientPanel();
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Title Label
        JLabel titleLabel = createStyledTitleLabel("Add New Product");
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Create main content panel with GridBagLayout
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Image Preview Panel
        JPanel imagePanel = new JPanel(new BorderLayout(10, 10));
        imagePanel.setOpaque(false);
        imagePreview = new JLabel();
        imagePreview.setPreferredSize(new Dimension(PREVIEW_WIDTH, PREVIEW_HEIGHT));
        imagePreview.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        imagePreview.setHorizontalAlignment(JLabel.CENTER);
        
        JButton uploadButton = createStyledButton("Upload Image", false);
        uploadButton.addActionListener(e -> handleImageUpload());
        
        imagePanel.add(imagePreview, BorderLayout.CENTER);
        imagePanel.add(uploadButton, BorderLayout.SOUTH);

        // Add image panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        contentPanel.add(imagePanel, gbc);

        // Reset gridwidth
        gbc.gridwidth = 1;

        // Add form fields using class member text fields
        addFormField(contentPanel, "Product Name:", nameField, gbc, 1);
        addFormField(contentPanel, "Description:", descriptionField, gbc, 2);
        addFormField(contentPanel, "Price (Rp):", priceField, gbc, 3);
        addFormField(contentPanel, "Stock Quantity:", stockField, gbc, 4);
        
        // Category ComboBox
        gbc.gridy = 5;
        gbc.gridx = 0;
        contentPanel.add(createStyledLabel("Category:"), gbc);
        categoryComboBox = createStyledComboBox();
        gbc.gridx = 1;
        contentPanel.add(categoryComboBox, gbc);
        loadCategories();

        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        JButton addButton = createStyledButton("Add Product", true);
        JButton cancelButton = createStyledButton("Cancel", false);
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        // Add action listeners
        addButton.addActionListener(e -> {
            if (selectedImagePath == null) {
                JOptionPane.showMessageDialog(this,
                    "Please upload a product image!",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            handleAddProduct();
        });
        cancelButton.addActionListener(e -> dispose());

        // Wrap content panel in scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);

        // Add panels to main panel
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private void handleImageUpload() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            selectedImagePath = selectedFile.getAbsolutePath();
            
            // Update preview
            ImageIcon originalIcon = new ImageIcon(selectedImagePath);
            Image image = originalIcon.getImage().getScaledInstance(
                PREVIEW_WIDTH, PREVIEW_HEIGHT, Image.SCALE_SMOOTH);
            imagePreview.setIcon(new ImageIcon(image));
        }
    }

    private String saveImage(String sourcePath) {
        try {
            String fileName = UUID.randomUUID().toString() + getFileExtension(sourcePath);
            String destinationPath = UPLOAD_DIR + fileName;
            Files.copy(Paths.get(sourcePath), Paths.get(destinationPath), 
                      StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getFileExtension(String path) {
        String extension = "";
        int i = path.lastIndexOf('.');
        if (i > 0) {
            extension = path.substring(i);
        }
        return extension;
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

    private void addFormField(JPanel panel, String labelText, JTextField field, GridBagConstraints gbc, int row) {
        // Add label
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(createStyledLabel(labelText), gbc);
        
        // Add text field
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(field, gbc);
    }

    private JTextField[] getAllFormFields() {
        JPanel contentPanel = (JPanel) ((JScrollPane) getContentPane()
            .getComponent(0)).getViewport().getView();
        
        JTextField[] fields = new JTextField[8];
        int fieldIndex = 0;
        
        // Get all JTextFields from the content panel
        for (Component comp : contentPanel.getComponents()) {
            if (comp instanceof JTextField) {
                fields[fieldIndex++] = (JTextField) comp;
            }
        }
        
        return fields;
    }

    private void handleAddProduct() {
        if (validateInputs(nameField, priceField, stockField)) {
            try {
                String imageFileName = saveImage(selectedImagePath);
                if (imageFileName == null) {
                    JOptionPane.showMessageDialog(this,
                        "Failed to save image!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String name = nameField.getText().trim();
                String description = descriptionField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                int stock = Integer.parseInt(stockField.getText().trim());
                
                String selectedCategory = (String) categoryComboBox.getSelectedItem();
                Category category = getCategoryByName(selectedCategory);

                Product product = new Product(
                    0, name, description, price, stock, category, 0.0
                );

                if (addProductToDatabase(product, imageFileName)) {
                    JOptionPane.showMessageDialog(this, 
                        "Product added successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    clearFields();
                    imagePreview.setIcon(null);
                    selectedImagePath = null;
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
                    rs.getInt("category_id"),
                    rs.getString("category_name")
                );
                categories.add(category);
                categoryComboBox.addItem(category.getCategoryName());
            }

            if (categories.isEmpty()) {
                Category defaultCategory = new Category(0, "Default");
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
                .orElse(new Category(0, "Default"));
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

    private void clearFields() {
        nameField.setText("");
        descriptionField.setText("");
        priceField.setText("");
        stockField.setText("");
        categoryComboBox.setSelectedIndex(0);
    }

    private boolean addProductToDatabase(Product product, String imageFileName) {
        String sql = "INSERT INTO products (name, description, price, stock_quantity, " +
                    "category_id, discount_price, image_url) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try {
            db.connect();
            PreparedStatement pstmt = db.con.prepareStatement(sql);
            
            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getDescription());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setInt(4, product.getStockQuantity());
            pstmt.setInt(5, product.getCategory().getCategoryId()); 
            pstmt.setDouble(6, product.getDiscountPrice());
            pstmt.setString(7, imageFileName);

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