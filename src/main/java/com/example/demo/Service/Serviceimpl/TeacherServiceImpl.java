package com.example.demo.Service.Serviceimpl;

import com.example.demo.Domain.TeacherDomain;
import com.example.demo.DTO.TeacherDTO;
import com.example.demo.Mapper.TeacherMapper;
import com.example.demo.Repository.TeacherRepository;
import com.example.demo.Repository.UserRepository; // ✅ assuming you have UserRepository
import com.example.demo.Service.TeacherService;
import com.example.demo.ExceptionHandler.TeacherNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;

    public TeacherServiceImpl(TeacherRepository teacherRepository, UserRepository userRepository) {
        this.teacherRepository = teacherRepository;
        this.userRepository = userRepository;
    }

    @Override
    public TeacherDTO addTeacher(TeacherDTO teacherDTO) {
        TeacherDomain teacher = TeacherMapper.toDomain(teacherDTO);
        return TeacherMapper.toDTO(teacherRepository.save(teacher));
    }

    @Override
    public TeacherDTO updateTeacher(Long id, TeacherDTO teacherDTO) {
        TeacherDomain teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new TeacherNotFoundException("Teacher not found with id " + id));
        teacher.setName(teacherDTO.getName());
        teacher.setEmail(teacherDTO.getEmail());
        teacher.setDept(teacherDTO.getDept());
        return TeacherMapper.toDTO(teacherRepository.save(teacher));
    }

    @Override
    public void deleteTeacher(Long id) {
        TeacherDomain teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new TeacherNotFoundException("Teacher not found with id " + id));

        teacherRepository.delete(teacher);
        userRepository.findByEmail(teacher.getEmail())
                .ifPresent(userRepository::delete);
    }

    @Override
    public List<TeacherDTO> getAllTeachers() {
        return teacherRepository.findAll()
                .stream()
                .map(TeacherMapper::toDTO)
                .toList();
    }

    @Override
    public Page<TeacherDTO> searchTeachers(String query, Pageable pageable) {
        return teacherRepository.searchByNameDeptOrEmail(query, pageable)
                .map(TeacherMapper::toDTO);
    }
}
