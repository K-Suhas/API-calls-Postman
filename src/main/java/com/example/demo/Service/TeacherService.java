package com.example.demo.Service;

import com.example.demo.DTO.TeacherDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TeacherService {
    String createTeacher(TeacherDTO teacher);
    TeacherDTO getTeacherById(Long id);
    Page<TeacherDTO> getAllTeachers(Pageable pageable);
    String updateTeacher(Long id, TeacherDTO teacher);
    String deleteTeacher(Long id);
    Page<TeacherDTO> searchTeachers(String query, Pageable pageable);
    List<TeacherDTO> getTeachersByDepartment(Long departmentId);
    Optional<TeacherDTO> findByEmail(String email);
}
