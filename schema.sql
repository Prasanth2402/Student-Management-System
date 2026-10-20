-- Student Management System (MySQL 8+)
CREATE DATABASE IF NOT EXISTS student_mgmt;
USE student_mgmt;

DROP TABLE IF EXISTS marks, attendance, enrollments, courses, students;

CREATE TABLE students (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(100) NOT NULL UNIQUE,
    phone      VARCHAR(15),
    dob        DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE courses (
    course_id      INT AUTO_INCREMENT PRIMARY KEY,
    course_name    VARCHAR(100) NOT NULL UNIQUE,
    duration_weeks INT NOT NULL,
    fee            DECIMAL(10,2) NOT NULL
);

CREATE TABLE enrollments (
    enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id    INT NOT NULL,
    course_id     INT NOT NULL,
    enrolled_on   DATE NOT NULL DEFAULT (CURRENT_DATE),
    UNIQUE (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id)  REFERENCES courses(course_id)  ON DELETE CASCADE
);

CREATE TABLE attendance (
    attendance_id INT AUTO_INCREMENT PRIMARY KEY,
    enrollment_id INT NOT NULL,
    att_date      DATE NOT NULL,
    status        CHAR(1) NOT NULL CHECK (status IN ('P','A')),
    UNIQUE (enrollment_id, att_date),
    FOREIGN KEY (enrollment_id) REFERENCES enrollments(enrollment_id) ON DELETE CASCADE
);

CREATE TABLE marks (
    mark_id        INT AUTO_INCREMENT PRIMARY KEY,
    enrollment_id  INT NOT NULL,
    subject        VARCHAR(60) NOT NULL,
    marks_obtained DECIMAL(5,2) NOT NULL,
    max_marks      DECIMAL(5,2) NOT NULL DEFAULT 100,
    UNIQUE (enrollment_id, subject),
    CHECK (marks_obtained >= 0 AND marks_obtained <= max_marks),
    FOREIGN KEY (enrollment_id) REFERENCES enrollments(enrollment_id) ON DELETE CASCADE
);

-- Sample data
INSERT INTO students (name,email,phone,dob) VALUES
 ('Arun Kumar','arun@example.com','9840000001','2002-05-14'),
 ('Divya Lakshmi','divya@example.com','9840000002','2003-02-21'),
 ('Karthik Raj','karthik@example.com','9840000003','2001-11-09');

INSERT INTO courses (course_name,duration_weeks,fee) VALUES
 ('Java Full Stack',16,45000.00),
 ('Python & Data Science',12,38000.00),
 ('Web Development',10,25000.00);

INSERT INTO enrollments (student_id,course_id) VALUES (1,1),(1,3),(2,1),(3,2);

INSERT INTO attendance (enrollment_id,att_date,status) VALUES
 (1,'2026-10-01','P'),(1,'2026-10-02','P'),(1,'2026-10-03','A'),(1,'2026-10-04','P'),
 (3,'2026-10-01','P'),(3,'2026-10-02','A'),(3,'2026-10-03','A'),(3,'2026-10-04','P');

INSERT INTO marks (enrollment_id,subject,marks_obtained,max_marks) VALUES
 (1,'Core Java',82,100),(1,'JDBC',76,100),
 (3,'Core Java',45,100),(3,'JDBC',38,100);
