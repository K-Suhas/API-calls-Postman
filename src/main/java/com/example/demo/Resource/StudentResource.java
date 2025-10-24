package com.example.demo.Resource;

import com.example.demo.DTO.StudentDTO;
import com.example.demo.Service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    public ResponseEntity<Page<StudentDTO>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<StudentDTO> students = studentService.getallstudent(PageRequest.of(page, size));
        if (students.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(students);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<StudentDTO>> searchStudents(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<StudentDTO> results = studentService.searchStudents(query, PageRequest.of(page, size));
        if (results.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Long id) {
        StudentDTO student = studentService.getstudentbyid(id);
        return student != null ? ResponseEntity.ok(student) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
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
