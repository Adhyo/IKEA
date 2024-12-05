package GUI;

import javax.swing.*;
import java.awt.*;

public class CartPanel extends JPanel {
    public CartPanel() {
        setLayout(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel("Shopping Cart", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Cart Items
        String[] columnNames = {"ID", "Name", "Quantity", "Price"};
        Object[][] data = {
                {1, "Chair", 2, 200.0},
                {2, "Table", 1, 300.0}
        };
        JTable cartTable = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(cartTable);
        add(scrollPane, BorderLayout.CENTER);

        // Action Buttons
        JPanel buttonPanel = new JPanel();
        JButton checkoutButton = new JButton("Checkout");
        JButton clearCartButton = new JButton("Clear Cart");
        buttonPanel.add(checkoutButton);
        buttonPanel.add(clearCartButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
