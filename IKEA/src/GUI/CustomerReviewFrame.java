package GUI;

import Database.DatabaseManager;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import java.awt.event.ActionEvent;

public class CustomerReviewFrame extends JFrame {
    private final int userId;
    private final int transactionId;
    private JTextArea reviewTextArea;
    private JComboBox<String> ratingComboBox;
    private DatabaseManager db;

    public CustomerReviewFrame(int userId, int transactionId) {
        this.userId = userId;
        this.transactionId = transactionId;
        
        setTitle("Write a Review");
        setSize(500, 400);
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
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Write Your Review", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel ratingLabel = new JLabel("Rating (1-5):");
        ratingLabel.setForeground(Color.WHITE);
        String[] ratings = {"5", "4", "3", "2", "1"};
        ratingComboBox = new JComboBox<>(ratings);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(ratingLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        centerPanel.add(ratingComboBox, gbc);

        JLabel reviewLabel = new JLabel("Your Review:");
        reviewLabel.setForeground(Color.WHITE);
        reviewTextArea = new JTextArea(5, 30);
        reviewTextArea.setLineWrap(true);
        reviewTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(reviewTextArea);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        centerPanel.add(reviewLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        centerPanel.add(scrollPane, gbc);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);

        JButton submitButton = new JButton("Submit Review");
        submitButton.setBackground(new Color(0, 51, 153));
        submitButton.setForeground(Color.WHITE);
        submitButton.addActionListener(this::submitReview);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(0, 51, 153));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
        
        checkExistingReview();
    }

    private void checkExistingReview() {
        try {
            db = DatabaseManager.getInstance();
            db.connect();
            
            String query = "SELECT * FROM reviews WHERE user_id = ? AND transaction_id = ?";
            PreparedStatement stmt = db.con.prepareStatement(query);
            stmt.setInt(1, userId);
            stmt.setInt(2, transactionId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this,
                    "You have already reviewed this transaction.",
                    "Review Exists",
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }

            rs.close();
            stmt.close();
            db.disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void submitReview(ActionEvent e) {
        String reviewText = reviewTextArea.getText().trim();
        int rating = 5 - ratingComboBox.getSelectedIndex();
        
        if (reviewText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please write your review before submitting.",
                "Review Required",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            db = DatabaseManager.getInstance();
            db.connect();
            
            String query = "INSERT INTO reviews (user_id, transaction_id, rating, review_text) " +
                          "VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = db.con.prepareStatement(query);
            stmt.setInt(1, userId);
            stmt.setInt(2, transactionId);
            stmt.setInt(3, rating);
            stmt.setString(4, reviewText);
            
            int result = stmt.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(this,
                    "Thank you for your review!",
                    "Review Submitted",
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }

            stmt.close();
            db.disconnect();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error submitting review: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}