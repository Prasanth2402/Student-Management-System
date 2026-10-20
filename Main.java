import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    static final Scanner in = new Scanner(System.in);
    static final StudentDAO students = new StudentDAO();
    static final CourseDAO courses = new CourseDAO();
    static final EnrollmentDAO enrollments = new EnrollmentDAO();
    static final AttendanceDAO attendance = new AttendanceDAO();
    static final MarksDAO marks = new MarksDAO();
    static final ResultDAO results = new ResultDAO();

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n===== STUDENT MANAGEMENT SYSTEM =====");
            System.out.println(" 1. Register student        2. View students");
            System.out.println(" 3. Update student          4. Delete student");
            System.out.println(" 5. Add course              6. View courses");
            System.out.println(" 7. Enroll in courses       8. View enrollments");
            System.out.println(" 9. Mark attendance        10. View attendance");
            System.out.println("11. Enter marks            12. Generate result");
            System.out.println(" 0. Exit");
            int choice = readInt("Choice: ");
            try {
                switch (choice) {
                    case 1 -> {
                        String name = readLine("Name: ");
                        String email = readLine("Email: ");
                        String phone = readLine("Phone: ");
                        LocalDate dob = LocalDate.parse(readLine("DOB (yyyy-mm-dd): "));
                        System.out.println("Registered with ID " + students.addStudent(name, email, phone, dob));
                    }
                    case 2 -> students.viewAll();
                    case 3 -> {
                        int id = readInt("Student ID: ");
                        boolean ok = students.updateStudent(id, readLine("New name: "),
                            readLine("New email: "), readLine("New phone: "));
                        System.out.println(ok ? "Updated." : "Student not found.");
                    }
                    case 4 -> System.out.println(students.deleteStudent(readInt("Student ID: "))
                            ? "Deleted." : "Student not found.");
                    case 5 -> {
                        String name = readLine("Course name: ");
                        int weeks = readInt("Duration (weeks): ");
                        BigDecimal fee = new BigDecimal(readLine("Fee: "));
                        System.out.println("Added with ID " + courses.addCourse(name, weeks, fee));
                    }
                    case 6 -> courses.viewAll();
                    case 7 -> {
                        int sid = readInt("Student ID: ");
                        String[] parts = readLine("Course IDs (comma separated): ").split(",");
                        int[] ids = new int[parts.length];
                        for (int i = 0; i < parts.length; i++) ids[i] = Integer.parseInt(parts[i].trim());
                        enrollments.enrollInCourses(sid, ids);
                    }
                    case 8 -> enrollments.viewEnrollments();
                    case 9 -> {
                        int eid = readInt("Enrollment ID: ");
                        LocalDate d = LocalDate.parse(readLine("Date (yyyy-mm-dd): "));
                        char st = readLine("Status (P/A): ").toUpperCase().charAt(0);
                        attendance.markAttendance(eid, d, st);
                        System.out.println("Attendance saved.");
                    }
                    case 10 -> attendance.viewStudentAttendance(readInt("Student ID: "));
                    case 11 -> {
                        int eid = readInt("Enrollment ID: ");
                        String subject = readLine("Subject: ");
                        BigDecimal got = new BigDecimal(readLine("Marks obtained: "));
                        BigDecimal max = new BigDecimal(readLine("Max marks: "));
                        marks.enterMarks(eid, subject, got, max);
                        System.out.println("Marks saved.");
                    }
                    case 12 -> results.generateResult(readInt("Student ID: "));
                    case 0 -> { System.out.println("Goodbye!"); return; }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("Invalid input: " + e.getMessage());
            }
        }
    }

    static String readLine(String prompt) { System.out.print(prompt); return in.nextLine().trim(); }

    static int readInt(String prompt) {
        while (true) {
            try { return Integer.parseInt(readLine(prompt)); }
            catch (NumberFormatException e) { System.out.println("Please enter a number."); }
        }
    }
}
