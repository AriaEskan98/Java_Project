import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class StudentTable {

    private static final String JDBC_URL = "jdbc:mysql://vsrvfeia0h-64.vsb.cz:3306/students_db";
    private static final String USERNAME = "student";
    private static final String PASSWORD = "we_love_java";

    private JTable table;
    private DefaultTableModel tableModel;
    private List<String> studentIds = new ArrayList<>(); // List of all student IDs

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new StudentTable().createAndShowGUI();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Student List");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);

        tableModel = new DefaultTableModel(new String[]{"ID", "First Name", "Last Name", "Email", "Actions"}, 0);
        table = new JTable(tableModel);
        table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        table.getColumn("Actions").setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(table);

        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);

        loadStudents();
        frame.setVisible(true);
    }

    private void loadStudents() {
        tableModel.setRowCount(0); // Clear table
        studentIds.clear(); // Clear previous student IDs

        String sql = "SELECT * FROM students";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Vector<Object> row = new Vector<>();
                String id = resultSet.getString("id");
                studentIds.add(id); // Add student ID to the list
                row.add(id);
                row.add(resultSet.getString("first_name"));
                row.add(resultSet.getString("last_name"));
                row.add(resultSet.getString("email"));
                row.add("Show");
                tableModel.addRow(row);

            }
            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showStudentDetails(String studentId) {
        new StudentDetailsFrame(studentIds, studentIds.indexOf(studentId));
    }

    // Button Renderer and Editor (same as before)
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String studentId;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> {
                fireEditingStopped();
                showStudentDetails(studentId);
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            studentId = table.getValueAt(row, 0).toString();
            button.setText((value == null) ? "" : value.toString());
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return button.getText();
        }
    }
}
