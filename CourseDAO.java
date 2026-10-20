import java.math.BigDecimal;
import java.sql.*;

public class CourseDAO {

    public int addCourse(String name, int weeks, BigDecimal fee) throws SQLException {
        String sql = "INSERT INTO courses (course_name, duration_weeks, fee) VALUES (?, ?, ?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setInt(2, weeks);
            ps.setBigDecimal(3, fee);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    public void viewAll() throws SQLException {
        String sql = "SELECT course_id, course_name, duration_weeks, fee FROM courses ORDER BY course_id";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.printf("%-4s %-26s %-8s %-10s%n", "ID", "Course", "Weeks", "Fee");
            while (rs.next()) {
                System.out.printf("%-4d %-26s %-8d %-10s%n", rs.getInt(1), rs.getString(2),
                    rs.getInt(3), rs.getBigDecimal(4));
            }
        }
    }
}
