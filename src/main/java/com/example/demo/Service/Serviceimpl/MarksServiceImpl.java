// src/main/java/com/example/demo/Service/Serviceimpl/MarksServiceImpl.java
package com.example.demo.Service.Serviceimpl;

import com.example.demo.DTO.*;
import com.example.demo.Domain.MarksDomain;
import com.example.demo.Domain.StudentDomain;
import com.example.demo.Domain.SubjectDomain;
import com.example.demo.Enum.Role;
import com.example.demo.ExceptionHandler.InvalidMarksException;
import com.example.demo.ExceptionHandler.ResourceNotFoundException;
import com.example.demo.Mapper.MarksMapper;
import com.example.demo.Repository.MarksRepository;
import com.example.demo.Repository.StudentRepository;
import com.example.demo.Repository.SubjectRepository;
import com.example.demo.Service.MarksService;
import com.example.demo.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MarksServiceImpl implements MarksService {

    private static final Logger log = LoggerFactory.getLogger(MarksServiceImpl.class);

    private final MarksRepository marksRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final UserService userService;

    public MarksServiceImpl(MarksRepository marksRepository,
                            StudentRepository studentRepository,
                            SubjectRepository subjectRepository,
                            UserService userService) {
        this.marksRepository = marksRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.userService = userService;
    }

    private void enforceRoleForStudent(String requesterEmail, StudentDomain student) {
        Role role = userService.getUserRole(requesterEmail);
        if (role == Role.ADMIN) return;
        if (role == Role.TEACHER) {
            Long teacherDeptId = userService.getDepartmentIdForTeacher(requesterEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher department not found"));
            if (!Objects.equals(student.getDepartment().getId(), teacherDeptId)) {
                // Updated message: block viewing and editing across departments
                throw new InvalidMarksException("You are not allowed to view subjects of a different department");
            }
            return;
        }
        throw new InvalidMarksException("Only admin/teacher can manage marks");
    }

    @Override
    @Transactional
    public void createAllMarks(MarksEntryRequestDTO request, String requesterEmail) {
        Long studentId = request.getStudentId();
        int semester = request.getSemester();

        log.info("Bulk marks upload requested by={} for studentId={}, semester={}",
                requesterEmail, studentId, semester);

        StudentDomain student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        enforceRoleForStudent(requesterEmail, student);

        List<SubjectDomain> subjects = subjectRepository.findByDepartment_IdAndSemester(
                student.getDepartment().getId(), semester);

        if (subjects.isEmpty()) {
            throw new ResourceNotFoundException("No subjects configured for this department in semester " + semester);
        }

        Map<Long, SubjectDomain> byId = subjects.stream()
                .collect(Collectors.toMap(SubjectDomain::getId, s -> s));
        Map<String, SubjectDomain> byName = subjects.stream()
                .collect(Collectors.toMap(s -> s.getName().trim().toLowerCase(), s -> s));

        for (MarksDTO dto : request.getSubjects()) {
            SubjectDomain subject = resolveSubject(dto, byId, byName);

            int marks = dto.getMarksObtained();
            if (marks < 0 || marks > 100) {
                throw new InvalidMarksException("Marks must be between 0 and 100 for " + subject.getName());
            }

            log.info("Upsert check → studentId={}, subjectId={}, semester={}, marks={}",
                    studentId, subject.getId(), semester, marks);

            Optional<MarksDomain> existingOpt =
                    marksRepository.findByStudentIdAndSubject_IdAndSemester(studentId, subject.getId(), semester);

            if (existingOpt.isPresent()) {
                MarksDomain existing = existingOpt.get();
                log.info("Existing row found: id={} → updating marks from {} to {}",
                        existing.getId(), existing.getMarksObtained(), marks);
                existing.setMarksObtained(marks);
                marksRepository.save(existing);
            } else {
                log.info("No existing row found → inserting new marks row");
                MarksDomain md = new MarksDomain()
                        .setStudent(student)
                        .setSubject(subject)
                        .setMarksObtained(marks)
                        .setSemester(semester);
                marksRepository.save(md);
            }
        }

        log.info("Bulk marks upload completed for studentId={}, semester={}", studentId, semester);
    }

    @Override
    public void updateMarks(Long studentId, int semester, Long subjectId, int newMarks, String requesterEmail) {
        log.info("Update marks requested by={} for studentId={}, subjectId={}, semester={}, newMarks={}",
                requesterEmail, studentId, subjectId, semester, newMarks);

        StudentDomain student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        enforceRoleForStudent(requesterEmail, student);

        MarksDomain existing = marksRepository.findByStudentIdAndSubject_IdAndSemester(studentId, subjectId, semester)
                .orElseThrow(() -> new ResourceNotFoundException("Marks not found"));

        if (newMarks < 0 || newMarks > 100) {
            throw new InvalidMarksException("Marks must be between 0 and 100");
        }

        existing.setMarksObtained(newMarks);
        marksRepository.save(existing);

        log.info("Marks updated: rowId={}, newMarks={}", existing.getId(), newMarks);
    }

    @Override
    public MarksResponseDTO getMarksheet(Long studentId, int semester, Pageable pageable, String requesterEmail) {
        log.info("Marksheet requested by={} for studentId={}, semester={}, page={}, size={}",
                requesterEmail, studentId, semester, pageable.getPageNumber(), pageable.getPageSize());

        StudentDomain student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Role role = userService.getUserRole(requesterEmail);
        if (role == Role.STUDENT) {
            Long studentDeptId = userService.getDepartmentIdForStudent(requesterEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("Student department not found"));
            if (!Objects.equals(student.getDepartment().getId(), studentDeptId)) {
                throw new InvalidMarksException("Students can only view marks of their own department");
            }
        }
        else if (role == Role.TEACHER) {
            Long teacherDeptId = userService.getDepartmentIdForTeacher(requesterEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher department not found"));
            if (!Objects.equals(student.getDepartment().getId(), teacherDeptId)) {
                // Updated message to prevent subject viewing at search time
                throw new InvalidMarksException("You are not allowed to view subjects of a different department");
            }
        }

        Page<MarksDomain> pageData = marksRepository.findByStudentIdAndSemester(studentId, semester, pageable);
        if (pageData.isEmpty()) {
            throw new ResourceNotFoundException("No marks found");
        }

        List<MarksDTO> subjects = pageData.getContent().stream()
                .map(MarksMapper::toDTO)
                .toList();

        int total = subjects.stream().mapToInt(MarksDTO::getMarksObtained).sum();
        double percentage = subjects.isEmpty() ? 0.0 : total / (subjects.size() * 1.0);

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
    public void deleteAllMarks(Long studentId, int semester, String requesterEmail) {
        log.info("Delete all marks requested by={} for studentId={}, semester={}", requesterEmail, studentId, semester);

        StudentDomain student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        enforceRoleForStudent(requesterEmail, student);

        List<MarksDomain> marksList = marksRepository.findByStudentIdAndSemester(studentId, semester);
        if (marksList.isEmpty()) {
            throw new ResourceNotFoundException("No marks found for student " + studentId + " in semester " + semester);
        }
        marksRepository.deleteAll(marksList);

        log.info("Deleted {} marks rows for studentId={}, semester={}", marksList.size(), studentId, semester);
    }

    @Override
    public Page<StudentMarksSummaryDTO> getPaginatedStudentSummary(Pageable pageable, String requesterEmail) {
        Role role = userService.getUserRole(requesterEmail);
        if (role == Role.ADMIN) {
            return marksRepository.getStudentTotals(pageable)
                    .map(p -> new StudentMarksSummaryDTO(p.getStudentId(), p.getName(), p.getTotal(), p.getCount()));
        } else if (role == Role.TEACHER) {
            Long deptId = userService.getDepartmentIdForTeacher(requesterEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher department not found"));
            return marksRepository.getStudentTotalsByDepartment(deptId, pageable)
                    .map(p -> new StudentMarksSummaryDTO(p.getStudentId(), p.getName(), p.getTotal(), p.getCount()));
        } else if (role == Role.STUDENT) {
            Long deptId = userService.getDepartmentIdForStudent(requesterEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("Student department not found"));
            return marksRepository.getStudentTotalsByDepartment(deptId, pageable)
                    .map(p -> new StudentMarksSummaryDTO(p.getStudentId(), p.getName(), p.getTotal(), p.getCount()));
        } else {
            throw new InvalidMarksException("Unauthorized role");
        }
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

    private SubjectDomain resolveSubject(MarksDTO dto,
                                         Map<Long, SubjectDomain> byId,
                                         Map<String, SubjectDomain> byName) {
        if (dto.getSubjectId() != null) {
            SubjectDomain s = byId.get(dto.getSubjectId());
            if (s == null) {
                throw new InvalidMarksException("Subject not in student's department/semester");
            }
            return s;
        } else if (dto.getSubjectName() != null && !dto.getSubjectName().isBlank()) {
            SubjectDomain s = byName.get(dto.getSubjectName().trim().toLowerCase());
            if (s == null) {
                throw new InvalidMarksException("Subject not in student's department/semester");
            }
            return s;
        } else {
            throw new InvalidMarksException("Subject must be provided");
        }
    }
}
