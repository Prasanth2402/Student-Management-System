# Student Management System (JDBC + MySQL)

## Setup
1. Run `schema.sql` in MySQL (creates DB `student_mgmt`, tables and sample data).
2. Set your MySQL password in `src/DBUtil.java`.
3. Download MySQL Connector/J (`mysql-connector-j-8.x.jar`).

## Compile and run (Java 17+)
    javac -d out src/*.java
    java -cp "out:mysql-connector-j-8.4.0.jar" Main        # Windows: use ; instead of :

## JDBC concepts covered
- CRUD: StudentDAO, CourseDAO
- PreparedStatement: every query
- Joins: EnrollmentDAO.viewEnrollments, AttendanceDAO, ResultDAO
- Transactions: EnrollmentDAO.enrollInCourses (commit / rollback)

## Result rules (ResultDAO)
Pass = overall marks >= 40% AND attendance >= 75% (or no attendance recorded).
