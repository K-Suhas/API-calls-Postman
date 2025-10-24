package com.example.demo.Resource;

import com.example.demo.DTO.StudentDTO;
import com.example.demo.Service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student")

@CrossOrigin(origins = "http://localhost:4200")
public class StudentResource {

    @Autowired
    private StudentService studentService;

    @PostMapping
    public ResponseEntity<String> createstudent(@RequestBody StudentDTO student) {
        String result = studentService.createstudent(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping
    public ResponseEntity<List<StudentDTO>> getallstudent() {
        List<StudentDTO> students = studentService.getallstudent();
        if (students.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(students);
    }
    @GetMapping("/search")
    public ResponseEntity<List<StudentDTO>> searchStudents(@RequestParam String query) {
        List<StudentDTO> results = studentService.searchStudents(query);
        if (results.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(results);
    }


    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getstudentbyid(@PathVariable Long id) {
        StudentDTO student = studentService.getstudentbyid(id);
        if (student != null) {
            return ResponseEntity.ok(student);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updatestudent(@PathVariable Long id, @RequestBody StudentDTO student) {
        String result = studentService.updatestudent(id, student);
        if (result.startsWith("Student updated")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletestudent(@PathVariable Long id) {
        String result = studentService.deletestudent(id);
        if (result.startsWith("Student deleted")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }
}
