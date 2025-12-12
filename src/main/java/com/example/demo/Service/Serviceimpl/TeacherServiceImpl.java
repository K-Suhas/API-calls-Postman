// src/main/java/com/example/demo/Service/Serviceimpl/TeacherServiceImpl.java
package com.example.demo.Service.Serviceimpl;

import com.example.demo.Domain.DepartmentDomain;
import com.example.demo.Domain.TeacherDomain;
import com.example.demo.DTO.TeacherDTO;
import com.example.demo.Mapper.TeacherMapper;
import com.example.demo.Repository.DepartmentRepository;
import com.example.demo.Repository.TeacherRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.TeacherService;
import com.example.demo.ExceptionHandler.TeacherNotFoundException;
import com.example.demo.ExceptionHandler.ResourceNotFoundException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    public TeacherServiceImpl(TeacherRepository teacherRepository,
                              UserRepository userRepository,
                              DepartmentRepository departmentRepository) {
        this.teacherRepository = teacherRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public String createTeacher(TeacherDTO teacherDTO) {
        DepartmentDomain dept = departmentRepository.findById(teacherDTO.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id " + teacherDTO.getDepartmentId()));
        TeacherDomain saved = teacherRepository.save(TeacherMapper.toDomain(teacherDTO, dept));
        return "Teacher created successfully with id " + saved.getId();
    }

    @Override
    public TeacherDTO getTeacherById(Long id) {
        TeacherDomain teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new TeacherNotFoundException("Teacher not found with id " + id));
        return TeacherMapper.toDTO(teacher);
    }

    @Override
    public Page<TeacherDTO> getAllTeachers(Pageable pageable) {
        return teacherRepository.findAll(pageable).map(TeacherMapper::toDTO);
    }

    @Override
    public String updateTeacher(Long id, TeacherDTO teacherDTO) {
        TeacherDomain teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new TeacherNotFoundException("Teacher not found with id " + id));

        DepartmentDomain dept = teacher.getDepartment();
        if (teacherDTO.getDepartmentId() != null && !teacherDTO.getDepartmentId().equals(dept.getId())) {
            dept = departmentRepository.findById(teacherDTO.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id " + teacherDTO.getDepartmentId()));
        }

        teacher.setName(teacherDTO.getName());
        teacher.setEmail(teacherDTO.getEmail());
        teacher.setDepartment(dept);

        teacherRepository.save(teacher);
        return "Teacher updated successfully";
    }

    @Override
    public String deleteTeacher(Long id) {
        TeacherDomain teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new TeacherNotFoundException("Teacher not found with id " + id));
        teacherRepository.delete(teacher);
        userRepository.findByEmail(teacher.getEmail()).ifPresent(userRepository::delete);
        return "Teacher deleted successfully";
    }

    @Override
    public Page<TeacherDTO> searchTeachers(String query, Pageable pageable) {
        Long id = null;
        try { id = Long.valueOf(query); } catch (NumberFormatException ignored) {}
        return teacherRepository.searchByIdOrNameOrDeptOrEmail(id, query, pageable).map(TeacherMapper::toDTO);
    }

    @Override
    public List<TeacherDTO> getTeachersByDepartment(Long departmentId) {
        return teacherRepository.findByDepartment_Id(departmentId).stream().map(TeacherMapper::toDTO).toList();
    }

    @Override
    public Optional<TeacherDTO> findByEmail(String email) {
        return teacherRepository.findByEmailIgnoreCase(email).map(TeacherMapper::toDTO);
    }



}
