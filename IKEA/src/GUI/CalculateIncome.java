package GUI;

import Database.DatabaseManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalculateIncome extends JFrame {
    private final DatabaseManager db = DatabaseManager.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy");
    private final SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private JTable incomeTable;
    private double totalIncome = 0;
    private Calendar currentMonth;
    private JLabel monthLabel;
    private JLabel totalAmountLabel;

    public CalculateIncome() {
        setTitle("IKEA Marketplace - Monthly Income Overview");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Initialize current month to current date
        currentMonth = Calendar.getInstance();
        currentMonth.set(Calendar.DAY_OF_MONTH, 1); // Set to first day of month

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

        // Month Navigation Panel
        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        navigationPanel.setOpaque(false);

        JButton prevButton = new JButton("<-");
        styleNavigationButton(prevButton);
        prevButton.addActionListener(e -> changeMonth(-1));

        monthLabel = new JLabel(dateFormat.format(currentMonth.getTime()), JLabel.CENTER);
        monthLabel.setFont(new Font("Arial", Font.BOLD, 20));
        monthLabel.setForeground(new Color(248, 209, 21));
        monthLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JButton nextButton = new JButton("->");
        styleNavigationButton(nextButton);
        nextButton.addActionListener(e -> changeMonth(1));

        navigationPanel.add(prevButton);
        navigationPanel.add(monthLabel);
        navigationPanel.add(nextButton);
        mainPanel.add(navigationPanel, BorderLayout.NORTH);

        // Create table
        String[] columns = {"Date", "Transaction ID", "Sub Total", "Discount", "Final Amount"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        incomeTable = new JTable(model);
        incomeTable.setFillsViewportHeight(true);
        incomeTable.setBackground(Color.WHITE);
        incomeTable.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JScrollPane scrollPane = new JScrollPane(incomeTable);
        scrollPane.setBackground(Color.WHITE);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Summary Panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        summaryPanel.setOpaque(false);
        
        JLabel totalLabel = new JLabel("Monthly Income: $");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalLabel.setForeground(Color.WHITE);
        
        totalAmountLabel = new JLabel("0.00");
        totalAmountLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalAmountLabel.setForeground(new Color(248, 209, 21));
        
        summaryPanel.add(totalLabel);
        summaryPanel.add(totalAmountLabel);
        mainPanel.add(summaryPanel, BorderLayout.SOUTH);

        add(mainPanel);
        loadMonthlyIncomeData();
        setVisible(true);
    }

    private void styleNavigationButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(new Color(248, 209, 21));
        button.setBackground(new Color(0, 51, 153));
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(50, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void changeMonth(int delta) {
        currentMonth.add(Calendar.MONTH, delta);
        monthLabel.setText(dateFormat.format(currentMonth.getTime()));
        loadMonthlyIncomeData();
    }

    private void loadMonthlyIncomeData() {
        DefaultTableModel model = (DefaultTableModel) incomeTable.getModel();
        model.setRowCount(0); // Clear existing data
        totalIncome = 0;

        try {
            db.connect();
            
            // Create calendar for end of month
            Calendar endMonth = (Calendar) currentMonth.clone();
            endMonth.add(Calendar.MONTH, 1);
            endMonth.add(Calendar.DAY_OF_MONTH, -1);

            String query = "SELECT transaction_id, transaction_date, sub_total, discount_amount, final_amount " +
                          "FROM transactions " +
                          "WHERE transaction_date BETWEEN ? AND ? " +
                          "ORDER BY transaction_date DESC";

            PreparedStatement pstmt = db.con.prepareStatement(query);
            pstmt.setString(1, sqlDateFormat.format(currentMonth.getTime()));
            pstmt.setString(2, sqlDateFormat.format(endMonth.getTime()));
            
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    sqlDateFormat.format(rs.getDate("transaction_date")),
                    rs.getInt("transaction_id"),
                    String.format("$%.2f", rs.getDouble("sub_total")),
                    String.format("$%.2f", rs.getDouble("discount_amount")),
                    String.format("$%.2f", rs.getDouble("final_amount"))
                });
                totalIncome += rs.getDouble("final_amount");
            }

            totalAmountLabel.setText(String.format("%.2f", totalIncome));

            // Add message if no transactions
            if (model.getRowCount() == 0) {
                model.addRow(new Object[]{"No transactions found for this month", "", "", "", ""});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading income data: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            db.disconnect();
        }
    }
}