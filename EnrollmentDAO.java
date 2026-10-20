import java.sql.*;

/** Course Enrollment: demonstrates a JDBC transaction and a JOIN. */
public class EnrollmentDAO {

    /**
     * Enrolls a student in several courses as ONE unit of work.
     * If any insert fails (bad course id, duplicate enrollment) everything is rolled back.
     */
    public void enrollInCourses(int studentId, int[] courseIds) throws SQLException {
        String sql = "INSERT INTO enrollments (student_id, course_id) VALUES (?, ?)";
        Connection c = null;
        try {
            c = DBUtil.getConnection();
            c.setAutoCommit(false);                       // begin transaction
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                for (int courseId : courseIds) {
                    ps.setInt(1, studentId);
                    ps.setInt(2, courseId);
                    ps.executeUpdate();
                }
            }
            c.commit();
            System.out.println("Enrolled in " + courseIds.length + " course(s).");
        } catch (SQLException e) {
            if (c != null) {
                c.rollback();
                System.out.println("Transaction rolled back, no enrollments were saved.");
            }
            throw e;
        } finally {
            if (c != null) {
                c.setAutoCommit(true);
                c.close();
            }
        }
    }

    /** JOIN across students, enrollments and courses. */
    public void viewEnrollments() throws SQLException {
        String sql = "SELECT e.enrollment_id, s.student_id, s.name, c.course_name, e.enrolled_on "
                   + "FROM enrollments e "
                   + "JOIN students s ON s.student_id = e.student_id "
                   + "JOIN courses  c ON c.course_id  = e.course_id "
                   + "ORDER BY s.name, c.course_name";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.printf("%-8s %-8s %-20s %-24s %-10s%n",
                "EnrollID", "StudID", "Student", "Course", "Enrolled");
            while (rs.next()) {
                System.out.printf("%-8d %-8d %-20s %-24s %-10s%n", rs.getInt(1), rs.getInt(2),
                    rs.getString(3), rs.getString(4), rs.getDate(5));
            }
        }
    }

    public boolean unenroll(int enrollmentId) throws SQLException {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM enrollments WHERE enrollment_id = ?")) {
            ps.setInt(1, enrollmentId);
            return ps.executeUpdate() > 0;
        }
    }
}
