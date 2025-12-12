package com.example.demo.Resource;

import com.example.demo.DTO.TeacherDTO;
import com.example.demo.Service.TeacherService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teacher")
@CrossOrigin(origins = "http://localhost:4200")
public class TeacherResource {

    private final TeacherService teacherService;
    public TeacherResource(TeacherService teacherService) { this.teacherService = teacherService; }

    @PostMapping
    public ResponseEntity<String> createTeacher(@Valid @RequestBody TeacherDTO teacher) {
        String result = teacherService.createTeacher(teacher);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping
    public ResponseEntity<Page<TeacherDTO>> getAllTeachers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TeacherDTO> teachers = teacherService.getAllTeachers(PageRequest.of(page, size));
        return ResponseEntity.ok(teachers);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<TeacherDTO>> searchTeachers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TeacherDTO> results = teacherService.searchTeachers(query, PageRequest.of(page, size));
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeacherDTO> getTeacherById(@PathVariable Long id) {
        TeacherDTO teacher = teacherService.getTeacherById(id);
        return ResponseEntity.ok(teacher);
    }

    @GetMapping("/by-department/{deptId}")
    public ResponseEntity<List<TeacherDTO>> getTeachersByDepartment(@PathVariable Long deptId) {
        List<TeacherDTO> teachers = teacherService.getTeachersByDepartment(deptId);
        return ResponseEntity.ok(teachers);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateTeacher(@PathVariable Long id, @Valid @RequestBody TeacherDTO teacher) {
        String result = teacherService.updateTeacher(id, teacher);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTeacher(@PathVariable Long id) {
        String result = teacherService.deleteTeacher(id);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/by-email")
    public ResponseEntity<TeacherDTO> getByEmail(@RequestParam String email) {
        return teacherService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
