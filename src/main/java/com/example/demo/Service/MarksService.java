package com.example.demo.Service;

import com.example.demo.DTO.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface MarksService {

    void createAllMarks(MarksEntryRequestDTO request);
    MarksResponseDTO getMarksheet(Long studentId, int semester, Pageable pageable);
    void updateMarks(Long studentId, int semester, String subjectName, int newMarks);
    Page<StudentMarksSummaryDTO> getPaginatedStudentSummary(Pageable pageable);
    void deleteAllMarks(Long studentId, int semester);
    Map<String, PercentageGroupDTO> getPercentageDistribution();

}
