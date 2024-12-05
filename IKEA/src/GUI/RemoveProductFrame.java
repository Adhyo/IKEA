package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RemoveProductFrame extends JFrame {
    public RemoveProductFrame() {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Frame configuration
        setTitle("IKEA Marketplace - Remove Product");
        setSize(500, 400);
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
        JLabel titleLabel = new JLabel("Remove Product", JLabel.CENTER);
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

        // Product ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel idLabel = new JLabel("Product ID to Remove:");
        idLabel.setForeground(Color.WHITE);
        formPanel.add(idLabel, gbc);
        
        gbc.gridy = 1;
        JTextField idField = new JTextField();
        idField.setPreferredSize(new Dimension(300, 40));
        formPanel.add(idField, gbc);

        // Product List (Optional: You could populate this dynamically)
        gbc.gridy = 2;
        JLabel listLabel = new JLabel("Current Products:");
        listLabel.setForeground(Color.WHITE);
        formPanel.add(listLabel, gbc);

        gbc.gridy = 3;
        String[] columnNames = {"ID", "Name", "Price", "Stock"};
        Object[][] data = {
            {"1", "KALLAX Shelf", "Rp 1,299,000", "50"},
            {"2", "BILLY Bookcase", "Rp 1,799,000", "30"},
            {"3", "POÄNG Armchair", "Rp 2,499,000", "20"}
        };
        JTable productTable = new JTable(data, columnNames);
        productTable.setPreferredScrollableViewportSize(new Dimension(300, 100));
        JScrollPane scrollPane = new JScrollPane(productTable);
        formPanel.add(scrollPane, gbc);

        // Remove Product Button
        gbc.gridy = 4;
        JButton removeButton = new JButton("Remove Product");
        removeButton.setFont(new Font("Arial", Font.BOLD, 16));
        removeButton.setPreferredSize(new Dimension(300, 50));
        removeButton.setBackground(Color.WHITE);
        removeButton.setForeground(new Color(0, 51, 153));
        removeButton.setFocusPainted(false);
        removeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        formPanel.add(removeButton, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Add action listener
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String productId = idField.getText();

                // Validate input
                if (productId.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(
                        RemoveProductFrame.this, 
                        "Please enter a Product ID!", 
                        "Validation Error", 
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                // Confirmation Dialog
                int response = JOptionPane.showConfirmDialog(
                    RemoveProductFrame.this,
                    "Are you sure you want to remove Product ID: " + productId + "?",
                    "Confirm Removal",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );

                if (response == JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(
                        RemoveProductFrame.this, 
                        "Product with ID " + productId + " has been removed.", 
                        "Product Removed", 
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    // Here you would typically remove the product from your database or product list
                    idField.setText("");
                }
            }
        });

        add(mainPanel);
        setVisible(true);
    }
}