// src/main/java/com/example/demo/Mapper/DepartmentMapper.java
package com.example.demo.Mapper;

import com.example.demo.DTO.DepartmentDTO;
import com.example.demo.Domain.DepartmentDomain;

public class DepartmentMapper {

    private DepartmentMapper() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    public static DepartmentDTO toDTO(DepartmentDomain domain) {
        if (domain == null) return null;
        return new DepartmentDTO(domain.getId(), domain.getName());
    }

    public static DepartmentDomain toDomain(DepartmentDTO dto) {
        if (dto == null) return null;
        return new DepartmentDomain()
                .setId(dto.getId())
                .setName(dto.getName());
    }
}
