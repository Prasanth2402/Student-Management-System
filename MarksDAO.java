import java.math.BigDecimal;
import java.sql.*;

public class MarksDAO {

    /** Inserts marks, or updates them if the subject was already entered. */
    public void enterMarks(int enrollmentId, String subject, BigDecimal obtained, BigDecimal max) throws SQLException {
        String sql = "INSERT INTO marks (enrollment_id, subject, marks_obtained, max_marks) VALUES (?, ?, ?, ?) "
                   + "ON DUPLICATE KEY UPDATE marks_obtained = VALUES(marks_obtained), max_marks = VALUES(max_marks)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            ps.setString(2, subject);
            ps.setBigDecimal(3, obtained);
            ps.setBigDecimal(4, max);
            ps.executeUpdate();
        }
    }

    public void viewMarks(int enrollmentId) throws SQLException {
        String sql = "SELECT subject, marks_obtained, max_marks FROM marks WHERE enrollment_id = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            try (ResultSet rs = ps.executeQuery()) {
                System.out.printf("%-20s %-10s %-10s%n", "Subject", "Obtained", "Max");
                while (rs.next()) {
                    System.out.printf("%-20s %-10s %-10s%n", rs.getString(1), rs.getBigDecimal(2), rs.getBigDecimal(3));
                }
            }
        }
    }
}
