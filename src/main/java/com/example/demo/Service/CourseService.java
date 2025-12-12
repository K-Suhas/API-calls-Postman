package com.example.demo.Service;

import com.example.demo.DTO.CourseDTO;
import com.example.demo.DTO.DepartmentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CourseService {

    String createCourse(CourseDTO course);
    CourseDTO getCourseById(Long id);
    Page<CourseDTO> getAllCourses(Pageable pageable);
    String updateCourse(Long id, CourseDTO course);
    String deleteCourse(Long id);
    Page<CourseDTO> searchCourses(String query, Pageable pageable);
    Page<CourseDTO> searchCoursesByDepartment(String query, Long departmentId, Pageable pageable);


    List<CourseDTO> getCoursesByDepartment(Long departmentId);

    // ✅ For populating dropdowns in frontend
    List<DepartmentDTO> getAllDepartments();
}
