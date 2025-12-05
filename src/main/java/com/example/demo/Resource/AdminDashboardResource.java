package com.example.demo.Resource;

import com.example.demo.DTO.StudentDTO;
import com.example.demo.DTO.CourseDTO;
import com.example.demo.Service.AdminDashboardService;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminDashboardResource {

   private final AdminDashboardService dashboardService;
   public AdminDashboardResource(AdminDashboardService dashboardService)
   {
       this.dashboardService=dashboardService;
   }

    @GetMapping("/dashboard")
    public CompletableFuture<Map<String, Object>> getDashboardData() {
        CompletableFuture<List<StudentDTO>> studentsFuture = dashboardService.getAllStudentsAsync();
        CompletableFuture<List<CourseDTO>> coursesFuture = dashboardService.getAllCoursesAsync();

        return CompletableFuture.allOf(studentsFuture, coursesFuture)
                .thenApply(v -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("students", studentsFuture.join());
                    result.put("courses", coursesFuture.join());
                    return result;
                });
    }
}
