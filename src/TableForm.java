import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class TableForm extends JFrame {
    private JTable dateTabel;
    private JComboBox<String> btnSelectTable;
    private DefaultTableModel tableModel;
    private JButton btnDelete;
    private JButton btnEdit;
    private String currentTable;

    public TableForm() {
        setTitle("Table Viewer");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize components
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Allow editing all cells, including the first column
                return true;
            }
        };

        dateTabel = new JTable(tableModel);
        dateTabel.setAutoCreateRowSorter(true); // Enable sorting

        btnSelectTable = new JComboBox<>(new String[]{"client", "medicament", "furnizor", "categorie", "producator", "vanzare", "vanzare_medicament"});
        btnDelete = new JButton("Delete");
        btnEdit = new JButton("Edit");

        // Layout
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(btnDelete);
        bottomPanel.add(btnEdit);

        setLayout(new BorderLayout());
        add(btnSelectTable, BorderLayout.NORTH);
        add(new JScrollPane(dateTabel), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Add ActionListener for table selection
        btnSelectTable.addActionListener(e -> {
            currentTable = (String) btnSelectTable.getSelectedItem();
            if (currentTable != null) {
                showTable(currentTable);
            }
        });

        // Add ActionListener for delete button
        btnDelete.addActionListener(e -> deleteSelectedRow());

        // Add ActionListener for edit button
        btnEdit.addActionListener(e -> editSelectedRow());

        setVisible(true);
    }

    private void showTable(String tableName) {
        final String DB_URL = "jdbc:mysql://127.0.0.1:3306/farmacie";
        final String USERNAME = "root";
        final String PASSWORD = "1234";

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {

            // Get column names
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            String[] columnNames = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnNames[i - 1] = metaData.getColumnName(i);
            }

            // Set table headers
            tableModel.setColumnIdentifiers(columnNames);

            // Clear existing data
            tableModel.setRowCount(0);

            // Add rows to the table model
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve data", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedRow() {
        int selectedRow = dateTabel.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "No row selected", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        final String DB_URL = "jdbc:mysql://127.0.0.1:3306/farmacie";
        final String USERNAME = "root";
        final String PASSWORD = "1234";

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            // Get primary key value (first column)
            Object primaryKeyValue = tableModel.getValueAt(selectedRow, 0);

            // Delete from the database
            String sql = "DELETE FROM " + currentTable + " WHERE " + tableModel.getColumnName(0) + " = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setObject(1, primaryKeyValue);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    // Remove from table model
                    tableModel.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(this, "Row deleted successfully");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete row", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSelectedRow() {
        int selectedRow = dateTabel.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "No row selected", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        final String DB_URL = "jdbc:mysql://127.0.0.1:3306/farmacie";
        final String USERNAME = "root";
        final String PASSWORD = "1234";

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            // Get updated values from the table
            int columnCount = tableModel.getColumnCount();
            Object[] updatedRow = new Object[columnCount];
            for (int i = 0; i < columnCount; i++) {
                updatedRow[i] = tableModel.getValueAt(selectedRow, i);
            }

            // Assuming the first column (primary key) can also change
            Object originalPrimaryKey = tableModel.getValueAt(selectedRow, 0);

            // Build update query
            StringBuilder sql = new StringBuilder("UPDATE " + currentTable + " SET ");
            for (int i = 0; i < columnCount; i++) {
                sql.append(tableModel.getColumnName(i)).append(" = ?");
                if (i < columnCount - 1) {
                    sql.append(", ");
                }
            }
            sql.append(" WHERE ").append(tableModel.getColumnName(0)).append(" = ?");

            try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                for (int i = 0; i < columnCount; i++) {
                    pstmt.setObject(i + 1, updatedRow[i]);
                }
                pstmt.setObject(columnCount + 1, originalPrimaryKey);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Row updated successfully");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update row", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        TableForm myForm = new TableForm();
    }
}
