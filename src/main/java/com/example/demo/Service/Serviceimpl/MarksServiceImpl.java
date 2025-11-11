package com.example.demo.Service.Serviceimpl;

import com.example.demo.DTO.*;

import com.example.demo.Domain.MarksDomain;
import com.example.demo.Domain.StudentDomain;
import com.example.demo.ExceptionHandler.DuplicateResourceException;
import com.example.demo.ExceptionHandler.InvalidMarksException;
import com.example.demo.ExceptionHandler.ResourceNotFoundException;
import com.example.demo.Repository.MarksRepository;
import com.example.demo.Repository.StudentRepository;
import com.example.demo.Service.MarksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MarksServiceImpl implements MarksService {

    @Autowired
    private MarksRepository marksRepository;

    @Autowired
    private StudentRepository studentRepository;

    private static final List<String> VALID_SUBJECTS = List.of("DBMS", "DATA STRUCTURES", "DAA", "ADE", "MES");

    @Override
    public void createAllMarks(MarksEntryRequestDTO request) {
        Long studentId = request.getStudentId();
        int semester = request.getSemester();

        StudentDomain student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        for (MarksDTO dto : request.getSubjects()) {
            String subject = dto.getSubjectName().toUpperCase();
            int marks = dto.getMarksObtained();

            if (!VALID_SUBJECTS.contains(subject)) {
                throw new InvalidMarksException("Invalid subject: " + subject);
            }
            if (marks < 0 || marks > 100) {
                throw new InvalidMarksException("Marks must be between 0 and 100 for " + subject);
            }

            Optional<MarksDomain> existing = marksRepository.findByStudentIdAndSubjectNameAndSemester(studentId, subject, semester);
            if (existing.isPresent()) {
                throw new DuplicateResourceException("Marks have already been uploaded for one or more subjects in semester " + semester);

            }

            MarksDomain marksDomain = new MarksDomain()
                    .setStudent(student)
                    .setSubjectName(subject)
                    .setMarksObtained(marks)
                    .setSemester(semester);

            marksRepository.save(marksDomain);
        }
    }


    @Override
    public MarksResponseDTO getMarksheet(Long studentId, int semester, Pageable pageable) {
        Page<MarksDomain> pageData = marksRepository.findByStudentIdAndSemester(studentId, semester, pageable);

        if (pageData.isEmpty()) {
            throw new ResourceNotFoundException("No marks found");
        }

        List<MarksDTO> subjects = pageData.getContent().stream()
                .map(m -> new MarksDTO(m.getSubjectName(), m.getMarksObtained()))
                .collect(Collectors.toList());

        int total = subjects.stream().mapToInt(MarksDTO::getMarksObtained).sum();
        double percentage = total / (subjects.size() * 1.0);

        return new MarksResponseDTO()
                .setSubjects(subjects)
                .setTotal(total)
                .setPercentage(percentage)
                .setPage(pageData.getNumber())
                .setSize(pageData.getSize())
                .setTotalPages(pageData.getTotalPages())
                .setTotalElements(pageData.getTotalElements());
    }
    @Override
    public void updateMarks(Long studentId, int semester, String subjectName, int newMarks) {
        MarksDomain existing = marksRepository
                .findByStudentIdAndSubjectNameAndSemester(studentId, subjectName.toUpperCase(), semester)
                .orElseThrow(() -> new ResourceNotFoundException("Marks not found"));

        if (newMarks < 0 || newMarks > 100) {
            throw new InvalidMarksException("Marks must be between 0 and 100");
        }

        existing.setMarksObtained(newMarks);
        marksRepository.save(existing);
    }
    @Override
    public void deleteAllMarks(Long studentId, int semester) {
        List<MarksDomain> marksList = marksRepository.findByStudentIdAndSemester(studentId, semester);
        if (marksList.isEmpty()) {
            throw new ResourceNotFoundException("No marks found for student " + studentId + " in semester " + semester);
        }
        marksRepository.deleteAll(marksList);
    }
    @Override
    public Page<StudentMarksSummaryDTO> getPaginatedStudentSummary(Pageable pageable) {
        Page<StudentMarksProjection> rawPage = marksRepository.getStudentTotals(pageable);
        return rawPage.map(p -> new StudentMarksSummaryDTO(p.getStudentId(), p.getName(), p.getTotal(), p.getCount()));
    }
    @Override
    public Map<String, PercentageGroupDTO> getPercentageDistribution() {
        List<StudentMarksProjection> raw = marksRepository.getStudentTotals();

        Map<String, List<StudentInfoDTO>> grouped = raw.stream()
                .collect(Collectors.groupingBy(p -> {
                    double percentage = p.getCount() > 0 ? p.getTotal() / (p.getCount() * 1.0) : 0.0;
                    if (percentage <= 35) return "0-35%";
                    else if (percentage <= 50) return "36-50%";
                    else if (percentage <= 75) return "51-75%";
                    else return "76-100%";
                }, Collectors.mapping(p -> new StudentInfoDTO(p.getStudentId(), p.getName()), Collectors.toList())));

        return grouped.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> new PercentageGroupDTO(e.getValue().size(), e.getValue())
                ));
    }

}