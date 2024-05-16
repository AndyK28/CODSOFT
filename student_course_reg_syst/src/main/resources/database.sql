CREATE TABLE IF NOT EXISTS courses (
    courseCode VARCHAR(10) PRIMARY KEY,
    title VARCHAR(100),
    description VARCHAR(255),
    capacity INT
);

CREATE TABLE IF NOT EXISTS course_code (
    courseCode VARCHAR(10) PRIMARY KEY,
    title VARCHAR(100),
    FOREIGN KEY (courseCode) REFERENCES courses(courseCode)
);

CREATE TABLE IF NOT EXISTS students (
    studentID VARCHAR(7) PRIMARY KEY,
    name VARCHAR(100),
    surname VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS registrations (
    registrationID INTEGER PRIMARY KEY AUTOINCREMENT,
    studentID INT,
    courseCode VARCHAR(10),
    FOREIGN KEY (studentID) REFERENCES students(studentID),
    FOREIGN KEY (courseCode) REFERENCES courses(courseCode)
);

CREATE TABLE IF NOT EXISTS schedule (
    scheduleID INTEGER PRIMARY KEY AUTOINCREMENT,
    courseCode VARCHAR(10),
    days VARCHAR(10),
    startTime TIME,
    endTime TIME,
    FOREIGN KEY (courseCode) REFERENCES courses(courseCode)
);
