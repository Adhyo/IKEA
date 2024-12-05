package GUI;

import Model.User;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private User currentUser;

    public MainFrame(User user) {
        this.currentUser = user;

        setTitle("IKEA Marketplace");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        JMenuItem viewProducts = new JMenuItem("View Products");
        JMenuItem viewCart = new JMenuItem("View Cart");
        JMenuItem logout = new JMenuItem("Logout");

        menu.add(viewProducts);
        menu.add(viewCart);
        if (currentUser != null && currentUser.getUserType().equals("ADMIN")) {
            JMenuItem manageUsers = new JMenuItem("Manage Users");
            menu.add(manageUsers);
        }
        menu.add(logout);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        JPanel mainPanel = new JPanel(new CardLayout());
        ProductPanel productPanel = new ProductPanel();
        CartPanel cartPanel = new CartPanel();

        mainPanel.add(productPanel, "Products");
        if (currentUser != null) {
            mainPanel.add(cartPanel, "Cart");
        }

        viewProducts.addActionListener(e -> {
            CardLayout cl = (CardLayout) (mainPanel.getLayout());
            cl.show(mainPanel, "Products");
        });

        viewCart.addActionListener(e -> {
            if (currentUser == null) {
                JOptionPane.showMessageDialog(this, "Guest cannot access the cart. Please login first!");
            } else {
                CardLayout cl = (CardLayout) (mainPanel.getLayout());
                cl.show(mainPanel, "Cart");
            }
        });

        logout.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }
}
