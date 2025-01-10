package GUI;

import Database.DatabaseManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.ArrayList;
import java.util.List;

public class PromoManagementFrame extends JFrame {
    private JTable promoTable;
    private DefaultTableModel tableModel;
    private final DatabaseManager db = DatabaseManager.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final Color IKEA_BLUE = new Color(0, 51, 153);
    private final Color IKEA_YELLOW = new Color(248, 209, 21);
    private final Color IKEA_LIGHT_BLUE = new Color(0, 105, 255);
    private Timer expirationTimer;
    private static final int CHECK_INTERVAL = 3600000;

    public PromoManagementFrame() {
        setTitle("IKEA - Promo Management");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                        0, 0, IKEA_BLUE,
                        getWidth(), getHeight(), IKEA_LIGHT_BLUE);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("Promo Management", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);

        String[] columns = { "ID", "Name", "Description", "Discount (%)", "Expiration Date" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        promoTable = new JTable(tableModel);

        promoTable.setForeground(IKEA_BLUE);
        promoTable.setBackground(Color.WHITE);
        promoTable.setFont(new Font("Arial", Font.PLAIN, 14));
        promoTable.setRowHeight(30);
        promoTable.setSelectionBackground(IKEA_LIGHT_BLUE);
        promoTable.setSelectionForeground(Color.WHITE);
        promoTable.setShowGrid(true);
        promoTable.setGridColor(new Color(230, 230, 230));

        JTableHeader header = promoTable.getTableHeader();
        header.setBackground(IKEA_BLUE);
        header.setForeground(IKEA_YELLOW);
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));

        JScrollPane scrollPane = new JScrollPane(promoTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(IKEA_BLUE));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setOpaque(false);

        JButton addButton = createStyledButton("Add Promo", true);
        JButton editButton = createStyledButton("Edit Promo", true);
        JButton deleteButton = createStyledButton("Delete Promo", false);
        JButton refreshButton = createStyledButton("Refresh", true);

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> showAddPromoDialog());
        editButton.addActionListener(e -> {
            int selectedRow = promoTable.getSelectedRow();
            if (selectedRow != -1) {
                showEditPromoDialog(selectedRow);
            } else {
                showErrorMessage("Please select a promo to edit");
            }
        });
        deleteButton.addActionListener(e -> {
            int selectedRow = promoTable.getSelectedRow();
            if (selectedRow != -1) {
                deletePromo(selectedRow);
            } else {
                showErrorMessage("Please select a promo to delete");
            }
        });
        refreshButton.addActionListener(e -> refreshPromoTable());

        add(mainPanel);
        setupExpirationCheck();
        refreshPromoTable();
        setVisible(true);
    }

    private JButton createStyledButton(String text, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(150, 40));

        if (isPrimary) {
            button.setBackground(IKEA_BLUE);
            button.setForeground(IKEA_YELLOW);
        } else {
            button.setBackground(Color.WHITE);
            button.setForeground(IKEA_BLUE);
        }

        button.setBorder(BorderFactory.createLineBorder(IKEA_BLUE, 2));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (isPrimary) {
                    button.setBackground(IKEA_LIGHT_BLUE);
                } else {
                    button.setBackground(new Color(240, 240, 240));
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (isPrimary) {
                    button.setBackground(IKEA_BLUE);
                } else {
                    button.setBackground(Color.WHITE);
                }
            }
        });

        return button;
    }

    private void showStyledDialog(JDialog dialog) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                        0, 0, IKEA_BLUE,
                        getWidth(), getHeight(), IKEA_LIGHT_BLUE);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        dialog.setContentPane(mainPanel);
    }

    private void showAddPromoDialog() {
        JDialog dialog = new JDialog(this, "Add New Promo", true);
        dialog.setSize(400, 300);
        showStyledDialog(dialog);

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setOpaque(false);

        JLabel[] labels = {
                createStyledLabel("Promo Name:"),
                createStyledLabel("Description:"),
                createStyledLabel("Discount (%):"),
                createStyledLabel("Expiration Date:")
        };

        JTextField nameField = createStyledTextField();
        JTextField descField = createStyledTextField();
        JTextField discountField = createStyledTextField();
        JTextField dateField = createStyledTextField();
        dateField.setToolTipText("Format: YYYY-MM-DD");

        for (int i = 0; i < labels.length; i++) {
            inputPanel.add(labels[i]);
            inputPanel.add(i == 0 ? nameField : i == 1 ? descField : i == 2 ? discountField : dateField);
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        JButton saveButton = createStyledButton("Save", true);
        JButton cancelButton = createStyledButton("Cancel", false);

        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                String description = descField.getText();
                double discount = Double.parseDouble(discountField.getText());
                Date expirationDate = dateFormat.parse(dateField.getText());

                if (name.isEmpty() || description.isEmpty()) {
                    showErrorMessage("Please fill all fields");
                    return;
                }

                if (discount <= 0 || discount > 100) {
                    showErrorMessage("Discount must be between 0 and 100");
                    return;
                }

                if (addPromo(name, description, discount, expirationDate)) {
                    dialog.dispose();
                    refreshPromoTable();
                }
            } catch (ParseException ex) {
                showErrorMessage("Invalid date format. Please use YYYY-MM-DD");
            } catch (NumberFormatException ex) {
                showErrorMessage("Invalid discount value");
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.getContentPane().add(inputPanel, BorderLayout.CENTER);
        dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showEditPromoDialog(int row) {
        JDialog dialog = new JDialog(this, "Edit Promo", true);
        dialog.setSize(400, 300);
        showStyledDialog(dialog);

        int promoId = (int) promoTable.getValueAt(row, 0);

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setOpaque(false);

        JLabel[] labels = {
                createStyledLabel("Promo Name:"),
                createStyledLabel("Description:"),
                createStyledLabel("Discount (%):"),
                createStyledLabel("Expiration Date:")
        };

        JTextField nameField = createStyledTextField();
        JTextField descField = createStyledTextField();
        JTextField discountField = createStyledTextField();
        JTextField dateField = createStyledTextField();

        nameField.setText(promoTable.getValueAt(row, 1).toString());
        descField.setText(promoTable.getValueAt(row, 2).toString());
        discountField.setText(promoTable.getValueAt(row, 3).toString());
        dateField.setText(promoTable.getValueAt(row, 4).toString());

        for (int i = 0; i < labels.length; i++) {
            inputPanel.add(labels[i]);
            inputPanel.add(i == 0 ? nameField : i == 1 ? descField : i == 2 ? discountField : dateField);
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        JButton saveButton = createStyledButton("Save", true);
        JButton cancelButton = createStyledButton("Cancel", false);

        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                String description = descField.getText();
                double discount = Double.parseDouble(discountField.getText());
                Date expirationDate = dateFormat.parse(dateField.getText());

                if (name.isEmpty() || description.isEmpty()) {
                    showErrorMessage("Please fill all fields");
                    return;
                }

                if (discount <= 0 || discount > 100) {
                    showErrorMessage("Discount must be between 0 and 100");
                    return;
                }

                if (updatePromo(promoId, name, description, discount, expirationDate)) {
                    dialog.dispose();
                    refreshPromoTable();
                }
            } catch (ParseException ex) {
                showErrorMessage("Invalid date format. Please use YYYY-MM-DD");
            } catch (NumberFormatException ex) {
                showErrorMessage("Invalid discount value");
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.getContentPane().add(inputPanel, BorderLayout.CENTER);
        dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(Color.WHITE);
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(IKEA_BLUE),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return textField;
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private void refreshPromoTable() {
        tableModel.setRowCount(0);
        try {
            db.connect();
            Statement stmt = db.con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM promos");

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("promo_id"));
                row.add(rs.getString("promo_name"));
                row.add(rs.getString("description"));
                row.add(rs.getDouble("discount"));
                row.add(rs.getDate("expiration_date"));
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading promos: " + e.getMessage());
        } finally {
            db.disconnect();
        }
    }

    private boolean addPromo(String name, String description, double discount, Date expirationDate) {
        try {
            db.connect();
            String query = "INSERT INTO promos (promo_name, description, discount, expiration_date) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = db.con.prepareStatement(query);
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.setDouble(3, discount);
            pstmt.setDate(4, new java.sql.Date(expirationDate.getTime()));

            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding promo: " + e.getMessage());
            return false;
        } finally {
            db.disconnect();
        }
    }

    private boolean updatePromo(int promoId, String name, String description, double discount, Date expirationDate) {
        try {
            db.connect();
            String query = "UPDATE promos SET promo_name=?, description=?, discount=?, expiration_date=? WHERE promo_id=?";
            PreparedStatement pstmt = db.con.prepareStatement(query);
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.setDouble(3, discount);
            pstmt.setDate(4, new java.sql.Date(expirationDate.getTime()));
            pstmt.setInt(5, promoId);

            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating promo: " + e.getMessage());
            return false;
        } finally {
            db.disconnect();
        }
    }

    private void deletePromo(int row) {
        int promoId = (int) promoTable.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this promo?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                db.connect();
                String query = "DELETE FROM promos WHERE promo_id=?";
                PreparedStatement pstmt = db.con.prepareStatement(query);
                pstmt.setInt(1, promoId);

                int result = pstmt.executeUpdate();
                if (result > 0) {
                    refreshPromoTable();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting promo: " + e.getMessage());
            } finally {
                db.disconnect();
            }
        }
    }

    private void setupExpirationCheck() {
        deleteExpiredPromos();
        
        expirationTimer = new Timer(CHECK_INTERVAL, e -> deleteExpiredPromos());
        expirationTimer.start();
    }
    
    private void deleteExpiredPromos() {
        try {
            db.connect();
            java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
            
            String selectQuery = "SELECT promo_id, promo_name FROM promos WHERE expiration_date < ?";
            PreparedStatement selectStmt = db.con.prepareStatement(selectQuery);
            selectStmt.setDate(1, currentDate);
            ResultSet rs = selectStmt.executeQuery();
            
            List<Integer> promoIdsToDelete = new ArrayList<>();
            List<String> promoNamesToDelete = new ArrayList<>();
            
            while (rs.next()) {
                promoIdsToDelete.add(rs.getInt("promo_id"));
                promoNamesToDelete.add(rs.getString("promo_name"));
            }
            
            if (promoIdsToDelete.isEmpty()) {
                return;
            }
            
            for (int i = 0; i < promoIdsToDelete.size(); i++) {
                int promoId = promoIdsToDelete.get(i);
                String promoName = promoNamesToDelete.get(i);
                
                String orderCheckQuery = "SELECT COUNT(*) FROM orders WHERE promo_applied = ?";
                PreparedStatement orderCheckStmt = db.con.prepareStatement(orderCheckQuery);
                orderCheckStmt.setString(1, promoName);
                ResultSet orderRs = orderCheckStmt.executeQuery();
                orderRs.next();
                int activeOrderCount = orderRs.getInt(1);
                
                if (activeOrderCount > 0) {
                    System.out.println("Promo " + promoName + " is still in use in " + activeOrderCount + " orders. Skipping deletion.");
                    continue;
                }
                
                String deleteQuery = "DELETE FROM promos WHERE promo_id = ?";
                PreparedStatement deleteStmt = db.con.prepareStatement(deleteQuery);
                deleteStmt.setInt(1, promoId);
                deleteStmt.executeUpdate();
                
                System.out.println("Deleted expired promo: " + promoName);
            }
            
            refreshPromoTable();
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error handling expired promos: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            db.disconnect();
        }
    }
    
    @Override
    public void dispose() {
        if (expirationTimer != null) {
            expirationTimer.stop();
        }
        super.dispose();
    }
}