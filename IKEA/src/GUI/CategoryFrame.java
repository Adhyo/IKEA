package GUI;

import Database.DatabaseController;
import Model.Category;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CategoryFrame extends JFrame {
    private DatabaseController dbController;
    private JPanel categoryPanel;

    public CategoryFrame() {
        dbController = new DatabaseController();

        setTitle("Categories");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create background panel
        JPanel backgroundPanel = new JPanel() {
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
        backgroundPanel.setLayout(new BorderLayout());

        // Create the category panel
        categoryPanel = new JPanel();
        categoryPanel.setLayout(new GridLayout(0, 1, 10, 10)); // Vertical list of categories
        categoryPanel.setOpaque(false);

        // Fetch categories from the database
        List<Category> categories = dbController.getAllCategories();
        for (Category category : categories) {
            JButton categoryButton = new JButton(category.getCategoryName());
            categoryButton.setFont(new Font("Arial", Font.BOLD, 14));
            categoryButton.setPreferredSize(new Dimension(200, 40));
            categoryButton.setBackground(new Color(0, 51, 153));
            categoryButton.setForeground(new Color(248, 209, 21));
            categoryButton.setBorder(BorderFactory.createLineBorder(Color.WHITE));
            categoryButton.setFocusPainted(false);
            categoryButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            // Add action listener to open the selected category's details
            categoryButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    openCategoryDetails(category);
                }
            });

            categoryPanel.add(categoryButton);
        }

        backgroundPanel.add(categoryPanel, BorderLayout.CENTER);
        add(backgroundPanel);
        setVisible(true);
    }

    // Method to open the selected category's details or products
    private void openCategoryDetails(Category category) {
        // Assuming you have a ProductPanel or similar to show products in this category
        JOptionPane.showMessageDialog(this, 
            "Showing details for: " + category.getCategoryName(),
            "Category Details",
            JOptionPane.INFORMATION_MESSAGE);

        // Optionally, you can open another frame with category-specific products or content
        // new ProductFrame(category);
    }
}
