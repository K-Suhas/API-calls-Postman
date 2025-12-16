package com.example.demo.Service.Serviceimpl;

import com.example.demo.DTO.MarksDTO;
import com.example.demo.DTO.ReportJobStatusDTO;
import com.example.demo.DTO.StudentMarksheetDTO;
import com.example.demo.Domain.CourseDomain;
import com.example.demo.Domain.MarksDomain;
import com.example.demo.Domain.StudentDomain;
import com.example.demo.ExceptionHandler.ReportGenerationException;
import com.example.demo.ExceptionHandler.ResourceNotFoundException;
import com.example.demo.Repository.MarksRepository;
import com.example.demo.Repository.StudentRepository;
import com.example.demo.Service.ReportService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    private final StudentRepository studentRepository;
    private final MarksRepository marksRepository;

    public ReportServiceImpl(StudentRepository studentRepository, MarksRepository marksRepository) {
        this.marksRepository = marksRepository;
        this.studentRepository = studentRepository;
    }

    private final Map<String, ReportJobStatusDTO> jobs = new ConcurrentHashMap<>();
    private final Map<String, byte[]> jobFiles = new ConcurrentHashMap<>();

    private static final String STATE_RUNNING = "RUNNING";

    @Override
    public String startCsvReportJob(Integer semester) {
        String jobId = UUID.randomUUID().toString();
        jobs.put(jobId, new ReportJobStatusDTO(jobId, 0, "PENDING", "Queued"));

        Thread.ofVirtual().start(() -> runCsvReportJob(jobId, semester));
        return jobId;
    }

    private void runCsvReportJob(String jobId, Integer semester) {
        try {
            update(jobId, 25, STATE_RUNNING, "Fetching students");
            sleepSafely();

            List<StudentDomain> students = fetchStudents();
            if (students.isEmpty()) {
                throw new ResourceNotFoundException("No students available to generate report");
            }

            update(jobId, 50, STATE_RUNNING, "Aggregating marks");
            sleepSafely();

            Map<Long, List<MarksDomain>> marksByStudent = fetchMarksByStudent(semester);

            update(jobId, 75, STATE_RUNNING, "Computing totals");
            sleepSafely();

            byte[] data = buildCsv(students, marksByStudent);
            jobFiles.put(jobId, data);

            update(jobId, 100, "READY", "Report ready");
        } catch (Exception e) {
            update(jobId, 100, "FAILED", "Error: " + e.getMessage());
        }
    }

    private List<StudentDomain> fetchStudents() {
        try {
            return studentRepository.findAllWithCourses();
        } catch (Exception _) {
            return studentRepository.findAll();
        }
    }

    private Map<Long, List<MarksDomain>> fetchMarksByStudent(Integer semester) {
        List<MarksDomain> allMarks = marksRepository.findAll();
        if (semester != null) {
            allMarks = allMarks.stream()
                    .filter(m -> m.getSemester() == semester)
                    .toList();
        }
        return allMarks.stream()
                .collect(Collectors.groupingBy(m -> m.getStudent().getId()));
    }

    private byte[] buildCsv(List<StudentDomain> students, Map<Long, List<MarksDomain>> marksByStudent) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Name,Department,Email,DOB,Courses,Subjects,Total Marks Obtained,Percentage\n");

        for (StudentDomain s : students) {
            List<MarksDomain> marks = marksByStudent.getOrDefault(s.getId(), Collections.emptyList());
            int subjects = marks.size();
            int total = marks.stream().mapToInt(MarksDomain::getMarksObtained).sum();
            double percentage = subjects > 0 ? (total / (subjects * 1.0)) : 0.0;

            String courses = (s.getCourses() == null) ? "" :
                    s.getCourses().stream().map(CourseDomain::getName).sorted().collect(Collectors.joining(";"));

            String departmentName = (s.getDepartment() != null) ? s.getDepartment().getName() : "";

            sb.append(s.getId()).append(",")
                    .append(escape(s.getName())).append(",")
                    .append(escape(departmentName)).append(",")
                    .append(escape(s.getEmail())).append(",")
                    .append(s.getDob() != null ? s.getDob() : "").append(",")
                    .append(escape(courses)).append(",")
                    .append(subjects).append(",")
                    .append(total).append(",")
                    .append(String.format("%.2f", percentage))
                    .append("\n");
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private void sleepSafely() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ReportGenerationException("Thread interrupted during report generation", e);
        }
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
        } catch (Exception _) {
            students = studentRepository.findAll();
        }
        if (students.isEmpty()) {
            throw new ResourceNotFoundException("No students available to generate report");
        }

        Map<Long, List<MarksDomain>> marksByStudent = fetchMarksByStudent(semester);
        byte[] data = buildCsv(students, marksByStudent);
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
                .map(m -> new MarksDTO(
                        m.getSubject().getId(),
                        m.getSubject().getName(),
                        m.getMarksObtained()))
                .toList();

        List<String> courseNames = (student.getCourses() == null)
                ? Collections.emptyList()
                : student.getCourses().stream().map(CourseDomain::getName).sorted().toList();

        String departmentName = (student.getDepartment() != null) ? student.getDepartment().getName() : "";

        return new StudentMarksheetDTO()
                .setId(student.getId())
                .setName(student.getName())
                .setDepartmentName(departmentName)
                .setEmail(student.getEmail())
                .setDob(student.getDob())
                .setCourseNames(courseNames)
                .setTotalMarks(total)
                .setPercentage(percentage)
                .setSubjects(subjects);
    }

    @Override
    public Resource downloadIndividualReport(Long studentId, int semester) {
        StudentMarksheetDTO dto = getIndividualReport(studentId, semester);

        String coursesJoined = (dto.getCourseNames() == null || dto.getCourseNames().isEmpty())
                ? ""
                : String.join(";", dto.getCourseNames());

        StringBuilder sb = new StringBuilder();
        sb.append("ID,Name,Department,Email,DOB,Courses,Total Marks,Percentage\n");
        sb.append(dto.getId()).append(",")
                .append(escape(dto.getName())).append(",")
                .append(escape(dto.getDepartmentName())).append(",")
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

        return new ByteArrayResource(sb.toString().getBytes(StandardCharsets.UTF_8));
    }
}
