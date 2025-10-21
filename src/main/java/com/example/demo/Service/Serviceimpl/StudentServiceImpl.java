package com.example.demo.Service.Serviceimpl;

import com.example.demo.Domain.StudentDomain;
import com.example.demo.DTO.StudentDTO;
import com.example.demo.Mapper.StudentMapper;
import com.example.demo.Repository.StudentRepository;
import com.example.demo.Service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;



    @Override
    public String createstudent(StudentDTO student) {
        StudentDomain domain = StudentMapper.toDomain(student);
        studentRepository.save(domain);
        return "Student Created: " + student.getName();
    }

    @Override
    public StudentDTO getstudentbyid(Long id) {
        StudentDomain domain = studentRepository.findById(id).orElse(null);
        return domain != null ? StudentMapper.toDTO(domain) : null;
    }

    @Override
    public List<StudentDTO> getallstudent() {
        return studentRepository.findAll().stream()
                .map(StudentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public String updatestudent(Long id, StudentDTO student) {
        StudentDomain existing = studentRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setName(student.getName());
            existing.setDept(student.getDept());
            existing.setDob(student.getDob());
            studentRepository.save(existing);
            return "Student updated: " + student.getName();
        }
        return "Student not found";
    }

    @Override
    public String deletestudent(Long id) {
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
            return "Student deleted: " + id;
        }
        return "Student not found: " + id;
    }
}
