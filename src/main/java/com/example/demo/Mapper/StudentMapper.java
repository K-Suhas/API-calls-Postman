package com.example.demo.Mapper;

import com.example.demo.DTO.StudentDTO;
import com.example.demo.Domain.CourseDomain;
import com.example.demo.Domain.StudentDomain;

import java.util.stream.Collectors;

public class StudentMapper {

    public static StudentDTO toDTO(StudentDomain domain) {
        if (domain == null) return null;

        StudentDTO dto = new StudentDTO();
        dto.setId(domain.getId());
        dto.setName(domain.getName());
        dto.setDept(domain.getDept());
        dto.setDob(domain.getDob());

        if (domain.getCourses() != null) {
            dto.setCourseNames(domain.getCourses().stream()
                    .map(CourseDomain::getName)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public static StudentDomain toDomain(StudentDTO dto) {
        if (dto == null) return null;

        StudentDomain domain = new StudentDomain();
        domain.setId(dto.getId());
        domain.setName(dto.getName());
        domain.setDept(dto.getDept());
        domain.setDob(dto.getDob());

        return domain;
    }
}
