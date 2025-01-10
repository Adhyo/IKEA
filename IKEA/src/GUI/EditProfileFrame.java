package GUI;

import Database.DatabaseController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EditProfileFrame extends JFrame {
    private DatabaseController dbController;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private int userId;

    public EditProfileFrame(int userId, String currentUsername, String currentEmail) {
        this.userId = userId;
        dbController = new DatabaseController();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("IKEA Marketplace - Edit Profile");
        setSize(500, 600);
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

        // Logo Panel
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setOpaque(false);
        JLabel logoLabel = new JLabel("Edit Profile");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        logoLabel.setForeground(new Color(248, 209, 21));
        logoPanel.add(logoLabel);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        // Add form fields with current values
        usernameField = new JTextField(currentUsername);
        emailField = new JTextField(currentEmail);
        passwordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();

        addFormField(formPanel, "Username:", usernameField, gbc, 0);
        addFormField(formPanel, "New Password:", passwordField, gbc, 2);
        addFormField(formPanel, "Confirm New Password:", confirmPasswordField, gbc, 4);
        addFormField(formPanel, "Email:", emailField, gbc, 6);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        buttonPanel.setOpaque(false);
        JButton saveButton = createStyledButton("Save Changes", true);
        JButton cancelButton = createStyledButton("Cancel", false);

        saveButton.addActionListener(e -> handleProfileUpdate());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(logoPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private void addFormField(JPanel panel, String labelText, JComponent field, 
                            GridBagConstraints gbc, int gridy) {
        JLabel label = new JLabel(labelText);
        label.setForeground(Color.WHITE);
        gbc.gridy = gridy;
        panel.add(label, gbc);

        gbc.gridy = gridy + 1;
        field.setPreferredSize(new Dimension(300, 40));
        panel.add(field, gbc);
    }

    private JButton createStyledButton(String text, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(150, 40));
        
        if (isPrimary) {
            button.setBackground(new Color(0, 51, 153));
            button.setForeground(new Color(248, 209, 21));
        } else {
            button.setBackground(new Color(240, 240, 240));
            button.setForeground(new Color(0, 51, 153));
        }
        
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void handleProfileUpdate() {
        String newUsername = usernameField.getText();
        String newPassword = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String newEmail = emailField.getText();
    
        // Validation
        if (newUsername.isEmpty() || newEmail.isEmpty()) {
            showMessage("Error", "Username and email cannot be empty!", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        if (!newPassword.isEmpty() && !newPassword.equals(confirmPassword)) {
            showMessage("Error", "Passwords do not match!", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        if (!isValidEmail(newEmail)) {
            showMessage("Error", "Invalid email format!", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        boolean updateSuccess = dbController.updateUserProfile(
            userId, 
            newUsername,
            newEmail,
            newPassword.isEmpty() ? null : newPassword 
        );
        
        if (updateSuccess) {
            showMessage("Success", "Profile updated successfully!", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            showMessage("Error", "Failed to update profile. Username might already be taken.", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private void showMessage(String title, String message, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}