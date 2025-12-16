// src/main/java/com/example/demo/Service/MarksService.java
package com.example.demo.Service;

import com.example.demo.DTO.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface MarksService {
    void createAllMarks(MarksEntryRequestDTO request, String requesterEmail);
    MarksResponseDTO getMarksheet(Long studentId, int semester, Pageable pageable, String requesterEmail);
    void updateMarks(Long studentId, int semester, Long subjectId, int newMarks, String requesterEmail);
    void deleteAllMarks(Long studentId, int semester, String requesterEmail);
    Page<StudentMarksSummaryDTO> getPaginatedStudentSummary(Pageable pageable,String requesterEmail);
    Map<String, PercentageGroupDTO> getPercentageDistribution();
}
