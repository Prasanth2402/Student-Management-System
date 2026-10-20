import java.sql.*;

/** Result Generation: joins students, courses, marks and attendance. */
public class ResultDAO {

    private static final double PASS_PERCENT = 40.0;        // minimum overall marks %
    private static final double MIN_ATTENDANCE = 75.0;      // minimum attendance %

    public void generateResult(int studentId) throws SQLException {
        String sql =
            "SELECT s.name, c.course_name, "
          + "  COALESCE(SUM(m.marks_obtained), 0) AS total, "
          + "  COALESCE(SUM(m.max_marks), 0)      AS max_total, "
          + "  (SELECT ROUND(100 * SUM(a.status = 'P') / COUNT(*), 1) "
          + "     FROM attendance a WHERE a.enrollment_id = e.enrollment_id) AS att_pct "
          + "FROM enrollments e "
          + "JOIN students s ON s.student_id = e.student_id "
          + "JOIN courses  c ON c.course_id  = e.course_id "
          + "LEFT JOIN marks m ON m.enrollment_id = e.enrollment_id "
          + "WHERE s.student_id = ? "
          + "GROUP BY e.enrollment_id, s.name, c.course_name";

        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                boolean any = false;
                while (rs.next()) {
                    if (!any) {
                        System.out.println("\n===== RESULT: " + rs.getString("name") + " =====");
                        System.out.printf("%-24s %-10s %-8s %-8s %-6s %-6s%n",
                            "Course", "Marks", "Percent", "Attend%", "Grade", "Result");
                    }
                    any = true;
                    double total = rs.getDouble("total");
                    double max = rs.getDouble("max_total");
                    double pct = max == 0 ? 0 : total * 100 / max;
                    double att = rs.getDouble("att_pct");   // 0 if no records (NULL)
                    boolean attNull = rs.wasNull();
                    String result = (max > 0 && pct >= PASS_PERCENT && (attNull || att >= MIN_ATTENDANCE))
                                    ? "PASS" : "FAIL";
                    System.out.printf("%-24s %-10s %-8.1f %-8s %-6s %-6s%n",
                        rs.getString("course_name"), (int) total + "/" + (int) max, pct,
                        attNull ? "N/A" : String.format("%.1f", att), grade(pct), result);
                }
                if (!any) System.out.println("Student not found or not enrolled in any course.");
            }
        }
    }

    private String grade(double pct) {
        if (pct >= 80) return "A";
        if (pct >= 65) return "B";
        if (pct >= 50) return "C";
        if (pct >= PASS_PERCENT) return "D";
        return "F";
    }
}
