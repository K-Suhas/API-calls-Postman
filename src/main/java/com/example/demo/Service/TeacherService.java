package com.example.demo.Service;

import com.example.demo.DTO.TeacherDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TeacherService {
    TeacherDTO addTeacher(TeacherDTO teacherDTO);
    TeacherDTO updateTeacher(Long id, TeacherDTO teacherDTO);
    void deleteTeacher(Long id);
    List<TeacherDTO> getAllTeachers();
    Page<TeacherDTO> searchTeachers(String query, Pageable pageable);  // ✅ new
}
