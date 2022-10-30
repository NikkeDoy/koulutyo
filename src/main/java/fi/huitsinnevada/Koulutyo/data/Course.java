package fi.huitsinnevada.Koulutyo.data;

import java.util.ArrayList;
import java.util.List;

public class Course {
    String courseID = "dummy";
    int maxStudents = 250;
    List<Student> studentList = new ArrayList<>();
    // I could add beginning and ending date, but I won't...
    public Course(String courseID, int maxStudents) {
        this.courseID = courseID;
        this.maxStudents = maxStudents;
    }

    public String getCourseID() {
        return courseID;
    }

    public int getMaxStudents() {
        return maxStudents;
    }

    public List<Student> getStudentList() {
        return studentList;
    }

    public boolean addStudent(Student student) {
        if(studentList.stream().noneMatch(stdnt -> stdnt.id.equals(student.id))) {
            studentList.add(student);
            return true;
        } else return false;
    }
}
