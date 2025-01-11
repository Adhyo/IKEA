package GUI;

import Database.DatabaseManager;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.table.DefaultTableModel; 

public class TransactionHistoryFrame extends JFrame {
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private DatabaseManager db; 

    public TransactionHistoryFrame() {
        setTitle("Transaction History");
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

        JLabel titleLabel = new JLabel("Transaction History", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"Transaction ID", "Cart ID", "Subtotal", "Final Amount", "Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        transactionTable = new JTable(tableModel);
        transactionTable.setFillsViewportHeight(true);
        transactionTable.setBackground(Color.WHITE);
        transactionTable.setForeground(Color.BLACK);
        transactionTable.setFont(new Font("Arial", Font.PLAIN, 14));
        transactionTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBackground(Color.WHITE);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.setBackground(new Color(0, 51, 153));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.addActionListener(e -> loadTransactions());
        mainPanel.add(refreshButton, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
        
        loadTransactions();
    }

    private void loadTransactions() {
        tableModel.setRowCount(0);
        
        try {
            db = DatabaseManager.getInstance();
            db.connect();
            
            String query = "SELECT * FROM transactions ORDER BY transaction_date DESC";
            PreparedStatement stmt = db.con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("transaction_id"),
                    rs.getInt("cart_id"),
                    String.format("$%.2f", rs.getDouble("sub_total")),
                    String.format("$%.2f", rs.getDouble("final_amount")),
                    dateFormat.format(rs.getDate("transaction_date"))
                };
                tableModel.addRow(row);
            }

            rs.close();
            stmt.close();
            db.disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading transactions: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}