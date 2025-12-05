package com.example.demo.Resource;

import com.example.demo.DTO.CourseDTO;
import com.example.demo.Service.CourseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/course")
@CrossOrigin(origins = "http://localhost:4200")
public class CourseResource {


    private final CourseService courseService;
    public CourseResource(CourseService courseService)
    {
        this.courseService=courseService;
    }

    @PostMapping
    public ResponseEntity<String> createCourse(@Valid @RequestBody CourseDTO course) {
        String result = courseService.createCourse(course); // throws if duplicate
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }


    @GetMapping
    public ResponseEntity<Page<CourseDTO>> getAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<CourseDTO> courses = courseService.getAllCourses(PageRequest.of(page, size)); // throws if empty
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<CourseDTO>> searchCourses(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<CourseDTO> results = courseService.searchCourses(query, PageRequest.of(page, size)); // throws if empty
        return ResponseEntity.ok(results);
    }


    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {
        CourseDTO course = courseService.getCourseById(id); // throws if not found
        return ResponseEntity.ok(course);
    }


    @PutMapping("/{id}")
    public ResponseEntity<String> updateCourse(@PathVariable Long id, @RequestBody CourseDTO course) {
        String result = courseService.updateCourse(id, course); // throws if not found or duplicate
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable Long id) {
        String result = courseService.deleteCourse(id); // throws if not found
        return ResponseEntity.ok(result);
    }
}
