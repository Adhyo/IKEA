package GUI;

import Database.DatabaseManager;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AdminReviewFrame extends JFrame {
    private JTable reviewTable;
    private DefaultTableModel tableModel;
    private DatabaseManager db;

    public AdminReviewFrame() {
        setTitle("Customer Reviews");
        setSize(900, 600);
        setLocationRelativeTo(null);
        
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

        JLabel titleLabel = new JLabel("Customer Reviews", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"Review ID", "Customer Name", "Transaction ID", "Rating", "Review", "Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reviewTable = new JTable(tableModel);
        reviewTable.setFillsViewportHeight(true);
        reviewTable.setBackground(Color.WHITE);
        reviewTable.setForeground(Color.BLACK);
        reviewTable.setFont(new Font("Arial", Font.PLAIN, 14));
        reviewTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
        // Scroll pane for table
        JScrollPane scrollPane = new JScrollPane(reviewTable);
        scrollPane.setBackground(Color.WHITE);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.setBackground(new Color(0, 51, 153));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.addActionListener(e -> loadReviews());
        mainPanel.add(refreshButton, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
        loadReviews();
    }

    private void loadReviews() {
        tableModel.setRowCount(0);
        
        try {
            db = DatabaseManager.getInstance();
            db.connect();
            
            String query = "SELECT r.id, u.name, r.transaction_id, r.rating, r.review_text, " +
                          "r.review_date FROM reviews r " +
                          "JOIN users u ON r.user_id = u.user_id " +
                          "ORDER BY r.review_date DESC";
            PreparedStatement stmt = db.con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("transaction_id"),
                    String.valueOf(rs.getInt("rating")),
                    rs.getString("review_text"),
                    rs.getTimestamp("review_date")
                };
                tableModel.addRow(row);
            }

            rs.close();
            stmt.close();
            db.disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading reviews: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}