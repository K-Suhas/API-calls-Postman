package com.example.demo.Resource;

import com.example.demo.DTO.BulkStudentDTO;
import com.example.demo.DTO.StudentDTO;
import com.example.demo.Service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/student")

@CrossOrigin(origins = "http://localhost:4200")
public class StudentResource {

    @Autowired
    private StudentService studentService;

    @PostMapping
    public ResponseEntity<String> createstudent(@RequestBody StudentDTO student) {
        String result = studentService.createstudent(student); // let exception propagate
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
    @GetMapping
    public ResponseEntity<Page<StudentDTO>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<StudentDTO> students = studentService.getallstudent(PageRequest.of(page, size)); // throws if empty
        return ResponseEntity.ok(students);
    }


    @GetMapping("/search")
    public ResponseEntity<Page<StudentDTO>> searchStudents(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<StudentDTO> results = studentService.searchStudents(query, PageRequest.of(page, size)); // throws if empty
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Long id) {
        StudentDTO student = studentService.getstudentbyid(id); // throws if not found
        return ResponseEntity.ok(student);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateStudent(@PathVariable Long id, @RequestBody StudentDTO student) {
        String result = studentService.updatestudent(id, student); // let exception propagate
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletestudent(@PathVariable Long id) {
        String result = studentService.deletestudent(id); // throws if not found
        return ResponseEntity.ok(result);
    }
    @PostMapping("/bulk")
    public ResponseEntity<?> addstudentsInBulk(@RequestBody BulkStudentDTO bulkDto) {
        List<String> errors = studentService.addStudentsInBulk(bulkDto);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }
        return ResponseEntity.ok(Map.of("message", "Students uploaded successfully"));
    }



}
