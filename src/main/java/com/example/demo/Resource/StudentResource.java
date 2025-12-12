package com.example.demo.Resource;

import com.example.demo.DTO.BulkStudentDTO;
import com.example.demo.DTO.StudentDTO;
import com.example.demo.Enum.Role;
import com.example.demo.Service.StudentService;
import com.example.demo.Service.UserService;
import jakarta.validation.Valid;
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

    private final StudentService studentService;
    private final UserService userService;   // ✅ inject

    public StudentResource(StudentService studentService, UserService userService) {
        this.studentService = studentService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<String> createstudent(@Valid @RequestBody StudentDTO student) {
        String result = studentService.createstudent(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping
    public ResponseEntity<Page<StudentDTO>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<StudentDTO> students = studentService.getallstudent(PageRequest.of(page, size));
        return ResponseEntity.ok(students);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<StudentDTO>> searchStudents(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<StudentDTO> results = studentService.searchStudents(query, PageRequest.of(page, size));
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Long id) {
        StudentDTO student = studentService.getstudentbyid(id);
        return ResponseEntity.ok(student);
    }

    @GetMapping("/by-department/{deptId}")
    public ResponseEntity<Page<StudentDTO>> getStudentsByDepartment(
            @PathVariable Long deptId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<StudentDTO> students = studentService.getStudentsByDepartment(deptId, PageRequest.of(page, size));
        return ResponseEntity.ok(students);
    }


    // ✅ Teacher-specific: create student bound to teacher’s department
    @PostMapping("/department/{deptId}")
    public ResponseEntity<String> createStudentForDepartment(@PathVariable Long deptId,
                                                             @Valid @RequestBody StudentDTO student) {
        String result = studentService.createstudentForDepartment(student, deptId);
        return ResponseEntity.status(HttpStatus.CREATED).body(result); // ✅ add this
    }


    @PutMapping("/{id}")
    public ResponseEntity<String> updateStudent(@PathVariable Long id, @Valid @RequestBody StudentDTO student) {
        String result = studentService.updatestudent(id, student);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletestudent(@PathVariable Long id) {
        String result = studentService.deletestudent(id);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/bulk")
    public ResponseEntity<Object> addStudentsInBulk(@RequestBody BulkStudentDTO bulkDto) {
        // ✅ enforce role from JWT / SecurityContext instead of email param
        List<String> errors = studentService.addStudentsInBulk(bulkDto);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }
        return ResponseEntity.ok(Map.of("message", "Students uploaded successfully"));
    }




}
