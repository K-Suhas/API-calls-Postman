// src/main/java/com/example/demo/Service/Serviceimpl/DepartmentServiceImpl.java
package com.example.demo.Service.Serviceimpl;

import com.example.demo.DTO.DepartmentDTO;
import com.example.demo.Domain.DepartmentDomain;
import com.example.demo.ExceptionHandler.DuplicateResourceException;
import com.example.demo.ExceptionHandler.ResourceNotFoundException;
import com.example.demo.Mapper.DepartmentMapper;
import com.example.demo.Repository.DepartmentRepository;
import com.example.demo.Service.DepartmentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository repo;

    public DepartmentServiceImpl(DepartmentRepository repo) {
        this.repo = repo;
    }

    @Override
    public DepartmentDTO createDepartment(DepartmentDTO dto) {
        repo.findByNameIgnoreCase(dto.getName())
                .ifPresent(d -> { throw new DuplicateResourceException("Department already exists with name: " + dto.getName()); });

        DepartmentDomain saved = repo.save(DepartmentMapper.toDomain(dto));
        return DepartmentMapper.toDTO(saved);
    }

    @Override
    public List<DepartmentDTO> getAllDepartments() {
        List<DepartmentDomain> all = repo.findAll();
        if (all.isEmpty()) {
            throw new ResourceNotFoundException("No departments found");
        }
        return all.stream().map(DepartmentMapper::toDTO).toList();
    }

    @Override
    public DepartmentDTO getById(Long id) {
        DepartmentDomain d = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id " + id));
        return DepartmentMapper.toDTO(d);
    }

    @Override
    public void deleteDepartment(Long id) {
        DepartmentDomain d = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id " + id));
        repo.delete(d);
    }
    @Override
    public DepartmentDTO updateDepartment(Long id, DepartmentDTO dto) {
        DepartmentDomain existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id " + id));

        existing.setName(dto.getName());
        DepartmentDomain saved = repo.save(existing);
        return DepartmentMapper.toDTO(saved);
    }

    @Override
    public DepartmentDTO getByName(String name) {
        DepartmentDomain d = repo.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with name " + name));
        return DepartmentMapper.toDTO(d);
    }

}
