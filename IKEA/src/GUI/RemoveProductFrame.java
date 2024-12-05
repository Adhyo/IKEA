package GUI;

import javax.swing.*;
import java.awt.*;

public class RemoveProductFrame extends JFrame {
    public RemoveProductFrame() {
        setTitle("Remove Product");
        setBounds(350, 0, 700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(2, 1));
        setVisible(true);

        JLabel idLabel = new JLabel("Product ID to Remove:");
        JTextField idField = new JTextField();
        idField.setPreferredSize(new Dimension(20, 100));
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
