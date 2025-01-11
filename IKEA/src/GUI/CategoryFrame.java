package GUI;

import Model.Category;
import Model.User;
import Database.DatabaseController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

public class CategoryFrame extends JFrame {
    private DatabaseController dbController;
    private User currentUser;

    public CategoryFrame(User user) {
        this.currentUser = user;
        dbController = new DatabaseController();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("IKEA Marketplace - Categories");
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

        JPanel categoryPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        categoryPanel.setOpaque(false);

        List<Category> categories = dbController.getAllCategories();

        for (Category category : categories) {
            JPanel categoryCard = createCategoryCard(category);
            categoryPanel.add(categoryCard);
        }

        JScrollPane scrollPane = new JScrollPane(categoryPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        backgroundPanel.add(scrollPane, BorderLayout.CENTER);

        JLabel welcomeLabel = new JLabel("Browse Categories", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(248, 209, 21));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        backgroundPanel.add(welcomeLabel, BorderLayout.NORTH);

        add(backgroundPanel);
        setVisible(true);
    }

    private JPanel createCategoryCard(Category category) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(new Color(255, 255, 255, 30));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(248, 209, 21), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel nameLabel = new JLabel(category.getCategoryName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        nameLabel.setForeground(Color.WHITE);

        JButton viewButton = new JButton("View Products");
        styleButton(viewButton);
        viewButton.addActionListener(e -> {
            new ProductFrame(category, currentUser);
            dispose();
        });

        card.add(nameLabel, BorderLayout.CENTER);
        card.add(viewButton, BorderLayout.SOUTH);

        return card;
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

        menuBar.add(createMenuButton("Home", KeyEvent.VK_H, e -> {
            new MainFrame(currentUser);
            dispose();
        }));

        if (currentUser != null) {
            menuBar.add(createMenuButton("Cart", KeyEvent.VK_C, e -> {
                MainFrame mainFrame = MainFrame.getInstance();
                if (mainFrame != null) {
                    mainFrame.showCartPanel();
                }
            }));
        }

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