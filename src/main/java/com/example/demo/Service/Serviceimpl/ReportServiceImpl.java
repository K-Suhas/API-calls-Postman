// src/main/java/com/example/demo/Service/Serviceimpl/ReportServiceImpl.java
package com.example.demo.Service.Serviceimpl;

import com.example.demo.DTO.MarksDTO;
import com.example.demo.DTO.ReportJobStatusDTO;
import com.example.demo.DTO.StudentMarksheetDTO;
import com.example.demo.Domain.CourseDomain;
import com.example.demo.Domain.MarksDomain;
import com.example.demo.Domain.StudentDomain;
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

    // ===== Bulk CSV job API =====

    @Override
    public String startCsvReportJob(Integer semester) {
        String jobId = UUID.randomUUID().toString();
        jobs.put(jobId, new ReportJobStatusDTO(jobId, 0, "PENDING", "Queued"));

        new Thread(() -> {
            try {
                update(jobId, 25, "RUNNING", "Fetching students");
                Thread.sleep(1000);

                List<StudentDomain> students;
                try {
                    // If you have a custom fetch that preloads courses
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

                    String courses = (s.getCourses() == null) ? "" :
                            s.getCourses().stream().map(CourseDomain::getName).sorted().collect(Collectors.joining(";"));

                    sb.append(s.getId()).append(",")
                            .append(escape(s.getName())).append(",")
                            .append(escape(s.getDept())).append(",")
                            .append(escape(s.getEmail())).append(",")
                            .append(s.getDob() != null ? s.getDob() : "").append(",")
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

            String courses = (s.getCourses() == null) ? "" :
                    s.getCourses().stream().map(CourseDomain::getName).sorted().collect(Collectors.joining(";"));

            sb.append(s.getId()).append(",")
                    .append(escape(s.getName())).append(",")
                    .append(escape(s.getDept())).append(",")
                    .append(escape(s.getEmail())).append(",")
                    .append(s.getDob() != null ? s.getDob() : "").append(",")
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

    // ===== Individual student report API =====

    @Override
    public StudentMarksheetDTO getIndividualReport(Long studentId, int semester) {
        StudentDomain student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        List<MarksDomain> marks = marksRepository.findByStudentIdAndSemester(studentId, semester);
        if (marks.isEmpty()) {
            throw new ResourceNotFoundException("No marks found for student " + studentId + " in semester " + semester);
        }

        int total = marks.stream().mapToInt(MarksDomain::getMarksObtained).sum();
        double percentage = total / (marks.size() * 1.0);

        List<MarksDTO> subjects = marks.stream()
                .map(m -> new MarksDTO(m.getSubjectName(), m.getMarksObtained()))
                .toList();

        List<String> courseNames = (student.getCourses() == null)
                ? Collections.emptyList()
                : student.getCourses().stream().map(CourseDomain::getName).sorted().toList();

        return new StudentMarksheetDTO()
                .setId(student.getId())
                .setName(student.getName())
                .setDept(student.getDept())
                .setEmail(student.getEmail())
                .setDob(student.getDob())
                .setCourseNames(courseNames)
                .setTotalMarks(total)
                .setPercentage(percentage)
                .setSubjects(subjects);
    }

    @Override
    public org.springframework.core.io.Resource downloadIndividualReport(Long studentId, int semester) {
        StudentMarksheetDTO dto = getIndividualReport(studentId, semester);

        // ✅ join course names for CSV
        String coursesJoined = (dto.getCourseNames() == null || dto.getCourseNames().isEmpty())
                ? ""
                : String.join(";", dto.getCourseNames());

        StringBuilder sb = new StringBuilder();
        sb.append("ID,Name,Department,Email,DOB,Courses,Total Marks,Percentage\n");
        sb.append(dto.getId()).append(",")
                .append(escape(dto.getName())).append(",")
                .append(escape(dto.getDept())).append(",")
                .append(escape(dto.getEmail())).append(",")
                .append(dto.getDob() != null ? dto.getDob() : "").append(",")
                .append(escape(coursesJoined)).append(",")
                .append(dto.getTotalMarks()).append(",")
                .append(String.format("%.2f", dto.getPercentage()))
                .append("\n\n");

        sb.append("Subject,Marks Obtained\n");
        dto.getSubjects().forEach(m ->
                sb.append(escape(m.getSubjectName())).append(",").append(m.getMarksObtained()).append("\n")
        );

        return new org.springframework.core.io.ByteArrayResource(sb.toString().getBytes(StandardCharsets.UTF_8));
    }

}
