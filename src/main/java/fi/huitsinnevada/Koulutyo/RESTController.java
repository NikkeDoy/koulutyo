package fi.huitsinnevada.Koulutyo;

import fi.huitsinnevada.Koulutyo.data.Course;
import fi.huitsinnevada.Koulutyo.data.Student;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class RESTController {

    @GetMapping("courses")
    public ResponseEntity<List<Course>> getCourses(){
        return ResponseEntity.ok(FileService.getCourses());
    }

    @PostMapping("addcourse")
    public ResponseEntity<String> addCourse(@RequestParam String id, @RequestParam int maxstudents){
        Course course = new Course(id, maxstudents);
        if(FileService.setCourse(course)) return ResponseEntity.ok().build();
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("students")
    public ResponseEntity<List<Student>> getStudents() {
        return ResponseEntity.ok(FileService.getStudents());
    }

    @PostMapping("addstudent")
    public ResponseEntity<String> addStudent(@RequestParam String id, @RequestParam String name) {
        if(FileService.addStudent(new Student(id, name, 0))) return ResponseEntity.ok().build();
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("coursewithstudents")
    public ResponseEntity<Course> getStudents(@RequestParam String courseid) {
        Optional<Course> course = FileService.getCourseWithStudents(courseid);
        return course.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("addstudenttocourse")
    public ResponseEntity<Course> getStudents(@RequestParam String courseid, @RequestParam String studentid) {
        if(FileService.addStudentToCourse(courseid, studentid, "STUDENT", 0)) return ResponseEntity.ok().build();
        return ResponseEntity.notFound().build();
    }
}
