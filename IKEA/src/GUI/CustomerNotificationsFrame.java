package GUI;

import Database.DatabaseManager;
import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class CustomerNotificationsFrame extends JFrame {
    private final DatabaseManager db = DatabaseManager.getInstance();
    private final int userId;
    private JPanel notificationsPanel;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public CustomerNotificationsFrame(int userId) {
        this.userId = userId;
        
        setTitle("IKEA Marketplace - Notifications");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main panel with gradient background
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

        // Header
        JLabel headerLabel = new JLabel("Your Notifications", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(new Color(248, 209, 21));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        notificationsPanel = new JPanel();
        notificationsPanel.setLayout(new BoxLayout(notificationsPanel, BoxLayout.Y_AXIS));
        notificationsPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(notificationsPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JButton markAllReadButton = new JButton("Mark All as Read");
        markAllReadButton.setFont(new Font("Arial", Font.BOLD, 14));
        markAllReadButton.setBackground(new Color(0, 51, 153));
        markAllReadButton.setForeground(new Color(248, 209, 21));
        markAllReadButton.addActionListener(e -> markAllAsRead());
        mainPanel.add(markAllReadButton, BorderLayout.SOUTH);

        add(mainPanel);
        loadNotifications();
        setVisible(true);
    }

    private void loadNotifications() {
        notificationsPanel.removeAll();
        
        try {
            db.connect();
            String query = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
            PreparedStatement pstmt = db.con.prepareStatement(query);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                addNotificationCard(
                    rs.getInt("notification_id"),
                    rs.getString("message"),
                    rs.getTimestamp("created_at"),
                    rs.getBoolean("is_read")
                );
            }

            if (notificationsPanel.getComponentCount() == 0) {
                JLabel noNotifLabel = new JLabel("No notifications yet", JLabel.CENTER);
                noNotifLabel.setForeground(Color.WHITE);
                noNotifLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                notificationsPanel.add(noNotifLabel);
            }

            notificationsPanel.revalidate();
            notificationsPanel.repaint();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading notifications: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            db.disconnect();
        }
    }

    private void addNotificationCard(int notificationId, String message, java.sql.Timestamp createdAt, boolean isRead) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, isRead ? 200 : 240));
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            }
        };
        card.setLayout(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        card.setOpaque(false);

        JLabel messageLabel = new JLabel("<html><body style='width: 300px'>" + message + "</body></html>");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel dateLabel = new JLabel(dateFormat.format(createdAt));
        dateLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        if (!isRead) {
            JButton markReadButton = new JButton("Mark as Read");
            markReadButton.setFont(new Font("Arial", Font.PLAIN, 12));
            markReadButton.addActionListener(e -> markAsRead(notificationId));
            card.add(markReadButton, BorderLayout.EAST);
        }

        card.add(messageLabel, BorderLayout.CENTER);
        card.add(dateLabel, BorderLayout.SOUTH);

        notificationsPanel.add(Box.createVerticalStrut(10));
        notificationsPanel.add(card);
    }

    private void markAsRead(int notificationId) {
        try {
            db.connect();
            String query = "UPDATE notifications SET is_read = 1 WHERE notification_id = ?";
            PreparedStatement pstmt = db.con.prepareStatement(query);
            pstmt.setInt(1, notificationId);
            pstmt.executeUpdate();
            loadNotifications();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error marking notification as read: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            db.disconnect();
        }
    }

    private void markAllAsRead() {
        try {
            db.connect();
            String query = "UPDATE notifications SET is_read = 1 WHERE user_id = ?";
            PreparedStatement pstmt = db.con.prepareStatement(query);
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            loadNotifications();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error marking all notifications as read: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            db.disconnect();
        }
    }
}