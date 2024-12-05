package GUI;

import javax.swing.*;
import java.awt.*;

public class AddProductFrame extends JFrame {
    public AddProductFrame() {
        setTitle("Add Product");
        setBounds(350, 0, 700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setLayout(new GridLayout(5, 2));

        JLabel nameLabel = new JLabel("Product Name:");
        JTextField nameField = new JTextField();
        JLabel priceLabel = new JLabel("Price:");
        JTextField priceField = new JTextField();
        JLabel stockLabel = new JLabel("Stock:");
        JTextField stockField = new JTextField();
        JButton addButton = new JButton("Add Product");

        add(nameLabel);
        add(nameField);
        add(priceLabel);
        add(priceField);
        add(stockLabel);
        add(stockField);
        add(new JLabel());
        add(addButton);

        addButton.addActionListener(e -> {
            String name = nameField.getText();
            String price = priceField.getText();
            String stock = stockField.getText();

            JOptionPane.showMessageDialog(this, "Product Added:\nName: " + name + "\nPrice: " + price + "\nStock: " + stock);
        });

        setVisible(true);
    }
}
