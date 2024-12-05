package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminFrame extends JFrame {
    public AdminFrame() {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Frame configuration
        setTitle("IKEA Marketplace - Admin Dashboard");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

        // Welcome Label
        JLabel welcomeLabel = new JLabel("Welcome, Admin!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 20));
        buttonPanel.setOpaque(false);

        // Create custom styled buttons
        JButton addProductButton = createStyledButton("Add Product", true);
        JButton removeProductButton = createStyledButton("Remove Product", false);
        JButton manageUsersButton = createStyledButton("Manage Users", true);

        buttonPanel.add(addProductButton);
        buttonPanel.add(removeProductButton);
        buttonPanel.add(manageUsersButton);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // Logout Button
        JButton logoutButton = createLogoutButton();
        mainPanel.add(logoutButton, BorderLayout.SOUTH);

        // Action Listeners
        addProductButton.addActionListener(e -> new AddProductFrame());

        removeProductButton.addActionListener(e -> new RemoveProductFrame());

        manageUsersButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "User Management Feature\nComing Soon!", 
                "Feature Preview", 
                JOptionPane.INFORMATION_MESSAGE);
        });

        // Add main panel to frame
        add(mainPanel);
        setVisible(true);
    }

    // Custom styled button method
    private JButton createStyledButton(String text, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(200, 50));
        
        if (isPrimary) {
            button.setBackground(Color.WHITE);
            button.setForeground(new Color(0, 51, 153));
        } else {
            button.setBackground(new Color(0, 51, 153));
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        }
        
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    // Logout button with special styling
    private JButton createLogoutButton() {
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton.setPreferredSize(new Dimension(200, 40));
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });
        
        return logoutButton;
    }
}