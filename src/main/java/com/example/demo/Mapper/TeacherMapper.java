// src/main/java/com/example/demo/Mapper/TeacherMapper.java
package com.example.demo.Mapper;

import com.example.demo.Domain.DepartmentDomain;
import com.example.demo.Domain.TeacherDomain;
import com.example.demo.DTO.TeacherDTO;

public class TeacherMapper {

    private TeacherMapper() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    public static TeacherDTO toDTO(TeacherDomain teacher) {
        if (teacher == null) return null;

        TeacherDTO dto = new TeacherDTO();
        dto.setId(teacher.getId());
        dto.setName(teacher.getName());
        dto.setEmail(teacher.getEmail());

        if (teacher.getDepartment() != null) {
            dto.setDepartmentId(teacher.getDepartment().getId());
            dto.setDepartmentName(teacher.getDepartment().getName());
        }

        return dto;
    }

    public static TeacherDomain toDomain(TeacherDTO dto, DepartmentDomain department) {
        if (dto == null) return null;

        return new TeacherDomain()
                .setId(dto.getId())
                .setName(dto.getName())
                .setEmail(dto.getEmail())
                .setDepartment(department);
    }

    public static TeacherDomain toDomain(TeacherDTO dto) {
        return toDomain(dto, null);
    }
}
