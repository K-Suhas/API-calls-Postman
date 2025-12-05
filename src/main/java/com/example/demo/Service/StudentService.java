package com.example.demo.Service;

import com.example.demo.DTO.BulkStudentDTO;
import com.example.demo.DTO.StudentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudentService  {
        String createstudent(StudentDTO student);
        StudentDTO getstudentbyid(Long id);
        Page<StudentDTO> getallstudent(Pageable pageable);
        String updatestudent(Long id, StudentDTO student);
        String deletestudent(Long id);
        Page<StudentDTO> searchStudents(String query, Pageable pageable);
        List<String> addStudentsInBulk(BulkStudentDTO bulkDto);




}

