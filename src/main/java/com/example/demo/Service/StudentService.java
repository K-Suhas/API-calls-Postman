package com.example.demo.Service;

import com.example.demo.DTO.StudentDTO;
import com.example.demo.Domain.StudentDomain;

import java.util.List;

public interface StudentService  {



        String createstudent(StudentDTO student);
        StudentDTO getstudentbyid(Long id);
        List<StudentDTO> getallstudent();
        String updatestudent(Long id, StudentDTO student);
        String deletestudent(Long id);


}

