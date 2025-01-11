package GUI;

import Database.DatabaseManager;
import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WarehousesFrame extends JFrame {
    private final DatabaseManager db = DatabaseManager.getInstance();
    private JPanel warehousesPanel;

    public WarehousesFrame() {
        setTitle("IKEA Marketplace - Warehouses");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

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

        JLabel headerLabel = new JLabel("Warehouses", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(new Color(248, 209, 21));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        warehousesPanel = new JPanel();
        warehousesPanel.setLayout(new BoxLayout(warehousesPanel, BoxLayout.Y_AXIS));
        warehousesPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(warehousesPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
        loadWarehouses();
        setVisible(true);
    }

    private void loadWarehouses() {
        warehousesPanel.removeAll();

        try {
            db.connect();
            String query = "SELECT * FROM warehouses ORDER BY warehouse_id ASC";
            PreparedStatement pstmt = db.con.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                addWarehouseCard(
                    rs.getString("name"),
                    rs.getString("address"),
                    rs.getString("phone_number")
                );
            }

            if (warehousesPanel.getComponentCount() == 0) {
                JLabel noWarehouseLabel = new JLabel("No warehouses available", JLabel.CENTER);
                noWarehouseLabel.setForeground(Color.WHITE);
                noWarehouseLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                warehousesPanel.add(noWarehouseLabel);
            }

            warehousesPanel.revalidate();
            warehousesPanel.repaint();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading warehouses: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            db.disconnect();
        }
    }

    private void addWarehouseCard(String name, String address, String phoneNumber) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 240));
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            }
        };
        card.setLayout(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        card.setOpaque(false);

        JLabel nameLabel = new JLabel("Name: " + name);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel addressLabel = new JLabel("Address: " + address);
        addressLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel phoneLabel = new JLabel("Phone: " + phoneNumber);
        phoneLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.add(nameLabel);
        infoPanel.add(addressLabel);
        infoPanel.add(phoneLabel);

        card.add(infoPanel, BorderLayout.CENTER);

        warehousesPanel.add(Box.createVerticalStrut(10));
        warehousesPanel.add(card);
    }
}
