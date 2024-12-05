package GUI;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        // Setup Frame
        setTitle("IKEA Marketplace");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        JMenuItem viewProducts = new JMenuItem("View Products");
        JMenuItem viewCart = new JMenuItem("View Cart");
        JMenuItem exit = new JMenuItem("Exit");

        menu.add(viewProducts);
        menu.add(viewCart);
        menu.add(exit);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        // Main Content Panel
        JPanel mainPanel = new JPanel(new CardLayout());
        ProductPanel productPanel = new ProductPanel();
        CartPanel cartPanel = new CartPanel();

        mainPanel.add(productPanel, "Products");
        mainPanel.add(cartPanel, "Cart");

        // Add Listener for Menu Items
        viewProducts.addActionListener(e -> {
            CardLayout cl = (CardLayout) (mainPanel.getLayout());
            cl.show(mainPanel, "Products");
        });

        viewCart.addActionListener(e -> {
            CardLayout cl = (CardLayout) (mainPanel.getLayout());
            cl.show(mainPanel, "Cart");
        });

        exit.addActionListener(e -> System.exit(0));

        // Add Main Panel to Frame
        add(mainPanel, BorderLayout.CENTER);

        // Set visible
        setVisible(true);
    }
}
