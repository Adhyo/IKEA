package GUI;

import Model.Category;
import Model.Product;
import Database.DatabaseController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

public class ProductFrame extends JFrame {
    private Category category;
    private DatabaseController dbController;

    public ProductFrame(Category category) {
        this.category = category;
        dbController = new DatabaseController();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("IKEA Marketplace - " + category.getCategoryName());
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

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
        backgroundPanel.setLayout(new BorderLayout(20, 20));
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        createCustomMenuBar();

        JPanel productPanel = new JPanel(new GridLayout(0, 2, 20, 20));
        productPanel.setOpaque(false);

        List<Product> products = dbController.getProductsForCategory(category);

        for (Product product : products) {
            JPanel productCard = createProductCard(product);
            productPanel.add(productCard);
        }

        JScrollPane scrollPane = new JScrollPane(productPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        backgroundPanel.add(scrollPane, BorderLayout.CENTER);

        JLabel categoryLabel = new JLabel(category.getCategoryName(), SwingConstants.CENTER);
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 28));
        categoryLabel.setForeground(new Color(248, 209, 21));
        categoryLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        backgroundPanel.add(categoryLabel, BorderLayout.NORTH);

        add(backgroundPanel);
        setVisible(true);
    }

    private JPanel createProductCard(Product product) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(10, 10));
        card.setBackground(new Color(255, 255, 255, 30));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(248, 209, 21), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);

        addStyledLabel(detailsPanel, "Name: " + product.getName(), Font.BOLD, 16);
        addStyledLabel(detailsPanel, "Description: " + product.getDescription(), Font.PLAIN, 14);
        
        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pricePanel.setOpaque(false);
        
        if (product.getDiscountPrice() < product.getPrice()) {
            JLabel originalPrice = new JLabel("$" + product.getPrice());
            originalPrice.setFont(new Font("Arial", Font.PLAIN, 14));
            originalPrice.setForeground(Color.LIGHT_GRAY);
            originalPrice.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
            
            JLabel discountPrice = new JLabel("$" + product.getDiscountPrice());
            discountPrice.setFont(new Font("Arial", Font.BOLD, 16));
            discountPrice.setForeground(new Color(248, 209, 21));
            
            pricePanel.add(new JLabel("Price: "));
            pricePanel.add(originalPrice);
            pricePanel.add(new JLabel(" → "));
            pricePanel.add(discountPrice);
        } else {
            addStyledLabel(detailsPanel, "Price: $" + product.getPrice(), Font.BOLD, 16);
        }
        
        detailsPanel.add(pricePanel);
        addStyledLabel(detailsPanel, "Stock: " + product.getStockQuantity(), Font.PLAIN, 14);

        JButton addToCartButton = new JButton("Add to Cart");
        styleButton(addToCartButton);
        
        card.add(detailsPanel, BorderLayout.CENTER);
        card.add(addToCartButton, BorderLayout.SOUTH);

        return card;
    }

    private void addStyledLabel(JPanel panel, String text, int style, int size) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", style, size));
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        panel.add(label);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(new Color(248, 209, 21));
        button.setBackground(new Color(0, 51, 153));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 71, 173));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 51, 153));
            }
        });
    }

    private void createCustomMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(0, 51, 153));
        menuBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel titleLabel = new JLabel("IKEA Marketplace");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(248, 209, 21));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 20));
        menuBar.add(titleLabel);

        menuBar.add(createMenuButton("Categories", KeyEvent.VK_C, e -> {
            new CategoryFrame();
            dispose();
        }));

        menuBar.add(createMenuButton("Home", KeyEvent.VK_H, e -> {
            new MainFrame(null);
            dispose();
        }));

        menuBar.add(Box.createHorizontalGlue());
        setJMenuBar(menuBar);
    }

    private JButton createMenuButton(String text, int mnemonic, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(new Color(4, 52, 140));
        button.setBackground(new Color(0, 51, 153));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setMnemonic(mnemonic);
        if (listener != null) {
            button.addActionListener(listener);
        }

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(new Color(248, 209, 21));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(new Color(4, 52, 140));
            }
        });

        return button;
    }
}