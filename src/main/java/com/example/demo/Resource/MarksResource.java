// src/main/java/com/example/demo/Resource/MarksResource.java
package com.example.demo.Resource;

import com.example.demo.DTO.MarksEntryRequestDTO;
import com.example.demo.DTO.MarksResponseDTO;
import com.example.demo.DTO.PercentageGroupDTO;
import com.example.demo.ExceptionHandler.DuplicateResourceException;
import com.example.demo.ExceptionHandler.InvalidMarksException;
import com.example.demo.ExceptionHandler.ResourceNotFoundException;
import com.example.demo.Service.MarksService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/marks")
@CrossOrigin(origins = "http://localhost:4200")
public class MarksResource {

    private final MarksService marksService;

    public MarksResource(MarksService marksService) {
        this.marksService = marksService;
    }

    @PostMapping("/bulk")
    public ResponseEntity<String> createAllMarks(@RequestBody MarksEntryRequestDTO request,
                                                 @RequestParam String email) {
        try {
            marksService.createAllMarks(request, email);
            return ResponseEntity.ok("Marks saved successfully");
        } catch (InvalidMarksException | ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



    @GetMapping("/marksheet/{studentId}")
    public ResponseEntity<MarksResponseDTO> getMarksheet(@PathVariable Long studentId,
                                                         @RequestParam int semester,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "5") int size,
                                                         @RequestParam String email) {
        Pageable pageable = PageRequest.of(page, size);
        MarksResponseDTO result = marksService.getMarksheet(studentId, semester, pageable, email);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateMarks(@RequestParam Long studentId,
                                              @RequestParam int semester,
                                              @RequestParam Long subjectId,
                                              @RequestParam int newMarks,
                                              @RequestParam String email) {
        marksService.updateMarks(studentId, semester, subjectId, newMarks, email);
        return ResponseEntity.ok("Marks updated successfully");
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<String> deleteAllMarks(@RequestParam Long studentId,
                                                 @RequestParam int semester,
                                                 @RequestParam String email) {
        marksService.deleteAllMarks(studentId, semester, email);
        return ResponseEntity.ok("All marks deleted for student " + studentId + " in semester " + semester);
    }

    @GetMapping("/summary")
    public ResponseEntity<?> getPaginatedStudentSummary(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "5") int size,
                                                        @RequestParam String email) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(marksService.getPaginatedStudentSummary(pageable, email));
    }


    @GetMapping("/distribution")
    public ResponseEntity<Map<String, PercentageGroupDTO>> getPercentageDistribution() {
        return ResponseEntity.ok(marksService.getPercentageDistribution());
    }


}
