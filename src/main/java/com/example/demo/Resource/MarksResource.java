package com.example.demo.Resource;

import com.example.demo.DTO.*;
import com.example.demo.ExceptionHandler.DuplicateResourceException;
import com.example.demo.ExceptionHandler.InvalidMarksException;
import com.example.demo.ExceptionHandler.ResourceNotFoundException;
import com.example.demo.Service.MarksService;
import org.springframework.data.domain.Page;
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
    public MarksResource(MarksService marksService)
    {
        this.marksService=marksService;
    }

    @PostMapping("/bulk")
    public ResponseEntity<String> createAllMarks(@RequestBody MarksEntryRequestDTO request) {
        try {
            marksService.createAllMarks(request);
            return ResponseEntity.status(201).body("All marks saved successfully");
        } catch (DuplicateResourceException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (InvalidMarksException | ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/marksheet/{studentId}")
    public ResponseEntity<MarksResponseDTO> getMarksheet(
            @PathVariable Long studentId,
            @RequestParam int semester,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);
        MarksResponseDTO result = marksService.getMarksheet(studentId, semester, pageable);
        return ResponseEntity.ok(result);
    }
    @PutMapping("/update")
    public ResponseEntity<String> updateMarks(
            @RequestParam Long studentId,
            @RequestParam int semester,
            @RequestParam String subjectName,
            @RequestParam int newMarks) {
        marksService.updateMarks(studentId, semester, subjectName, newMarks);
        return ResponseEntity.ok("Marks updated successfully");
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<String> deleteAllMarks(
            @RequestParam Long studentId,
            @RequestParam int semester) {
        marksService.deleteAllMarks(studentId, semester);
        return ResponseEntity.ok("All marks deleted for student " + studentId + " in semester " + semester);
    }
    @GetMapping("/summary")
    public ResponseEntity<Page<StudentMarksSummaryDTO>> getPaginatedStudentSummary(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<StudentMarksSummaryDTO> result = marksService.getPaginatedStudentSummary(pageable);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/distribution")
    public ResponseEntity<Map<String, PercentageGroupDTO>> getPercentageDistribution() {
        return ResponseEntity.ok(marksService.getPercentageDistribution());
    }


}
