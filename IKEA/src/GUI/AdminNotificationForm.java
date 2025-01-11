package GUI;

import Database.DatabaseController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AdminNotificationForm extends JFrame {
    private DatabaseController dbController;
    private JTextField userNameField;
    private JTextArea messageArea;
    private JComboBox<String> userComboBox;

    public AdminNotificationForm() {
        dbController = new DatabaseController();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("IKEA Marketplace - Admin Notifications");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Panel with gradient background
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
        JLabel headerLabel = new JLabel("Send Notification");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(new Color(248, 209, 21));
        headerPanel.add(headerLabel);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        // User Selection
        JLabel userLabel = new JLabel("Select User:");
        userLabel.setForeground(Color.WHITE);
        userComboBox = new JComboBox<>();
        loadUsers();
        
        gbc.gridy = 0;
        formPanel.add(userLabel, gbc);
        gbc.gridy = 1;
        formPanel.add(userComboBox, gbc);

        JLabel messageLabel = new JLabel("Message:");
        messageLabel.setForeground(Color.WHITE);
        messageArea = new JTextArea(5, 30);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(messageArea);

        gbc.gridy = 2;
        formPanel.add(messageLabel, gbc);
        gbc.gridy = 3;
        formPanel.add(scrollPane, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setOpaque(false);
        JButton sendButton = createStyledButton("Send Notification", true);
        JButton clearButton = createStyledButton("Clear", false);

        sendButton.addActionListener(e -> handleSendNotification());
        clearButton.addActionListener(e -> clearForm());

        buttonPanel.add(sendButton);
        buttonPanel.add(clearButton);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private void loadUsers() {
        userComboBox.removeAllItems();
        userComboBox.addItem("Select a user...");
        java.util.List<String> users = dbController.getAllCustomerUsernames();
        if (users != null) {
            for (String username : users) {
                userComboBox.addItem(username);
            }
        }
    }

    private JButton createStyledButton(String text, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(150, 40));
        
        if (isPrimary) {
            button.setBackground(new Color(0, 51, 153));
            button.setForeground(new Color(248, 209, 21));
        } else {
            button.setBackground(Color.LIGHT_GRAY);
            button.setForeground(Color.BLACK);
        }
        
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void handleSendNotification() {
        String selectedUser = (String) userComboBox.getSelectedItem();
        String message = messageArea.getText().trim();

        if (selectedUser == null || selectedUser.equals("Select a user...")) {
            showMessage("Error", "Please select a user!", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (message.isEmpty()) {
            showMessage("Error", "Please enter a message!", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = dbController.sendNotification(selectedUser, message);

        if (success) {
            showMessage("Success", "Notification sent successfully!", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
        } else {
            showMessage("Error", "Failed to send notification. Please try again.", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        userComboBox.setSelectedIndex(0);
        messageArea.setText("");
    }

    private void showMessage(String title, String message, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}