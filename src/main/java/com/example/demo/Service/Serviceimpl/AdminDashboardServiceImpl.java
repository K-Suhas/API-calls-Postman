package com.example.demo.Service.Serviceimpl;

import com.example.demo.DTO.StudentDTO;
import com.example.demo.DTO.CourseDTO;
import com.example.demo.Mapper.StudentMapper;
import com.example.demo.Mapper.CourseMapper;
import com.example.demo.Repository.StudentRepository;
import com.example.demo.Repository.CourseRepository;
import com.example.demo.Service.AdminDashboardService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

    @Autowired private StudentRepository studentRepo;
    @Autowired private CourseRepository courseRepo;

    @Async
    @Transactional(readOnly = true)
    public CompletableFuture<List<StudentDTO>> getAllStudentsAsync() {
        List<StudentDTO> dtos = studentRepo.findAll()
                .stream()
                .map(StudentMapper::toDTO)
                .collect(Collectors.toList());
        return CompletableFuture.completedFuture(dtos);
    }

    @Async
    @Transactional(readOnly = true)
    public CompletableFuture<List<CourseDTO>> getAllCoursesAsync() {
        List<CourseDTO> dtos = courseRepo.findAll()
                .stream()
                .map(CourseMapper::toDTO)
                .collect(Collectors.toList());
        return CompletableFuture.completedFuture(dtos);
    }
}
