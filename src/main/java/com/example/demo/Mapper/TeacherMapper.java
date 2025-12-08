package com.example.demo.Mapper;

import com.example.demo.Domain.TeacherDomain;
import com.example.demo.DTO.TeacherDTO;

public class TeacherMapper {
    private TeacherMapper() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    public static TeacherDTO toDTO(TeacherDomain teacher) {
        return new TeacherDTO(
                teacher.getId(),
                teacher.getName(),
                teacher.getEmail(),
                teacher.getDept()
        );
    }

    public static TeacherDomain toDomain(TeacherDTO dto) {
        return new TeacherDomain()
                .setId(dto.getId())
                .setName(dto.getName())
                .setEmail(dto.getEmail())
                .setDept(dto.getDept());
    }
}
