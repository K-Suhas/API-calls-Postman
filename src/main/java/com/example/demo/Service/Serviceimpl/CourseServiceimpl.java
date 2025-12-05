package com.example.demo.Service.Serviceimpl;

import com.example.demo.DTO.CourseDTO;
import com.example.demo.Domain.CourseDomain;
import com.example.demo.ExceptionHandler.DuplicateResourceException;
import com.example.demo.ExceptionHandler.ResourceNotFoundException;
import com.example.demo.Mapper.CourseMapper;
import com.example.demo.Repository.CourseRepository;
import com.example.demo.Service.CourseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceimpl implements CourseService {
    private final CourseRepository courseRepository;
    public CourseServiceimpl(CourseRepository courseRepository)
    {
        this.courseRepository=courseRepository;
    }
    @Override
    public String createCourse(CourseDTO course) {
        List<CourseDomain> matches = courseRepository.findByNameIgnoreCase(course.getName().trim());
        if (!matches.isEmpty()) {
            throw new DuplicateResourceException("Course already exists with name: " + course.getName());
        }

        CourseDomain domain = CourseMapper.toDomain(course);
        courseRepository.save(domain);
        return "Course Created: " + course.getName();
    }



    private static final String COURSE_NOT_FOUND_MESSAGE = "Course not found with ID: ";

    @Override
    public CourseDTO getCourseById(Long id) {
        CourseDomain domain = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(COURSE_NOT_FOUND_MESSAGE + id));
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
                .orElseThrow(() -> new ResourceNotFoundException(COURSE_NOT_FOUND_MESSAGE + id));

        List<CourseDomain> duplicates = courseRepository.findByNameIgnoreCase(course.getName().trim());
        boolean conflict = duplicates.stream().anyMatch(c -> !c.getId().equals(id));
        if (conflict) {
            throw new DuplicateResourceException("Another course already exists with name: " + course.getName());
        }

        existing.setName(course.getName().trim());
        courseRepository.save(existing);
        return "Course updated: " + course.getName();
    }


    @Override
    public String deleteCourse(Long id) {
        CourseDomain existing = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(COURSE_NOT_FOUND_MESSAGE + id));

        courseRepository.delete(existing);
        return "Course deleted: " + existing.getName();
    }


    @Override
    public Page<CourseDTO> searchCourses(String query, Pageable pageable) {
        Page<CourseDomain> result;

        try {
            Long id = Long.parseLong(query);
            result = courseRepository.searchByIdOrName(id, query, pageable);
        } catch (NumberFormatException _) {
            result = courseRepository.searchByName(query, pageable);
        }

        if (result.isEmpty()) {
            throw new ResourceNotFoundException("No courses match the search query: " + query);
        }

        return result.map(CourseMapper::toDTO);
    }

}
