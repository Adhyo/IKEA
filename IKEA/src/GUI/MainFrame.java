package GUI;

import Model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class MainFrame extends JFrame {
    private User currentUser;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    public MainFrame(User user) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.currentUser = user;

        // Frame configuration
        setTitle("IKEA Marketplace");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create menu bar with modern styling
        createCustomMenuBar();

        // Create main content panel with CardLayout
        createMainPanel();

        setVisible(true);
    }

    private void createCustomMenuBar() {
        // Custom styled menu bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(0, 51, 153));

        // Menu with styled look
        JMenu menu = new JMenu("Menu");
        menu.setForeground(Color.WHITE);
        menu.setFont(new Font("Arial", Font.BOLD, 14));

        // Menu items
        JMenuItem viewProducts = createMenuItem("View Products", KeyEvent.VK_P);
        JMenuItem viewCart = createMenuItem("View Cart", KeyEvent.VK_C);
        JMenuItem logout = createMenuItem("Logout", KeyEvent.VK_L);

        // Add menu items to menu
        menu.add(viewProducts);
        menu.addSeparator();
        menu.add(viewCart);

        // Add admin-specific menu item if user is admin
        if (currentUser != null && currentUser.getUserType().toString().equals("ADMIN")) {
            JMenuItem manageUsers = createMenuItem("Manage Users", KeyEvent.VK_M);
            menu.addSeparator();
            menu.add(manageUsers);

            manageUsers.addActionListener(e -> {
                JOptionPane.showMessageDialog(this, 
                    "User Management Feature\nComing Soon!", 
                    "Feature Preview", 
                    JOptionPane.INFORMATION_MESSAGE);
            });
        }

        menu.addSeparator();
        menu.add(logout);

        menuBar.add(menu);
        setJMenuBar(menuBar);

        // Menu item action listeners
        viewProducts.addActionListener(e -> {
            cardLayout.show(mainPanel, "Products");
        });

        viewCart.addActionListener(e -> {
            if (currentUser == null) {
                showAccessDeniedDialog();
            } else {
                cardLayout.show(mainPanel, "Cart");
            }
        });

        logout.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });
    }

    private JMenuItem createMenuItem(String text, int mnemonic) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(new Font("Arial", Font.PLAIN, 12));
        item.setMnemonic(mnemonic);
        return item;
    }

    private void createMainPanel() {
        // Main content panel with CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        ProductPanel productPanel = new ProductPanel();
        CartPanel cartPanel = new CartPanel();

        mainPanel.add(productPanel, "Products");
        
        // Only add cart for logged-in users
        if (currentUser != null) {
            mainPanel.add(cartPanel, "Cart");
        }

        add(mainPanel, BorderLayout.CENTER);

        // Default to Products view
        cardLayout.show(mainPanel, "Products");
    }

    private void showAccessDeniedDialog() {
        JOptionPane optionPane = new JOptionPane(
            "Guest cannot access the cart. Please login first!", 
            JOptionPane.WARNING_MESSAGE
        );
        JDialog dialog = optionPane.createDialog(this, "Access Denied");
        dialog.setModal(true);
        dialog.setVisible(true);
    }
}