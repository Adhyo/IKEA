package GUI;

import Model.User;
import Model.UserType;
import Model.Admin;
import Model.Customer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class LoginFrame extends JFrame {
    private HashMap<String, User> userDatabase;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeUserDatabase();

        setTitle("IKEA Marketplace - Login");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 

        JPanel mainPanel = new JPanel() {
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
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setOpaque(false);
        JLabel logoLabel = new JLabel("IKEA Marketplace");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        logoLabel.setForeground(Color.WHITE);
        logoPanel.add(logoLabel);

        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.WHITE);
        loginPanel.add(usernameLabel, gbc);
        
        gbc.gridy = 1;
        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(300, 40));
        loginPanel.add(usernameField, gbc);

        gbc.gridy = 2;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);
        loginPanel.add(passwordLabel, gbc);
        
        gbc.gridy = 3;
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(300, 40));
        loginPanel.add(passwordField, gbc);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        buttonPanel.setOpaque(false);
        JButton loginButton = createStyledButton("Login", true);
        JButton guestButton = createStyledButton("Continue as Guest", false);
        
        buttonPanel.add(loginButton);
        buttonPanel.add(guestButton);

        mainPanel.add(logoPanel, BorderLayout.NORTH);
        mainPanel.add(loginPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
            
                User user = authenticate(username, password);
                if (user != null) {
                    showCustomDialog("Login Berhasil", 
                        "Selamat datang, " + user.getUserType(), 
                        JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    if (user.getUserType() == UserType.ADMIN) {
                        new AdminFrame();
                    } else {
                        new MainFrame(user);
                    }
                } else {
                    showCustomDialog("Login Gagal", 
                        "Username atau password salah!\nSilakan coba lagi.", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        guestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCustomDialog("Guest Access", 
                    "Melanjutkan sebagai Tamu", 
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new MainFrame(null);
            }
        });

        add(mainPanel);
        setVisible(true);
    }

    private JButton createStyledButton(String text, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(150, 40));
        
        if (isPrimary) {
            button.setBackground(new Color(255, 255, 255));
            button.setForeground(new Color(0, 51, 153));
        } else {
            button.setBackground(new Color(0, 51, 153));
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        }
        
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void showCustomDialog(String title, String message, int messageType) {
        JOptionPane optionPane = new JOptionPane(message, messageType);
        JDialog dialog = optionPane.createDialog(this, title);
        dialog.setModal(true);
        dialog.setVisible(true);
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