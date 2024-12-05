package GUI;

import Model.User;
import Model.UserType;
import Model.Admin;
import Model.Customer;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class LoginFrame extends JFrame {
    private HashMap<String, User> userDatabase;

    public LoginFrame() {
        initializeUserDatabase();

        setTitle("Login - IKEA Marketplace");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Welcome to IKEA Marketplace", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        JPanel loginPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();

        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);

        JButton loginButton = new JButton("Login");
        JButton guestButton = new JButton("Continue as Guest");
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        buttonPanel.add(loginButton);
        buttonPanel.add(guestButton);

        add(loginPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
        
            User user = authenticate(username, password);
            if (user != null) {
                JOptionPane.showMessageDialog(this, "Login berhasil sebagai " + user.getUserType());
                dispose();
                if (user.getUserType() == UserType.ADMIN) {
                    new AdminFrame(); // Menu khusus Admin
                } else {
                    new MainFrame(user); // Menu untuk Customer
                }
            } else {
                JOptionPane.showMessageDialog(this, "Username atau password salah! Silahkan coba lagi.");
            }
        });

        guestButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Lanjut sebagai Guest");
            dispose();
            new MainFrame(null);
        });

        setVisible(true);
    }

    private void initializeUserDatabase() {
        userDatabase = new HashMap<>();
        userDatabase.put("admin", new Admin(1, "admin", "kocak123", "admin@ikea.com", 0));
        userDatabase.put("customer1", new Customer(2, "customer1", "awikwok123", "customer1@ikea.com", "akuBang", "123456"));
    }

    private User authenticate(String username, String password) {
        if (userDatabase.containsKey(username)) {
            User user = userDatabase.get(username);
            if (user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }    
}
