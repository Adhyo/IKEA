package GUI;

import Database.DatabaseManager;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AdminReturnFrame extends JFrame {
    private JTable returnTable;
    private DefaultTableModel tableModel;
    private DatabaseManager db;

    public AdminReturnFrame() {
        setTitle("Return Requests");
        setSize(800, 600);
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

        JLabel titleLabel = new JLabel("Return Requests", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"Request ID", "Transaction ID", "User ID", "Request Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        returnTable = new JTable(tableModel);
        returnTable.setFillsViewportHeight(true);
        returnTable.setBackground(Color.WHITE);
        returnTable.setForeground(Color.BLACK);
        returnTable.setFont(new Font("Arial", Font.PLAIN, 14));
        returnTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(returnTable);
        scrollPane.setBackground(Color.WHITE);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.setBackground(new Color(0, 51, 153));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.addActionListener(e -> loadReturnRequests());
        mainPanel.add(refreshButton, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);

        loadReturnRequests();
    }

    private void loadReturnRequests() {
        tableModel.setRowCount(0);

        try {
            db = DatabaseManager.getInstance();
            db.connect();

            String query = "SELECT * FROM return_requests ORDER BY request_date DESC";
            PreparedStatement stmt = db.con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getInt("transaction_id"),
                    rs.getInt("user_id"),
                    rs.getDate("request_date")
                };
                tableModel.addRow(row);
            }

            rs.close();
            stmt.close();
            db.disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading return requests: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
