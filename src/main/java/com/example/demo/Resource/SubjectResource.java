package com.example.demo.Resource;

import com.example.demo.DTO.SubjectDTO;
import com.example.demo.Service.SubjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subject")
@CrossOrigin(origins = "http://localhost:4200")
public class SubjectResource {

    private final SubjectService subjectService;

    public SubjectResource(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @PostMapping
    public ResponseEntity<String> createSubject(@RequestBody SubjectDTO dto) {
        return ResponseEntity.status(201).body(subjectService.createSubject(dto));
    }

    @GetMapping("/by-department/{deptId}")
    public ResponseEntity<List<SubjectDTO>> getByDepartmentAndSemester(@PathVariable Long deptId,
                                                                       @RequestParam int semester) {
        return ResponseEntity.ok(subjectService.getSubjectsByDepartmentAndSemester(deptId, semester));
    }

    @GetMapping("/for-student/{studentId}")
    public ResponseEntity<List<SubjectDTO>> getForStudent(@PathVariable Long studentId,
                                                          @RequestParam int semester) {
        return ResponseEntity.ok(subjectService.getSubjectsForStudent(studentId, semester));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateSubject(@PathVariable Long id, @RequestBody SubjectDTO dto) {
        return ResponseEntity.ok(subjectService.updateSubject(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSubject(@PathVariable Long id) {
        return ResponseEntity.ok(subjectService.deleteSubject(id));
    }

    @PostMapping("/by-ids")
    public List<SubjectDTO> getSubjectsByIds(@RequestBody List<Long> ids) {
        return subjectService.getSubjectsByIds(ids);
    }
}
