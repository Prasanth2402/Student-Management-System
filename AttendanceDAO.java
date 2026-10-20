import java.sql.*;
import java.time.LocalDate;

public class AttendanceDAO {

    /** status: 'P' (present) or 'A' (absent). Re-marking the same day updates the record. */
    public void markAttendance(int enrollmentId, LocalDate date, char status) throws SQLException {
        String sql = "INSERT INTO attendance (enrollment_id, att_date, status) VALUES (?, ?, ?) "
                   + "ON DUPLICATE KEY UPDATE status = VALUES(status)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            ps.setDate(2, Date.valueOf(date));
            ps.setString(3, String.valueOf(Character.toUpperCase(status)));
            ps.executeUpdate();
        }
    }

    /** Attendance summary per course for one student (JOIN + aggregates). */
    public void viewStudentAttendance(int studentId) throws SQLException {
        String sql = "SELECT c.course_name, COUNT(*) AS total_days, SUM(a.status = 'P') AS present_days, "
                   + "ROUND(100 * SUM(a.status = 'P') / COUNT(*), 1) AS pct "
                   + "FROM attendance a "
                   + "JOIN enrollments e ON e.enrollment_id = a.enrollment_id "
                   + "JOIN courses c ON c.course_id = e.course_id "
                   + "WHERE e.student_id = ? GROUP BY c.course_name";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                System.out.printf("%-24s %-6s %-8s %-8s%n", "Course", "Days", "Present", "%");
                boolean any = false;
                while (rs.next()) {
                    any = true;
                    System.out.printf("%-24s %-6d %-8d %-8.1f%n", rs.getString(1), rs.getInt(2),
                        rs.getInt(3), rs.getDouble(4));
                }
                if (!any) System.out.println("No attendance records found.");
            }
        }
    }
}
