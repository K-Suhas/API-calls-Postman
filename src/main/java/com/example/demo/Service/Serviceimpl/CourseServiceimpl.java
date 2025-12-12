package com.example.demo.Service.Serviceimpl;

import com.example.demo.DTO.CourseDTO;
import com.example.demo.DTO.DepartmentDTO;
import com.example.demo.Domain.CourseDomain;
import com.example.demo.Domain.DepartmentDomain;
import com.example.demo.ExceptionHandler.DuplicateResourceException;
import com.example.demo.ExceptionHandler.ResourceNotFoundException;
import com.example.demo.Mapper.CourseMapper;
import com.example.demo.Repository.CourseRepository;
import com.example.demo.Repository.DepartmentRepository;
import com.example.demo.Service.CourseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceimpl implements CourseService {
    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;

    public CourseServiceimpl(CourseRepository courseRepository,
                             DepartmentRepository departmentRepository) {
        this.courseRepository = courseRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public String createCourse(CourseDTO course) {
        if (course.getDepartmentId() == null) {
            throw new ResourceNotFoundException("Department must be selected before adding a course");
        }

        List<CourseDomain> matches = courseRepository.findByNameIgnoreCase(course.getName().trim());
        if (!matches.isEmpty()) {
            throw new DuplicateResourceException("Course already exists with name: " + course.getName());
        }

        DepartmentDomain dept = departmentRepository.findById(course.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + course.getDepartmentId()));

        CourseDomain domain = CourseMapper.toDomain(course);
        domain.setDepartment(dept);

        courseRepository.save(domain);
        return "Course Created: " + course.getName() + " in Department " + dept.getName();
    }

    @Override
    public CourseDTO getCourseById(Long id) {
        CourseDomain domain = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + id));
        return CourseMapper.toDTO(domain);
    }

    @Override
    public Page<CourseDTO> getAllCourses(Pageable pageable) {
        Page<CourseDomain> page = courseRepository.findAll(pageable);
        if (page.isEmpty()) {
            throw new ResourceNotFoundException("No courses found");
        }
        return page.map(CourseMapper::toDTO);
    }

    @Override
    public String updateCourse(Long id, CourseDTO course) {
        CourseDomain existing = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + id));

        if (course.getName() != null && !course.getName().isBlank()) {
            List<CourseDomain> duplicates = courseRepository.findByNameIgnoreCase(course.getName().trim());
            boolean conflict = duplicates.stream().anyMatch(c -> !c.getId().equals(id));
            if (conflict) {
                throw new DuplicateResourceException("Another course already exists with name: " + course.getName());
            }
            existing.setName(course.getName().trim());
        }

        DepartmentDomain dept = existing.getDepartment();
        existing.setDepartment(dept);

        courseRepository.save(existing);
        return "Course updated: " + existing.getName() + " (Department: " + (dept != null ? dept.getName() : "N/A") + ")";
    }

    @Override
    public String deleteCourse(Long id) {
        CourseDomain existing = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + id));

        courseRepository.delete(existing);
        return "Course deleted: " + existing.getName();
    }

    @Override
    public Page<CourseDTO> searchCourses(String query, Pageable pageable) {
        Page<CourseDomain> result;
        try {
            Long id = Long.parseLong(query);
            result = courseRepository.searchByIdOrName(id, query, pageable);
        } catch (NumberFormatException ignored) {
            result = courseRepository.searchByName(query, pageable);
        }
        if (result.isEmpty()) {
            throw new ResourceNotFoundException("No courses match the search query: " + query);
        }
        return result.map(CourseMapper::toDTO);
    }

    @Override
    public Page<CourseDTO> searchCoursesByDepartment(String query, Long departmentId, Pageable pageable) {
        departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + departmentId));

        Page<CourseDomain> result;
        try {
            Long id = Long.parseLong(query);
            result = courseRepository.searchByIdOrNameInDepartment(departmentId, id, query, pageable);
        } catch (NumberFormatException ignored) {
            result = courseRepository.searchByIdOrNameInDepartment(departmentId, null, query, pageable);
        }
        if (result.isEmpty()) {
            throw new ResourceNotFoundException("No courses match the search query in this department: " + query);
        }
        return result.map(CourseMapper::toDTO);
    }

    @Override
    public List<CourseDTO> getCoursesByDepartment(Long departmentId) {
        return courseRepository.findByDepartment_Id(departmentId)
                .stream()
                .map(CourseMapper::toDTO)
                .toList();
    }

    @Override
    public List<DepartmentDTO> getAllDepartments() {
        return departmentRepository.findAll()
                .stream()
                .map(dept -> new DepartmentDTO(dept.getId(), dept.getName()))
                .toList();
    }
}
