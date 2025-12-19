package com.example.demo.Resource;

import com.example.demo.DTO.SubjectChoiceRequestDTO;
import com.example.demo.DTO.SubjectChoiceResponseDTO;
import com.example.demo.Service.SubjectChoiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subject-choice")
@CrossOrigin(origins = "http://localhost:4200")
public class SubjectChoiceResource {

    private final SubjectChoiceService service;

    public SubjectChoiceResource(SubjectChoiceService service) {
        this.service = service;
    }

    /**
     * Student chooses subjects for a semester.
     * Must be exactly 4 subjects, department fixed.
     * Once saved, cannot change again.
     */
    @PostMapping
    public ResponseEntity<SubjectChoiceResponseDTO> createChoice(@RequestBody SubjectChoiceRequestDTO req) {
        try {
            SubjectChoiceResponseDTO resp = service.createChoice(req);
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * Get chosen subjects for a student in a semester.
     * Used by Teacher/Admin when loading marks.
     */
    @GetMapping
    public ResponseEntity<SubjectChoiceResponseDTO> getChoice(@RequestParam Long studentId,
                                                              @RequestParam Integer semester) {
        return service.getChoice(studentId, semester)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
