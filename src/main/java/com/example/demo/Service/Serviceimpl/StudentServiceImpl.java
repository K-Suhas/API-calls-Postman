package com.example.demo.Service.Serviceimpl;

import com.example.demo.DTO.BulkStudentDTO;
import com.example.demo.Domain.CourseDomain;
import com.example.demo.Domain.StudentDomain;
import com.example.demo.DTO.StudentDTO;
import com.example.demo.ExceptionHandler.BulkValidationException;
import com.example.demo.ExceptionHandler.DuplicateResourceException;
import com.example.demo.ExceptionHandler.ResourceNotFoundException;
import com.example.demo.Mapper.StudentMapper;
import com.example.demo.Repository.CourseRepository;
import com.example.demo.Repository.StudentRepository;
import com.example.demo.Service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;

    @Override
    public String createstudent(StudentDTO student) {
        List<StudentDomain> matches = studentRepository.findByNameAndDobAndDept(
                student.getName().trim(), student.getDob(), student.getDept().trim()
        );

        if (!matches.isEmpty()) {
            throw new DuplicateResourceException("Student already exists with same name, DOB, and department");
        }

        StudentDomain domain = StudentMapper.toDomain(student);
        domain.setEmail(student.getEmail());

        if (student.getCourseNames() != null && !student.getCourseNames().isEmpty()) {
            List<CourseDomain> courses = courseRepository.findAll().stream()
                    .filter(c -> student.getCourseNames().contains(c.getName()))
                    .collect(Collectors.toList());

            if (courses.size() != student.getCourseNames().size()) {
                throw new ResourceNotFoundException("One or more course names are invalid");
            }

            domain.setCourses(new HashSet<>(courses));
        }

        studentRepository.save(domain);
        return "Student Created: " + student.getName();
    }

    @Override
    public StudentDTO getstudentbyid(Long id) {
        StudentDomain domain = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));
        return StudentMapper.toDTO(domain);
    }

    @Override
    public Page<StudentDTO> getallstudent(Pageable pageable) {
        Page<StudentDomain> page = studentRepository.findAll(pageable);
        if (page.isEmpty()) {
            throw new ResourceNotFoundException("No students found");
        }
        return page.map(StudentMapper::toDTO);
    }

    private LocalDate parseDate(String input) {
        try {
            return LocalDate.parse(input, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Page<StudentDTO> searchStudents(String query, Pageable pageable) {
        Page<StudentDomain> result;

        try {
            Long id = Long.parseLong(query);
            result = studentRepository.searchByIdOrNameOrDept(id, query, pageable);
        } catch (NumberFormatException e) {
            LocalDate dob = parseDate(query);
            if (dob != null) {
                result = studentRepository.searchByDob(dob, pageable);
            } else {
                result = studentRepository.searchByNameOrDept(query, pageable);
            }
        }

        if (result.isEmpty()) {
            throw new ResourceNotFoundException("No students match the search query: " + query);
        }

        return result.map(StudentMapper::toDTO);
    }

    @Override
    public String updatestudent(Long id, StudentDTO student) {
        StudentDomain existing = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));

        List<StudentDomain> duplicates = studentRepository.findByNameAndDobAndDept(
                student.getName().trim(), student.getDob(), student.getDept().trim()
        );

        boolean conflict = duplicates.stream().anyMatch(s -> !s.getId().equals(id));
        if (conflict) {
            throw new DuplicateResourceException("Another student already exists with same name, DOB, and department");
        }

        existing.setName(student.getName());
        existing.setDept(student.getDept());
        existing.setDob(student.getDob());
        existing.setEmail(student.getEmail());

        if (student.getCourseNames() != null && !student.getCourseNames().isEmpty()) {
            List<CourseDomain> matched = courseRepository.findAll().stream()
                    .filter(c -> student.getCourseNames().contains(c.getName()))
                    .collect(Collectors.toList());

            if (matched.size() != student.getCourseNames().size()) {
                throw new ResourceNotFoundException("One or more course names are invalid");
            }

            existing.setCourses(new HashSet<>(matched));
        } else {
            existing.setCourses(new HashSet<>());
        }

        studentRepository.save(existing);
        return "Student updated: " + student.getName();
    }

    @Override
    public String deletestudent(Long id) {
        StudentDomain existing = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));

        studentRepository.delete(existing);
        return "Student deleted: " + existing.getName();
    }
    @Override
    public List<String> addStudentsInBulk(BulkStudentDTO bulkDto) {
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < bulkDto.getStudents().size(); i++) {
            StudentDTO dto = bulkDto.getStudents().get(i);
            List<String> rowErrors = new ArrayList<>();

            if (dto.getName() == null || dto.getName().isBlank()) rowErrors.add("Missing name");
            if (dto.getDob() == null) rowErrors.add("Missing DOB");
            if (dto.getDept() == null || dto.getDept().isBlank()) rowErrors.add("Missing department");
            if (dto.getCourseNames() == null || dto.getCourseNames().isEmpty()) rowErrors.add("Missing course names");
            if (dto.getEmail() == null || dto.getEmail().isBlank()) rowErrors.add("Missing email");
            else if (!dto.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) rowErrors.add("Invalid email format");

            // ✅ Duplicate check (name+dob+dept)
            boolean exists = studentRepository.findExistingStudent(dto.getName(), dto.getDob(), dto.getDept()).isPresent();
            if (exists) rowErrors.add("Duplicate student");

            // ✅ Duplicate email check
            boolean emailExists = studentRepository.findByEmail(dto.getEmail()).isPresent();
            if (emailExists) rowErrors.add("Duplicate email");

            if (!rowErrors.isEmpty()) {
                errors.add("Row " + (i + 1) + ": " + String.join(", ", rowErrors));
                continue;
            }

            List<CourseDomain> courses = courseRepository.findByNameInIgnoreCase(dto.getCourseNames());
            if (courses.size() != dto.getCourseNames().size()) {
                List<String> missing = new ArrayList<>(dto.getCourseNames());
                missing.removeAll(courses.stream().map(CourseDomain::getName).toList());
                rowErrors.add("Invalid course names: " + String.join(", ", missing));
                errors.add("Row " + (i + 1) + ": " + String.join(", ", rowErrors));
                continue;
            }

            StudentDomain student = StudentMapper.toDomain(dto, courses);
            student.setEmail(dto.getEmail()); // ✅ Persist email
            studentRepository.save(student);
        }

        if (!errors.isEmpty()) throw new BulkValidationException(errors);
        return errors;
    }


}






