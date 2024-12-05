package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ProductPanel extends JPanel {
    public ProductPanel() {
        
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Product List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Name", "Description", "Price"};
        Object[][] data = {
                {1, "Chair", "Comfortable wooden chair", 100.0},
                {2, "Table", "Dining table", 300.0}
        };
        JTable productTable = new JTable(new DefaultTableModel(data, columnNames));
        JScrollPane scrollPane = new JScrollPane(productTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addToCartButton = new JButton("Add to Cart");
        buttonPanel.add(addToCartButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
