// src/main/java/com/example/demo/Service/DepartmentService.java
package com.example.demo.Service;

import com.example.demo.DTO.DepartmentDTO;

import java.util.List;

public interface DepartmentService {
    DepartmentDTO createDepartment(DepartmentDTO dto);
    List<DepartmentDTO> getAllDepartments();
    DepartmentDTO getById(Long id);
    void deleteDepartment(Long id);

    DepartmentDTO updateDepartment(Long id, DepartmentDTO dto);

    DepartmentDTO getByName(String name);
}
