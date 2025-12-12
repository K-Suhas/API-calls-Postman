package com.example.demo.Service.Serviceimpl;

import com.example.demo.DTO.BulkStudentDTO;
import com.example.demo.Domain.CourseDomain;
import com.example.demo.Domain.DepartmentDomain;
import com.example.demo.Domain.StudentDomain;
import com.example.demo.DTO.StudentDTO;
import com.example.demo.ExceptionHandler.DuplicateResourceException;
import com.example.demo.ExceptionHandler.ResourceNotFoundException;
import com.example.demo.Mapper.StudentMapper;
import com.example.demo.Repository.CourseRepository;
import com.example.demo.Repository.DepartmentRepository;
import com.example.demo.Repository.StudentRepository;
import com.example.demo.Service.StudentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;

    public StudentServiceImpl(StudentRepository studentRepository,
                              CourseRepository courseRepository,
                              DepartmentRepository departmentRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.departmentRepository = departmentRepository;
    }

    private static final String STUDENT_NOT_FOUND_MESSAGE = "Student not found with ID: ";

    // ✅ CREATE STUDENT
    @Override
    public String createstudent(StudentDTO student) {
        DepartmentDomain dept = departmentRepository.findById(student.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department not found with id: " + student.getDepartmentId()
                ));

        // ✅ Duplicate check by email
        if (studentRepository.findByEmail(student.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Duplicate email: " + student.getEmail());
        }

        // ✅ Duplicate check by name + dob + department
        Optional<StudentDomain> existing = studentRepository.findExistingStudent(
                student.getName().trim(),
                LocalDate.parse(student.getDob()),
                dept.getName()
        );
        if (existing.isPresent()) {
            throw new DuplicateResourceException(
                    "Duplicate student: " + student.getName() + " in " + dept.getName()
            );
        }

        // Validate courses
        List<CourseDomain> filteredCourses = List.of();
        if (student.getCourseNames() != null && !student.getCourseNames().isEmpty()) {
            List<CourseDomain> matched = courseRepository.findByNameInIgnoreCase(student.getCourseNames());
            filteredCourses = matched.stream()
                    .filter(c -> c.getDepartment() != null &&
                            c.getDepartment().getId().equals(dept.getId()))
                    .toList();

            if (filteredCourses.size() != student.getCourseNames().size()) {
                throw new ResourceNotFoundException(
                        "One or more course names are invalid for this department"
                );
            }
        }

        StudentDomain domain = StudentMapper.toDomain(student, dept, filteredCourses);
        studentRepository.save(domain);

        return "Student Created: " + student.getName();
    }


    // ✅ GET STUDENT BY ID
    @Override
    public StudentDTO getstudentbyid(Long id) {
        StudentDomain domain = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(STUDENT_NOT_FOUND_MESSAGE + id));
        return StudentMapper.toDTO(domain);
    }

    // ✅ GET ALL STUDENTS
    @Override
    public Page<StudentDTO> getallstudent(Pageable pageable) {
        Page<StudentDomain> page = studentRepository.findAll(pageable);
        if (page.isEmpty()) {
            throw new ResourceNotFoundException("No students found");
        }
        return page.map(StudentMapper::toDTO);
    }

    // ✅ SEARCH STUDENTS
    @Override
    public Page<StudentDTO> searchStudents(String query, Pageable pageable) {
        Long id = null;
        try {
            id = Long.parseLong(query); // numeric query → id
        } catch (NumberFormatException ignored) {}

        Page<StudentDomain> results = studentRepository.searchByIdOrNameOrDept(id, query, pageable);

        if (results.isEmpty()) {
            throw new ResourceNotFoundException("There is no student called " + query);
        }

        return results.map(StudentMapper::toDTO);
    }




    // ✅ UPDATE STUDENT
    @Override
    public String updatestudent(Long id, StudentDTO student) {

        StudentDomain existing = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));

        // Determine department (existing or new)
        DepartmentDomain dept = existing.getDepartment();
        if (student.getDepartmentId() != null && !student.getDepartmentId().equals(dept.getId())) {
            dept = departmentRepository.findById(student.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Department not found with id " + student.getDepartmentId()
                    ));
        }

        final DepartmentDomain finalDept = dept;

        // Update basic fields
        existing.setName(student.getName());
        existing.setEmail(student.getEmail());
        existing.setDepartment(finalDept);

        // Convert dob String → LocalDate via mapper
        if (student.getDob() != null && !student.getDob().isBlank()) {
            existing.setDob(StudentMapper.toDomain(student).getDob());
        }

        // ✅ Enforce course selection
        if (student.getCourseNames() == null || student.getCourseNames().isEmpty()) {
            throw new ResourceNotFoundException("At least one course must be selected");
        }

        // Validate and filter courses by department
        List<CourseDomain> matched = courseRepository.findByNameInIgnoreCase(student.getCourseNames());
        List<CourseDomain> filteredCourses = matched.stream()
                .filter(c -> c.getDepartment() != null &&
                        c.getDepartment().getId().equals(finalDept.getId()))
                .toList();

        if (filteredCourses.size() != student.getCourseNames().size()) {
            throw new ResourceNotFoundException("One or more course names are invalid for this department");
        }

        existing.setCourses(new HashSet<>(filteredCourses));

        studentRepository.save(existing);

        return "Student updated: " + student.getName();
    }



    // ✅ DELETE STUDENT
    @Override
    public String deletestudent(Long id) {
        StudentDomain existing = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(STUDENT_NOT_FOUND_MESSAGE + id));

        studentRepository.delete(existing);
        return "Student deleted: " + existing.getName();
    }

    @Override
    public Page<StudentDTO> getStudentsByDepartment(Long deptId, Pageable pageable) {
        return studentRepository.findByDepartment_Id(deptId, pageable)
                .map(StudentMapper::toDTO);
    }


    // ✅ CREATE STUDENT FOR DEPARTMENT
    @Override
    public String createstudentForDepartment(StudentDTO student, Long departmentId) {

        DepartmentDomain dept = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + departmentId));

        student.setDepartmentId(departmentId);

        Optional<StudentDomain> existing = studentRepository.findExistingStudent(
                student.getName().trim(),
                LocalDate.parse(student.getDob()),   // ✅ convert String → LocalDate
                dept.getName()
        );


        if (existing.isPresent()) {
            throw new DuplicateResourceException("Student already exists with same name, DOB, and department");
        }

        List<CourseDomain> filteredCourses = List.of();

        if (student.getCourseNames() != null && !student.getCourseNames().isEmpty()) {
            List<CourseDomain> matched = courseRepository.findByNameInIgnoreCase(student.getCourseNames());
            filteredCourses = matched.stream()
                    .filter(c -> c.getDepartment() != null &&
                            c.getDepartment().getId().equals(departmentId))
                    .toList();

            if (filteredCourses.size() != student.getCourseNames().size()) {
                throw new ResourceNotFoundException("One or more course names are invalid for this department");
            }
        }

        StudentDomain domain = StudentMapper.toDomain(student, dept, filteredCourses);
        studentRepository.save(domain);

        return "Student Created: " + student.getName();
    }

    // ✅ BULK UPLOAD (not implemented yet)
    @Override
    public List<String> addStudentsInBulk(BulkStudentDTO bulkDto) {
        List<String> errors = new ArrayList<>();
        List<StudentDomain> toSave = new ArrayList<>();

        for (StudentDTO dto : bulkDto.getStudents()) {
            try {
                // Validate department
                DepartmentDomain dept = departmentRepository.findByNameIgnoreCase(dto.getDepartmentName())
                        .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + dto.getDepartmentName()));

                // Validate DOB
                if (dto.getDob() == null || dto.getDob().isBlank()) {
                    throw new ResourceNotFoundException("DOB missing for student: " + dto.getName());
                }
                LocalDate dob = LocalDate.parse(dto.getDob());

                // Validate email
                if (dto.getEmail() == null || !dto.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                    throw new ResourceNotFoundException("Invalid email for student: " + dto.getName());
                }

                // ✅ Duplicate check by email
                if (studentRepository.findByEmail(dto.getEmail()).isPresent()) {
                    throw new DuplicateResourceException("Duplicate email: " + dto.getEmail());
                }

                // ✅ Duplicate check by name + dob + department
                Optional<StudentDomain> existing = studentRepository.findExistingStudent(
                        dto.getName().trim(), dob, dept.getName()
                );
                if (existing.isPresent()) {
                    throw new DuplicateResourceException("Duplicate student: " + dto.getName() + " in " + dept.getName());
                }

                // Validate courses
                if (dto.getCourseNames() == null || dto.getCourseNames().isEmpty()) {
                    throw new ResourceNotFoundException("Courses missing for student: " + dto.getName());
                }
                List<CourseDomain> matched = courseRepository.findByNameInIgnoreCase(dto.getCourseNames());
                List<CourseDomain> filtered = matched.stream()
                        .filter(c -> c.getDepartment().getId().equals(dept.getId()))
                        .toList();
                if (filtered.size() != dto.getCourseNames().size()) {
                    throw new ResourceNotFoundException("Invalid course(s) for student: " + dto.getName());
                }

                // Build domain object but don’t save yet
                StudentDomain domain = StudentMapper.toDomain(dto, dept, filtered);
                toSave.add(domain);

            } catch (Exception e) {
                errors.add("Row failed for " + dto.getEmail() + ": " + e.getMessage());
            }
        }

        if (!errors.isEmpty()) {
            // Abort, do not save anything
            return errors;
        }

        // Save all students only if no errors
        studentRepository.saveAll(toSave);
        return Collections.emptyList();
    }




}
