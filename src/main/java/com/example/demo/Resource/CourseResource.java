// src/main/java/com/example/demo/Resource/CourseResource.java
package com.example.demo.Resource;

import com.example.demo.DTO.CourseDTO;
import com.example.demo.DTO.DepartmentDTO;
import com.example.demo.Domain.DepartmentDomain;
import com.example.demo.Enum.Role;
import com.example.demo.Service.CourseService;
import com.example.demo.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
// src/main/java/com/example/demo/Resource/CourseResource.java
@RestController
@RequestMapping("/course")
@CrossOrigin(origins = "http://localhost:4200")
public class CourseResource {

    private final CourseService courseService;
    private final UserService userService;  // ✅ injected bean

    public CourseResource(CourseService courseService, UserService userService) {
        this.courseService = courseService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<String> createCourse(@Valid @RequestBody CourseDTO course) {
        String result = courseService.createCourse(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping
    public ResponseEntity<Page<CourseDTO>> getAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<CourseDTO> courses = courseService.getAllCourses(PageRequest.of(page, size));
        return ResponseEntity.ok(courses);
    }

    // ✅ Search with optional department scoping
    @GetMapping("/search")
    public ResponseEntity<Page<CourseDTO>> searchCourses(
            @RequestParam String query,
            @RequestParam(required = false) Long deptId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageRequest pr = PageRequest.of(page, size);
        Page<CourseDTO> results = (deptId != null)
                ? courseService.searchCoursesByDepartment(query, deptId, pr)
                : courseService.searchCourses(query, pr);

        return ResponseEntity.ok(results);
    }


    // ✅ Provide departments for frontend dropdowns
    @GetMapping("/departments")
    public ResponseEntity<List<DepartmentDTO>> getDepartments() {
        return ResponseEntity.ok(courseService.getAllDepartments());
    }



    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {
        CourseDTO course = courseService.getCourseById(id);
        return ResponseEntity.ok(course);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateCourse(@PathVariable Long id, @RequestBody CourseDTO course) {
        String result = courseService.updateCourse(id, course);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable Long id) {
        String result = courseService.deleteCourse(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/by-department/{deptId}")
    public ResponseEntity<List<CourseDTO>> getByDepartment(@PathVariable Long deptId) {
        return ResponseEntity.ok(courseService.getCoursesByDepartment(deptId));
    }

    @GetMapping("/restricted-courses")
    public ResponseEntity<?> getCoursesRestricted(@RequestParam String email,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        Role role = userService.getUserRole(email);
        if (role == Role.STUDENT) {
            return ResponseEntity.ok(courseService.getCoursesForStudentRole(email));
        }
        // ✅ supply a Pageable
        return ResponseEntity.ok(courseService.getAllCourses(PageRequest.of(page, size)));
    }


}
