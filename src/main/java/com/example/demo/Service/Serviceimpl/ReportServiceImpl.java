// src/main/java/com/example/demo/Service/Serviceimpl/ReportServiceImpl.java
package com.example.demo.Service.Serviceimpl;

import com.example.demo.Domain.MarksDomain;
import com.example.demo.Domain.StudentDomain;
import com.example.demo.DTO.ReportJobStatusDTO;
import com.example.demo.ExceptionHandler.ResourceNotFoundException;
import com.example.demo.Repository.MarksRepository;
import com.example.demo.Repository.StudentRepository;
import com.example.demo.Service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired private StudentRepository studentRepository;
    @Autowired private MarksRepository marksRepository;

    // In-memory job storage
    private final Map<String, ReportJobStatusDTO> jobs = new ConcurrentHashMap<>();
    private final Map<String, byte[]> jobFiles = new ConcurrentHashMap<>();

    @Override
    public String startCsvReportJob(Integer semester) {
        String jobId = UUID.randomUUID().toString();
        jobs.put(jobId, new ReportJobStatusDTO(jobId, 0, "PENDING", "Queued"));

        // Simulate progress in background thread
        new Thread(() -> {
            try {
                update(jobId, 25, "RUNNING", "Fetching students");
                Thread.sleep(1000);

                List<StudentDomain> students;
                try {
                    students = studentRepository.findAllWithCourses();
                } catch (Exception ignored) {
                    students = studentRepository.findAll();
                }
                if (students.isEmpty()) {
                    throw new ResourceNotFoundException("No students available to generate report");
                }

                update(jobId, 50, "RUNNING", "Aggregating marks");
                Thread.sleep(1000);

                List<MarksDomain> allMarks = marksRepository.findAll();
                if (semester != null) {
                    allMarks = allMarks.stream()
                            .filter(m -> m.getSemester() == semester)
                            .collect(Collectors.toList());
                }
                Map<Long, List<MarksDomain>> marksByStudent = allMarks.stream()
                        .collect(Collectors.groupingBy(m -> m.getStudent().getId()));

                update(jobId, 75, "RUNNING", "Computing totals");
                Thread.sleep(1000);

                // Build CSV
                StringBuilder sb = new StringBuilder();
                sb.append("ID,Name,Department,Email,DOB,Courses,Subjects,Total Marks Obtained,Percentage\n");
                for (StudentDomain s : students) {
                    List<MarksDomain> marks = marksByStudent.getOrDefault(s.getId(), Collections.emptyList());
                    int subjects = marks.size();
                    int total = marks.stream().mapToInt(MarksDomain::getMarksObtained).sum();
                    double percentage = subjects > 0 ? (total / (subjects * 1.0)) : 0.0;

                    String courses = s.getCourses() == null ? "" :
                            s.getCourses().stream().map(c -> c.getName()).sorted().collect(Collectors.joining(";"));

                    sb.append(s.getId()).append(",")
                            .append(escape(s.getName())).append(",")
                            .append(escape(s.getDept())).append(",")
                            .append(escape(s.getEmail())).append(",")
                            .append(s.getDob() != null ? s.getDob().toString() : "").append(",")
                            .append(escape(courses)).append(",")
                            .append(subjects).append(",")
                            .append(total).append(",")
                            .append(String.format("%.2f", percentage))
                            .append("\n");
                }

                byte[] data = sb.toString().getBytes(StandardCharsets.UTF_8);
                jobFiles.put(jobId, data);

                update(jobId, 100, "READY", "Report ready");
            } catch (Exception e) {
                update(jobId, 100, "FAILED", "Error: " + e.getMessage());
            }
        }).start();

        return jobId;
    }

    @Override
    public ReportJobStatusDTO getJobStatus(String jobId) {
        return jobs.getOrDefault(jobId, new ReportJobStatusDTO(jobId, 0, "NOT_FOUND", "Invalid jobId"));
    }

    @Override
    public Resource downloadReport(String jobId) {
        byte[] data = jobFiles.get(jobId);
        if (data == null) {
            throw new ResourceNotFoundException("Report not found or not ready for jobId: " + jobId);
        }
        return new ByteArrayResource(data);
    }

    @Override
    public Resource generateCsvReport(Integer semester) {
        // Direct CSV generation (used internally by async job)
        List<StudentDomain> students;
        try {
            students = studentRepository.findAllWithCourses();
        } catch (Exception ignored) {
            students = studentRepository.findAll();
        }
        if (students.isEmpty()) {
            throw new ResourceNotFoundException("No students available to generate report");
        }

        List<MarksDomain> allMarks = marksRepository.findAll();
        if (semester != null) {
            allMarks = allMarks.stream()
                    .filter(m -> m.getSemester() == semester)
                    .collect(Collectors.toList());
        }
        Map<Long, List<MarksDomain>> marksByStudent = allMarks.stream()
                .collect(Collectors.groupingBy(m -> m.getStudent().getId()));

        StringBuilder sb = new StringBuilder();
        sb.append("ID,Name,Department,Email,DOB,Courses,Subjects,Total Marks Obtained,Percentage\n");
        for (StudentDomain s : students) {
            List<MarksDomain> marks = marksByStudent.getOrDefault(s.getId(), Collections.emptyList());
            int subjects = marks.size();
            int total = marks.stream().mapToInt(MarksDomain::getMarksObtained).sum();
            double percentage = subjects > 0 ? (total / (subjects * 1.0)) : 0.0;

            String courses = s.getCourses() == null ? "" :
                    s.getCourses().stream().map(c -> c.getName()).sorted().collect(Collectors.joining(";"));

            sb.append(s.getId()).append(",")
                    .append(escape(s.getName())).append(",")
                    .append(escape(s.getDept())).append(",")
                    .append(escape(s.getEmail())).append(",")
                    .append(s.getDob() != null ? s.getDob().toString() : "").append(",")
                    .append(escape(courses)).append(",")
                    .append(subjects).append(",")
                    .append(total).append(",")
                    .append(String.format("%.2f", percentage))
                    .append("\n");
        }

        byte[] data = sb.toString().getBytes(StandardCharsets.UTF_8);
        return new ByteArrayResource(data);
    }

    private void update(String jobId, int progress, String state, String message) {
        jobs.put(jobId, new ReportJobStatusDTO(jobId, progress, state, message));
    }

    private String escape(String val) {
        if (val == null) return "";
        if (val.contains(",") || val.contains("\"")) {
            return "\"" + val.replace("\"", "\"\"") + "\"";
        }
        return val;
    }
}
