import java.awt.*;
import java.sql.*;
import java.util.List;
import javax.swing.*;

public class StudentDetailsFrame extends JFrame {
    private static final String JDBC_URL = "jdbc:mysql://vsrvfeia0h-64.vsb.cz:3306/students_db";
    private static final String USERNAME = "student";
    private static final String PASSWORD = "we_love_java";

    private List<String> studentIds;
    private int currentIndex;

    private JLabel idLabel;
    private JLabel firstNameLabel;
    private JLabel lastNameLabel;
    private JLabel emailLabel;
    private JLabel javaExamLabel;

    public StudentDetailsFrame(List<String> studentIds, int startIndex) {
        this.studentIds = studentIds;
        this.currentIndex = startIndex;

        setTitle("Student Details");
        setSize(400, 300);
        setLayout(new GridLayout(0, 1));

        idLabel = new JLabel();
        firstNameLabel = new JLabel();
        lastNameLabel = new JLabel();
        emailLabel = new JLabel();
        javaExamLabel = new JLabel();

        add(idLabel);
        add(firstNameLabel);
        add(lastNameLabel);
        add(emailLabel);
        add(javaExamLabel);

       
        JPanel buttonPanel = new JPanel();
        JButton firstButton = new JButton("First");
        JButton prevButton = new JButton("Previous");
        JButton nextButton = new JButton("Next");
        JButton lastButton = new JButton("Last");

        // Add action listeners for navigation
        firstButton.addActionListener(e -> showFirstStudent());
        prevButton.addActionListener(e -> showPreviousStudent());
        nextButton.addActionListener(e -> showNextStudent());
        lastButton.addActionListener(e -> showLastStudent());

        buttonPanel.add(firstButton);
        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(lastButton);
        add(buttonPanel);

        loadStudentDetails(studentIds.get(currentIndex));
        setVisible(true);
    }

    private void loadStudentDetails(String studentId) {
        String sql = "SELECT * FROM students WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, studentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    idLabel.setText("ID: " + resultSet.getString("id"));
                    firstNameLabel.setText("First Name: " + resultSet.getString("first_name"));
                    lastNameLabel.setText("Last Name: " + resultSet.getString("last_name"));
                    emailLabel.setText("Email: " + resultSet.getString("email"));
                    javaExamLabel.setText("Java Exam: " + resultSet.getString("java_exam"));
                }
                resultSet.close();
            }
            
            statement.close();
            connection.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching student details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showPreviousStudent() {
        if (currentIndex > 0) {
            currentIndex--;
            loadStudentDetails(studentIds.get(currentIndex));
        }
    }

    private void showNextStudent() {
        if (currentIndex < studentIds.size() - 1) {
            currentIndex++;
            loadStudentDetails(studentIds.get(currentIndex));
        }
    }
    private void showFirstStudent() {
        currentIndex = 0;
        loadStudentDetails(studentIds.get(currentIndex));
    }

    private void showLastStudent() {
        currentIndex = studentIds.size() - 1;
        loadStudentDetails(studentIds.get(currentIndex));
    }
}
