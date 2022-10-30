package fi.huitsinnevada.Koulutyo;

import fi.huitsinnevada.Koulutyo.data.Course;
import fi.huitsinnevada.Koulutyo.data.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class FileService {
    private static Optional<Connection> getConnection() {
        try {
            return Optional.of(DriverManager.getConnection("jdbc:sqlite:./data.db"));
        } catch (SQLException e) {
            Logger.getGlobal().info(e.getMessage());
        }
        return Optional.empty();
    }

    public static void init() {
        Optional<Connection> connection = getConnection();
        if(connection.isPresent()) {
            try (Connection c = connection.get()){
                try(PreparedStatement st = c.prepareStatement("CREATE TABLE IF NOT EXISTS courses (COURSEID CHAR(8) PRIMARY KEY, MAXSTUDENTS INT)")) {
                    st.execute();
                }
                try(PreparedStatement st = c.prepareStatement("CREATE TABLE IF NOT EXISTS students (STUDENTID CHAR(8) PRIMARY KEY, NAME TEXT, POINTS INT)")) {
                    st.execute();
                }
                try(PreparedStatement st = c.prepareStatement("""
                    CREATE TABLE [coursesNpeople] (
                    [COURSEID] CHAR(8) NOT NULL,
                    [PEOPLEID] CHAR(8) NOT NULL,
                    [PERMISSIONLEVEL] CHAR(64) NOT NULL,
                    [POINTS] INT NOT NULL,
                    CONSTRAINT [PK_coursesNpeople_0] PRIMARY KEY ([COURSEID], [PEOPLEID]),
                    CONSTRAINT [FK_coursesNpeople_0] FOREIGN KEY ([COURSEID]) REFERENCES [courses] ([COURSEID]) ON DELETE NO ACTION ON UPDATE NO ACTION,
                    CONSTRAINT [FK_coursesNpeople_1] FOREIGN KEY ([PEOPLEID]) REFERENCES [students] ([STUDENTID]) ON DELETE NO ACTION ON UPDATE NO ACTION,
                    CONSTRAINT [UK_coursesNpeople_0] UNIQUE ([COURSEID], [PEOPLEID])
                    );""")) {
                    st.execute();
                }
            } catch (SQLException e) {
                Logger.getGlobal().info(e.getMessage());
            }
        }
    }

    public static List<Course> getCourses(){
        List<Course> courseList = new ArrayList<>();
        Optional<Connection> connection = getConnection();
        if(connection.isPresent()) {
            try (Connection c = connection.get()){
                try(PreparedStatement st = c.prepareStatement("SELECT COURSEID, MAXSTUDENTS FROM courses")) {
                    ResultSet rs = st.executeQuery();
                    while(rs.next()) {
                        courseList.add(new Course(rs.getString(1), rs.getInt(2)));
                    }
                }
            } catch (SQLException e) {
                Logger.getGlobal().info(e.getMessage());
            }
        }
        return courseList;
    }

    public static boolean setCourse(Course course){
        Optional<Connection> connection = getConnection();
        if(connection.isPresent()) {
            try (Connection c = connection.get()){
                try(PreparedStatement st = c.prepareStatement("INSERT INTO courses (COURSEID, MAXSTUDENTS) VALUES (?,?)")) {
                    st.setString(1, course.getCourseID());
                    st.setInt(2, course.getMaxStudents());
                    st.executeUpdate();
                }
            } catch (SQLException e) {
                Logger.getGlobal().info(e.getMessage());
                return false;
            }
        }
        return true;
    }

    public static List<Student> getStudents(){
        List<Student> studentList = new ArrayList<>();
        Optional<Connection> connection = getConnection();
        if(connection.isPresent()) {
            try (Connection c = connection.get()){
                try(PreparedStatement st = c.prepareStatement("SELECT * FROM students")) {
                    ResultSet rs = st.executeQuery();
                    while(rs.next()) {
                        studentList.add(new Student(rs.getString(1), rs.getString(2), rs.getInt(3)));
                    }
                }
            } catch (SQLException e) {
                Logger.getGlobal().info(e.getMessage());
            }
        }
        return studentList;
    }

    public static boolean addStudent(Student student) {
        Optional<Connection> connection = getConnection();
        if(connection.isPresent()) {
            try (Connection c = connection.get()){
                try(PreparedStatement st = c.prepareStatement("INSERT INTO students (STUDENTID, NAME, POINTS) VALUES (?,?,?)")) {
                    st.setString(1, student.getId());
                    st.setString(2, student.getName());
                    st.setInt(3, student.getPoints());
                    st.execute();
                    return true;
                }
            } catch (SQLException e) {
                Logger.getGlobal().info(e.getMessage());
            }
        }
        return false;
    }

    public static boolean addStudentToCourse(String courseid, String studentid, String permissionLevel, int points) {
        Optional<Connection> connection = getConnection();
        if(connection.isPresent()) {
            try (Connection c = connection.get()){
                try(PreparedStatement st = c.prepareStatement("INSERT INTO coursesNpeople (COURSEID, PEOPLEID, PERMISSIONLEVEL, POINTS) VALUES (?,?,?,?)")) {
                    st.setString(1, courseid);
                    st.setString(2, studentid);
                    st.setString(3, permissionLevel);
                    st.setInt(4, points);
                    st.execute();
                    return true;
                }
            } catch (SQLException e) {
                Logger.getGlobal().info(e.getMessage());
            }
        }
        return false;
    }

    public static Optional<Course> getCourseWithStudents(String courseID) {
        Optional<Connection> connection = getConnection();
        if(connection.isPresent()) {
            try (Connection c = connection.get()){
                try(PreparedStatement st = c.prepareStatement("""
                    SELECT *
                    FROM coursesNpeople
                    INNER JOIN courses ON coursesNpeople.COURSEID=courses.COURSEID
                    INNER JOIN students ON coursesNpeople.PEOPLEID=students.STUDENTID
                    WHERE courses.COURSEID=?;
                    """)) {
                    st.setString(1, courseID);
                    ResultSet rs = st.executeQuery();
                    Course course = null;
                    while(rs.next()) {
                        if(course == null) course = new Course(rs.getString("COURSEID"), rs.getInt("MAXSTUDENTS"));
                        course.addStudent(new Student(rs.getString("STUDENTID"), rs.getString("NAME"), rs.getInt("POINTS")));
                    }
                    assert course != null;
                    return Optional.of(course);
                }
            } catch (SQLException e) {
                Logger.getGlobal().info(e.getMessage());
            }
        }
        return Optional.empty();
    }
}