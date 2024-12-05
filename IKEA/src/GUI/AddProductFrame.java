package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddProductFrame extends JFrame {
    public AddProductFrame() {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Frame configuration
        setTitle("IKEA Marketplace - Add Product");
        setSize(500, 600);
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
        JLabel titleLabel = new JLabel("Add New Product", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        // Product Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Product Name:");
        nameLabel.setForeground(Color.WHITE);
        formPanel.add(nameLabel, gbc);
        
        gbc.gridy = 1;
        JTextField nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(300, 40));
        formPanel.add(nameField, gbc);

        // Price
        gbc.gridy = 2;
        JLabel priceLabel = new JLabel("Price (Rp):");
        priceLabel.setForeground(Color.WHITE);
        formPanel.add(priceLabel, gbc);
        
        gbc.gridy = 3;
        JTextField priceField = new JTextField();
        priceField.setPreferredSize(new Dimension(300, 40));
        formPanel.add(priceField, gbc);

        // Stock
        gbc.gridy = 4;
        JLabel stockLabel = new JLabel("Stock:");
        stockLabel.setForeground(Color.WHITE);
        formPanel.add(stockLabel, gbc);
        
        gbc.gridy = 5;
        JTextField stockField = new JTextField();
        stockField.setPreferredSize(new Dimension(300, 40));
        formPanel.add(stockField, gbc);

        // Add Product Button
        gbc.gridy = 6;
        JButton addButton = new JButton("Add Product");
        addButton.setFont(new Font("Arial", Font.BOLD, 16));
        addButton.setPreferredSize(new Dimension(300, 50));
        addButton.setBackground(Color.WHITE);
        addButton.setForeground(new Color(0, 51, 153));
        addButton.setFocusPainted(false);
        addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        formPanel.add(addButton, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Add action listener
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Validate inputs
                if (validateInputs(nameField, priceField, stockField)) {
                    String name = nameField.getText();
                    String price = priceField.getText();
                    String stock = stockField.getText();

                    // Show confirmation dialog
                    int response = JOptionPane.showConfirmDialog(
                        AddProductFrame.this,
                        "Confirm Product Details:\n" +
                        "Name: " + name + "\n" +
                        "Price: Rp " + price + "\n" +
                        "Stock: " + stock,
                        "Confirm Product",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                    );

                    if (response == JOptionPane.YES_OPTION) {
                        JOptionPane.showMessageDialog(
                            AddProductFrame.this, 
                            "Product Added Successfully!", 
                            "Success", 
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        // Here you would typically add the product to your database or product list
                        clearFields(nameField, priceField, stockField);
                    }
                }
            }
        });

        add(mainPanel);
        setVisible(true);
    }

    // Input validation method
    private boolean validateInputs(JTextField nameField, JTextField priceField, JTextField stockField) {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Product Name cannot be empty!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocusInWindow();
            return false;
        }

        try {
            double price = Double.parseDouble(priceField.getText());
            if (price <= 0) {
                JOptionPane.showMessageDialog(this, "Price must be a positive number!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                priceField.requestFocusInWindow();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Price Format!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            priceField.requestFocusInWindow();
            return false;
        }

        try {
            int stock = Integer.parseInt(stockField.getText());
            if (stock < 0) {
                JOptionPane.showMessageDialog(this, "Stock cannot be negative!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                stockField.requestFocusInWindow();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Stock Format!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            stockField.requestFocusInWindow();
            return false;
        }

        return true;
    }

    // Clear input fields method
    private void clearFields(JTextField... fields) {
        for (JTextField field : fields) {
            field.setText("");
        }
    }
}