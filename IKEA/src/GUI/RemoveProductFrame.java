package GUI;

import javax.swing.*;
import java.awt.*;

public class RemoveProductFrame extends JFrame {
    public RemoveProductFrame() {
        setTitle("Remove Product");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(2, 1));

        JLabel idLabel = new JLabel("Product ID to Remove:");
        JTextField idField = new JTextField();
        JButton removeButton = new JButton("Remove Product");

        add(idLabel);
        add(idField);
        add(new JLabel()); 
        add(removeButton);

        removeButton.addActionListener(e -> {
            String productId = idField.getText();

            JOptionPane.showMessageDialog(this, "Product with ID " + productId + " has been removed.");
        });

        setVisible(true);
    }
}
