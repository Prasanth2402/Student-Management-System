import java.sql.*;
import java.time.LocalDate;

/** Student Registration: full CRUD using PreparedStatement. */
public class StudentDAO {

    public int addStudent(String name, String email, String phone, LocalDate dob) throws SQLException {
        String sql = "INSERT INTO students (name, email, phone, dob) VALUES (?, ?, ?, ?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setDate(4, Date.valueOf(dob));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    public void viewAll() throws SQLException {
        String sql = "SELECT student_id, name, email, phone, dob FROM students ORDER BY student_id";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.printf("%-4s %-20s %-26s %-12s %-10s%n", "ID", "Name", "Email", "Phone", "DOB");
            while (rs.next()) {
                System.out.printf("%-4d %-20s %-26s %-12s %-10s%n",
                    rs.getInt("student_id"), rs.getString("name"), rs.getString("email"),
                    rs.getString("phone"), rs.getDate("dob"));
            }
        }
    }

    public boolean updateStudent(int id, String name, String email, String phone) throws SQLException {
        String sql = "UPDATE students SET name = ?, email = ?, phone = ? WHERE student_id = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        }
    }

    /** Enrollments, attendance and marks are removed automatically (ON DELETE CASCADE). */
    public boolean deleteStudent(int id) throws SQLException {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM students WHERE student_id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
