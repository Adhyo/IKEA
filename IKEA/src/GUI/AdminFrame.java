package GUI;

import java.awt.*;
import javax.swing.*;

public class AdminFrame extends JFrame {
    private JPanel buttonPanel;

    public AdminFrame() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("IKEA Marketplace - Admin Dashboard");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

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

        JLabel welcomeLabel = new JLabel("Welcome, Admin!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);

        // Updated GridLayout to accommodate 6 buttons (5 original + 1 new)
        buttonPanel = new JPanel(new GridLayout(6, 1, 10, 20));
        buttonPanel.setOpaque(false);

        JButton addProductButton = createStyledButton("Add Product", true);
        JButton removeProductButton = createStyledButton("Remove Product", true);
        JButton manageUsersButton = createStyledButton("Manage Users", true);
        JButton managePromosButton = createStyledButton("Manage Promos", true);
        JButton notificationsButton = createStyledButton("Send Notifications", true);

        buttonPanel.add(addProductButton);
        buttonPanel.add(removeProductButton);
        buttonPanel.add(manageUsersButton);
        buttonPanel.add(managePromosButton);
        buttonPanel.add(notificationsButton);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        JButton logoutButton = createLogoutButton();
        mainPanel.add(logoutButton, BorderLayout.SOUTH);

        addProductButton.addActionListener(e -> new AddProductFrame());
        removeProductButton.addActionListener(e -> new RemoveProductFrame());
        manageUsersButton.addActionListener(e -> new ManageUserFrame());
        managePromosButton.addActionListener(e -> new PromoManagementFrame());
        notificationsButton.addActionListener(e -> new AdminNotificationForm());

        add(mainPanel);
        setVisible(true);
        addTransactionHistoryButton();
    }

    private void addTransactionHistoryButton() {
        JButton transactionHistoryButton = createStyledButton("Transaction History", true);
        buttonPanel.add(transactionHistoryButton);

        transactionHistoryButton.addActionListener(e -> new TransactionHistoryFrame());

        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    private JButton createStyledButton(String text, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(200, 50));

        if (isPrimary) {
            button.setBackground(new Color(0, 51, 153));
            button.setForeground(new Color(248, 209, 21));
            button.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        } else {
            button.setBackground(new Color(0, 51, 153));
            button.setForeground(new Color(4, 52, 140));
            button.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        }

        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createLogoutButton() {
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton.setPreferredSize(new Dimension(200, 40));
        logoutButton.setBackground(new Color(0, 51, 153));
        logoutButton.setForeground(new Color(4, 52, 140));
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        return logoutButton;
    }
}