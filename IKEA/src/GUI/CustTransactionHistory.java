package GUI;

import Database.DatabaseManager;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class CustTransactionHistory extends JFrame {
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private DatabaseManager db;
    private int customerID;

    public CustTransactionHistory(int customerID) {
        this.customerID = customerID;

        setTitle("Transaction History");
        setSize(900, 600);
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

        String[] columns = {"Transaction ID", "Cart ID", "Subtotal", "Final Amount", "Date", "Actions", "Review"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5 || column == 6; 
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

            String query = "SELECT t.*, CASE WHEN r.rating IS NOT NULL THEN 'Already Reviewed' ELSE 'Write Review' END as review_status " +
                          "FROM transactions t " +
                          "LEFT JOIN reviews r ON t.transaction_id = r.transaction_id AND r.user_id = ? " +
                          "WHERE t.user_id = ? ORDER BY t.transaction_date DESC";
            
            PreparedStatement stmt = db.con.prepareStatement(query);
            stmt.setInt(1, customerID);
            stmt.setInt(2, customerID);
            ResultSet rs = stmt.executeQuery();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("transaction_id"),
                    rs.getInt("cart_id"),
                    String.format("$%.2f", rs.getDouble("sub_total")),
                    String.format("$%.2f", rs.getDouble("final_amount")),
                    dateFormat.format(rs.getDate("transaction_date")),
                    "Request Return",
                    rs.getString("review_status")
                };
                tableModel.addRow(row);
            }

            // Set up button renderers and editors for both Actions and Review columns
            transactionTable.getColumn("Actions").setCellRenderer(new ButtonRenderer());
            transactionTable.getColumn("Actions").setCellEditor(new ButtonEditor(new JCheckBox(), customerID, "return"));
            
            transactionTable.getColumn("Review").setCellRenderer(new ButtonRenderer());
            transactionTable.getColumn("Review").setCellEditor(new ButtonEditor(new JCheckBox(), customerID, "review"));

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

    private static class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value == null ? "" : value.toString());
            return this;
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private int customerID;
        private String actionType;

        public ButtonEditor(JCheckBox checkBox, int customerID, String actionType) {
            super(checkBox);
            this.customerID = customerID;
            this.actionType = actionType;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                int row = transactionTable.getSelectedRow();
                int transactionID = (int) tableModel.getValueAt(row, 0);
                
                if (actionType.equals("return")) {
                    requestReturn(transactionID);
                } else if (actionType.equals("review")) {
                    if (!label.equals("Already Reviewed")) {
                        new CustomerReviewFrame(customerID, transactionID);
                        loadTransactions(); // Refresh the table after opening review window
                    }
                }
            }
            isPushed = false;
            return label;
        }

        private void requestReturn(int transactionID) {
            try {
                db = DatabaseManager.getInstance();
                db.connect();

                String query = "INSERT INTO return_requests (transaction_id, user_id, request_date) VALUES (?, ?, ?)";
                PreparedStatement stmt = db.con.prepareStatement(query);
                stmt.setInt(1, transactionID);
                stmt.setInt(2, customerID);
                stmt.setDate(3, new java.sql.Date(System.currentTimeMillis()));
                stmt.executeUpdate();
                stmt.close();

                JOptionPane.showMessageDialog(null, "Return request submitted successfully.");
                db.disconnect();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error submitting return request: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}