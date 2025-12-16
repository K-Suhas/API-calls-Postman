// src/main/java/com/example/demo/Service/Serviceimpl/SubjectServiceImpl.java
package com.example.demo.Service.Serviceimpl;

import com.example.demo.DTO.SubjectDTO;
import com.example.demo.Domain.DepartmentDomain;
import com.example.demo.Domain.StudentDomain;
import com.example.demo.Domain.SubjectDomain;
import com.example.demo.ExceptionHandler.DuplicateResourceException;
import com.example.demo.ExceptionHandler.ResourceNotFoundException;
import com.example.demo.Mapper.SubjectMapper;
import com.example.demo.Repository.DepartmentRepository;
import com.example.demo.Repository.StudentRepository;
import com.example.demo.Repository.SubjectRepository;
import com.example.demo.Service.SubjectService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;
    private final DepartmentRepository departmentRepository;
    private final StudentRepository studentRepository;

    public SubjectServiceImpl(SubjectRepository subjectRepository,
                              DepartmentRepository departmentRepository,
                              StudentRepository studentRepository) {
        this.subjectRepository = subjectRepository;
        this.departmentRepository = departmentRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public String createSubject(SubjectDTO dto) {
        DepartmentDomain dept = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        subjectRepository.findByNameIgnoreCaseAndDepartment_IdAndSemester(dto.getName(), dept.getId(), dto.getSemester())
                .ifPresent(s -> { throw new DuplicateResourceException("Subject already exists in department and semester"); });

        SubjectDomain subject = new SubjectDomain()
                .setName(dto.getName())
                .setSemester(dto.getSemester())
                .setDepartment(dept);

        subjectRepository.save(subject);
        return "Subject created successfully";
    }

    @Override
    public List<SubjectDTO> getSubjectsByDepartmentAndSemester(Long departmentId, int semester) {
        return subjectRepository.findByDepartment_IdAndSemester(departmentId, semester)
                .stream().map(SubjectMapper::toDTO).toList();
    }

    @Override
    public List<SubjectDTO> getSubjectsForStudent(Long studentId, int semester) {
        StudentDomain student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        return subjectRepository.findByDepartment_IdAndSemester(student.getDepartment().getId(), semester)
                .stream().map(SubjectMapper::toDTO).toList();
    }
    @Override
    public String updateSubject(Long id, SubjectDTO dto) {
        SubjectDomain subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        DepartmentDomain dept = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        // Prevent duplicate in same dept+semester
        subjectRepository.findByNameIgnoreCaseAndDepartment_IdAndSemester(dto.getName(), dept.getId(), dto.getSemester())
                .filter(s -> !s.getId().equals(id))
                .ifPresent(s -> { throw new DuplicateResourceException("Subject already exists in department and semester"); });

        subject.setName(dto.getName());
        subject.setSemester(dto.getSemester());
        subject.setDepartment(dept);

        subjectRepository.save(subject);
        return "Subject updated successfully";
    }

    @Override
    public String deleteSubject(Long id) {
        SubjectDomain subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        subjectRepository.delete(subject);
        return "Subject deleted successfully";
    }

}
