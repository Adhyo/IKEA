package GUI;

import Model.User;
import Database.DatabaseManager;

import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PaymentFrame extends JFrame {
    private final DatabaseManager db = DatabaseManager.getInstance();
    private final User currentUser;
    private final int orderId;
    private final double amount;
    private final Color IKEA_BLUE = new Color(0, 51, 153);
    private final Color IKEA_YELLOW = new Color(248, 209, 21);
    private final Color IKEA_LIGHT_BLUE = new Color(0, 105, 255);
    private JComboBox<String> paymentMethodComboBox;
    private JTextField cardNumberField;
    private JTextField expiryField;
    private JTextField cvvField;

    public PaymentFrame(User user, int orderId, double amount) {
        this.currentUser = user;
        this.orderId = orderId;
        this.amount = amount;

        setTitle("Payment Processing");
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                    0, 0, IKEA_BLUE,
                    getWidth(), getHeight(), IKEA_LIGHT_BLUE
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("Payment Details", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(IKEA_YELLOW);
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);

        JLabel amountLabel = new JLabel(String.format("Amount to Pay: $%.2f", amount));
        amountLabel.setFont(new Font("Arial", Font.BOLD, 18));
        amountLabel.setForeground(Color.WHITE);
        formPanel.add(amountLabel, gbc);

        JLabel methodLabel = new JLabel("Payment Method:");
        methodLabel.setForeground(Color.WHITE);
        formPanel.add(methodLabel, gbc);

        paymentMethodComboBox = new JComboBox<>(new String[]{"Credit Card", "Debit Card"});
        formPanel.add(paymentMethodComboBox, gbc);

        cardNumberField = createStyledTextField("Card Number");
        expiryField = createStyledTextField("MM/YY");
        cvvField = createStyledTextField("CVV");

        formPanel.add(new JLabel("Card Number:"), gbc);
        formPanel.add(cardNumberField, gbc);
        formPanel.add(new JLabel("Expiry Date:"), gbc);
        formPanel.add(expiryField, gbc);
        formPanel.add(new JLabel("CVV:"), gbc);
        formPanel.add(cvvField, gbc);

        for (Component comp : formPanel.getComponents()) {
            if (comp instanceof JLabel) {
                comp.setForeground(Color.WHITE);
                ((JLabel) comp).setFont(new Font("Arial", Font.BOLD, 14));
            }
        }

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);

        JButton processButton = createStyledButton("Process Payment", true);
        JButton cancelButton = createStyledButton("Cancel", false);

        processButton.addActionListener(e -> processPayment());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(processButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleCancel();
            }
        });
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(IKEA_BLUE),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        field.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if ("Card Number".equals(placeholder)) {
                    if (!Character.isDigit(c) || field.getText().length() >= 16) {
                        evt.consume();
                    }
                } else if ("MM/YY".equals(placeholder)) {
                    if ((!Character.isDigit(c) && c != '/') || field.getText().length() >= 5) {
                        evt.consume();
                    }
                } else if ("CVV".equals(placeholder)) {
                    if (!Character.isDigit(c) || field.getText().length() >= 3) {
                        evt.consume();
                    }
                }
            }
        });

        return field;
    }

    private JButton createStyledButton(String text, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(150, 40));

        if (isPrimary) {
            button.setBackground(IKEA_BLUE);
            button.setForeground(IKEA_YELLOW);
        } else {
            button.setBackground(Color.WHITE);
            button.setForeground(IKEA_BLUE);
        }

        button.setBorder(BorderFactory.createLineBorder(IKEA_BLUE, 2));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void processPayment() {
        if (!validatePaymentFields()) {
            return;
        }

        JDialog processingDialog = new JDialog(this, "Processing Payment", true);
        processingDialog.setLayout(new FlowLayout());
        processingDialog.add(new JLabel("Processing payment, please wait..."));
        processingDialog.setSize(250, 100);
        processingDialog.setLocationRelativeTo(this);

        new Thread(() -> {
            try {
                Thread.sleep(2000);
                updateOrderStatus();
                SwingUtilities.invokeLater(() -> {
                    processingDialog.dispose();
                    JOptionPane.showMessageDialog(this,
                        "Payment processed successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private boolean validatePaymentFields() {
        if (cardNumberField.getText().trim().isEmpty() ||
            expiryField.getText().trim().isEmpty() ||
            cvvField.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this,
                "Please fill in all payment details",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (cardNumberField.getText().length() != 16) {
            JOptionPane.showMessageDialog(this,
                "Card Number must be 16 digits long",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!expiryField.getText().matches("\\d{2}/\\d{2}")) {
            JOptionPane.showMessageDialog(this,
                "Expiry Date must be in MM/YY format",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void updateOrderStatus() {
        try {
            db.connect();
            String query = "UPDATE orders SET status = 'PAID' WHERE order_id = ?";
            PreparedStatement pstmt = db.con.prepareStatement(query);
            pstmt.setInt(1, orderId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error updating order status: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            db.disconnect();
        }
    }

    private void handleCancel() {
        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel the payment?",
            "Confirm Cancel",
            JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            dispose();
        }
    }
}
