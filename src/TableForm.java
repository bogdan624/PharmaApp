import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class TableForm extends JFrame {
    private JTable dateTabel;
    private JComboBox<String> btnSelectTable;
    private DefaultTableModel tableModel;
    private JButton btnDelete;
    private JButton btnEdit;
    private JComboBox<String> queryComboBox;
    private String currentTable;

    public TableForm() {
        setTitle("Table Viewer");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };

        dateTabel = new JTable(tableModel);
        dateTabel.setAutoCreateRowSorter(true);

        btnSelectTable = new JComboBox<>(new String[]{"client", "medicament", "furnizor", "categorie", "producator", "vanzare", "vanzare_medicament"});
        btnDelete = new JButton("Delete");
        btnEdit = new JButton("Edit");

        // ComboBox pentru query-uri
        queryComboBox = new JComboBox<>(new String[]{
                "1. Medicamente și furnizori",
                "2. Clienți și număr vânzări",
                "3. Medicamente și categorii",
                "4. Vânzări și clienți",
                "5. Medicamente nevândute",
                "6. Furnizori și număr medicamente",
                "7. Top 5 medicamente cu cele mai mari vânzări",
                "8. Clienți cu peste 3 vânzări într-un an",
                "9. Furnizori care furnizează mai multe categorii",
                "10. Vânzări totale per lună pentru ultimul an"
        });

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(btnDelete);
        bottomPanel.add(btnEdit);
        bottomPanel.add(queryComboBox);

        setLayout(new BorderLayout());
        add(btnSelectTable, BorderLayout.NORTH);
        add(new JScrollPane(dateTabel), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        btnSelectTable.addActionListener(e -> {
            currentTable = (String) btnSelectTable.getSelectedItem();
            if (currentTable != null) {
                showTable(currentTable);
            }
        });

        btnDelete.addActionListener(e -> deleteSelectedRow());
        btnEdit.addActionListener(e -> editSelectedRow());

        queryComboBox.addActionListener(e -> executeSelectedQuery());

        setVisible(true);
    }

    private void showTable(String tableName) {
        final String DB_URL = "jdbc:mysql://127.0.0.1:3306/farmacie";
        final String USERNAME = "root";
        final String PASSWORD = "1234";

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            String[] columnNames = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnNames[i - 1] = metaData.getColumnName(i);
            }

            tableModel.setColumnIdentifiers(columnNames);
            tableModel.setRowCount(0);

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
            Object primaryKeyValue = tableModel.getValueAt(selectedRow, 0);

            String sql = "DELETE FROM " + currentTable + " WHERE " + tableModel.getColumnName(0) + " = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setObject(1, primaryKeyValue);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
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
            int columnCount = tableModel.getColumnCount();
            Object[] updatedRow = new Object[columnCount];
            for (int i = 0; i < columnCount; i++) {
                updatedRow[i] = tableModel.getValueAt(selectedRow, i);
            }

            Object originalPrimaryKey = tableModel.getValueAt(selectedRow, 0);

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

    private void executeSelectedQuery() {
        int selectedQueryIndex = queryComboBox.getSelectedIndex(); // Obține indexul query-ului selectat
        String query = "";

        switch (selectedQueryIndex) {
            case 0 -> query = """
                SELECT m.id_medicament, m.nume, f.nume_furnizor
                FROM medicament m
                JOIN furnizor f ON m.id_furnizor = f.id_furnizor;
            """;
            case 1 -> query = """
                SELECT c.id_client, c.nume_client, COUNT(v.id_vanzare) AS numar_vanzari
                FROM client c
                LEFT JOIN vanzare v ON c.id_client = v.id_client
                GROUP BY c.id_client, c.nume_client;
            """;
            case 2 -> query = """
                SELECT m.id_medicament, m.nume, c.nume_categorie AS categorie
                FROM medicament m
                JOIN categorie c ON m.id_categorie = c.id_categorie;
            """;
            case 3 -> query = """
                SELECT v.id_vanzare, v.data_vanzare, c.nume_client AS client
                FROM vanzare v
                JOIN client c ON v.id_client = c.id_client;
            """;
            case 4 -> query = """
                SELECT m.id_medicament, m.nume
                FROM medicament m
                LEFT JOIN vanzare_medicament vm ON m.id_medicament = vm.id_medicament
                WHERE vm.id_medicament IS NULL;
            """;
            case 5 -> query = """
                SELECT f.id_furnizor, f.nume_furnizor, COUNT(m.id_medicament) AS numar_medicamente
                FROM furnizor f
                LEFT JOIN medicament m ON f.id_furnizor = m.id_furnizor
                GROUP BY f.id_furnizor, f.nume_furnizor;
            """;
            case 6 -> query = """
                SELECT m.id_medicament, m.nume, SUM(vm.cantitate * m.pret) AS valoare_totala
                FROM medicament m
                JOIN vanzare_medicament vm ON m.id_medicament = vm.id_medicament
                GROUP BY m.id_medicament, m.nume
                ORDER BY valoare_totala DESC
                LIMIT 5;
            """;
            case 7 -> query = """
                SELECT c.id_client, c.nume_client, YEAR(v.data_vanzare) AS an, COUNT(v.id_vanzare) AS numar_vanzari
                FROM client c
                JOIN vanzare v ON c.id_client = v.id_client
                GROUP BY c.id_client, c.nume_client, YEAR(v.data_vanzare)
                HAVING COUNT(v.id_vanzare) > 3;
            """;
            case 8 -> query = """
                SELECT f.id_furnizor, f.nume_furnizor, COUNT(DISTINCT m.id_categorie) AS numar_categorii
                FROM furnizor f
                JOIN medicament m ON f.id_furnizor = m.id_furnizor
                GROUP BY f.id_furnizor, f.nume_furnizor
                HAVING COUNT(DISTINCT m.id_categorie) > 1;
            """;
            case 9 -> query = """
                SELECT DATE_FORMAT(v.data_vanzare, '%Y-%m') AS luna, SUM(vm.cantitate * m.pret) AS total_vanzari
                FROM vanzare v
                JOIN vanzare_medicament vm ON v.id_vanzare = vm.id_vanzare
                JOIN medicament m ON vm.id_medicament = m.id_medicament
                WHERE v.data_vanzare >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR)
                GROUP BY DATE_FORMAT(v.data_vanzare, '%Y-%m')
                ORDER BY luna ASC;
            """;

            default -> {
                JOptionPane.showMessageDialog(this, "Selectați un query valid!", "Eroare", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        executeCustomQuery(query);
    }

    private void executeCustomQuery(String query) {
        final String DB_URL = "jdbc:mysql://127.0.0.1:3306/farmacie";
        final String USERNAME = "root";
        final String PASSWORD = "1234";

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            String[] columnNames = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnNames[i - 1] = metaData.getColumnName(i);
            }

            tableModel.setColumnIdentifiers(columnNames);
            tableModel.setRowCount(0);

            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Eroare la executarea query-ului", "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new TableForm();
    }
}
