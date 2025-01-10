package GUI;

import Model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class MainFrame extends JFrame {
    private static MainFrame instance;
    private User currentUser;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private CartPanel cartPanel;
    private CheckoutPanel checkoutPanel;
    private ProductPanel productPanel;
    
    public static MainFrame getInstance() {
        return instance;
    }
    
    public MainFrame(User user) {
        instance = this;
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.currentUser = user;

        setTitle("IKEA Marketplace");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        backgroundPanel.setLayout(new BorderLayout());

        createCustomMenuBar();
        createMainPanel();

        backgroundPanel.add(mainPanel);
        add(backgroundPanel);

        setVisible(true);
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

        menuBar.add(createMenuButton("Products", KeyEvent.VK_P, e -> showProductPanel()));
        
        if (currentUser != null) {
            menuBar.add(createMenuButton("Cart", KeyEvent.VK_C, e -> showCartPanel()));
            
            if (currentUser.getUserType().toString().equals("ADMIN")) {
                menuBar.add(createMenuButton("Manage Users", KeyEvent.VK_M, e -> {
                    JOptionPane.showMessageDialog(this, 
                        "User Management Feature\nComing Soon!", 
                        "Feature Preview", 
                        JOptionPane.INFORMATION_MESSAGE);
                }));
            }
        }

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        if (currentUser == null) {
            rightPanel.add(createMenuButton("Login", KeyEvent.VK_L, e -> {
                dispose();
                new LoginFrame(); 
            }));
        } else {
            JButton profileButton = createMenuButton(currentUser.getUsername(), KeyEvent.VK_U, null);
            
            JPopupMenu profileMenu = new JPopupMenu();
            profileMenu.setBackground(new Color(0, 51, 153));
            
            JMenuItem editProfileItem = new JMenuItem("Edit Profile");
            editProfileItem.setForeground(new Color(248, 209, 21));
            editProfileItem.setBackground(new Color(0, 51, 153));
            editProfileItem.addActionListener(e -> new EditProfileFrame(
                currentUser.getUserId(), 
                currentUser.getUsername(), 
                currentUser.getEmail()
            ));
            
            JMenuItem logoutItem = new JMenuItem("Logout");
            logoutItem.setForeground(new Color(248, 209, 21));
            logoutItem.setBackground(new Color(0, 51, 153));
            logoutItem.addActionListener(e -> {
                dispose();
                new LoginFrame();
            });
            
            profileMenu.add(editProfileItem);
            profileMenu.addSeparator();
            profileMenu.add(logoutItem);
            
            profileButton.addActionListener(e -> {
                profileMenu.show(profileButton, 0, profileButton.getHeight());
            });
            
            rightPanel.add(profileButton);
        }
        
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(rightPanel);

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

    private void createMainPanel() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setOpaque(false);

        productPanel = new ProductPanel(currentUser);
        mainPanel.add(productPanel, "Products");

        if (currentUser != null) {
            cartPanel = new CartPanel(currentUser, this);
            checkoutPanel = new CheckoutPanel(currentUser);
            
            productPanel.addObserver(cartPanel);
            cartPanel.addCartObserver(checkoutPanel);
            
            mainPanel.add(cartPanel, "Cart");
            mainPanel.add(checkoutPanel, "Checkout");
        }

        cardLayout.show(mainPanel, "Products");
    }

    public void showProductPanel() {
        cardLayout.show(mainPanel, "Products");
    }

    public void showCartPanel() {
        if (currentUser == null) {
            showAccessDeniedDialog();
            return;
        }
        cardLayout.show(mainPanel, "Cart");
    }

    public void showCheckoutPanel() {
        if (currentUser == null) {
            showAccessDeniedDialog();
            return;
        }
        cardLayout.show(mainPanel, "Checkout");
    }

    private void showAccessDeniedDialog() {
        JOptionPane.showMessageDialog(this,
            "Please login to access this feature!",
            "Access Denied",
            JOptionPane.WARNING_MESSAGE);
    }
}