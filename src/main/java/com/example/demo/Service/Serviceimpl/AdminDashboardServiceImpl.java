package com.example.demo.Service.Serviceimpl;

import com.example.demo.DTO.StudentDTO;
import com.example.demo.DTO.CourseDTO;
import com.example.demo.Mapper.StudentMapper;
import com.example.demo.Mapper.CourseMapper;
import com.example.demo.Repository.StudentRepository;
import com.example.demo.Repository.CourseRepository;
import com.example.demo.Service.AdminDashboardService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final StudentRepository studentRepo;
    private final CourseRepository courseRepo;
    public AdminDashboardServiceImpl(StudentRepository studentRepo,CourseRepository courseRepo)
    {
        this.courseRepo=courseRepo;
        this.studentRepo=studentRepo;
    }

    @Async
    @Transactional(readOnly = true)
    public CompletableFuture<List<StudentDTO>> getAllStudentsAsync() {
        List<StudentDTO> dtos = studentRepo.findAll()
                .stream()
                .map(StudentMapper::toDTO)
                .toList();
        return CompletableFuture.completedFuture(dtos);
    }

    @Async
    @Transactional(readOnly = true)
    public CompletableFuture<List<CourseDTO>> getAllCoursesAsync() {
        List<CourseDTO> dtos = courseRepo.findAll()
                .stream()
                .map(CourseMapper::toDTO)
                .toList();
        return CompletableFuture.completedFuture(dtos);
    }
}
