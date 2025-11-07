package com.example.demo.Service;

import com.example.demo.DTO.MarksDTO;
import com.example.demo.DTO.MarksEntryRequestDTO;
import com.example.demo.DTO.MarksResponseDTO;
import com.example.demo.DTO.StudentMarksSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MarksService {

    void createAllMarks(MarksEntryRequestDTO request);
    MarksResponseDTO getMarksheet(Long studentId, int semester, Pageable pageable);
    void updateMarks(Long studentId, int semester, String subjectName, int newMarks);
    Page<StudentMarksSummaryDTO> getPaginatedStudentSummary(Pageable pageable);
    void deleteAllMarks(Long studentId, int semester);
}
