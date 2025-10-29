package com.example.demo.Service;

import com.example.demo.DTO.CourseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseService {

    String createCourse(CourseDTO course);
    CourseDTO getCourseById(Long id);
    Page<CourseDTO> getAllCourses(Pageable pageable);
    String updateCourse(Long id, CourseDTO course);
    String deleteCourse(Long id);
    Page<CourseDTO> searchCourses(String query, Pageable pageable);
}
