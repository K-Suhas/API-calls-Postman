package com.example.demo.Service;

import com.example.demo.DTO.StudentDTO;
import com.example.demo.DTO.CourseDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AdminDashboardService {
    CompletableFuture<List<StudentDTO>> getAllStudentsAsync();
    CompletableFuture<List<CourseDTO>> getAllCoursesAsync();
}
