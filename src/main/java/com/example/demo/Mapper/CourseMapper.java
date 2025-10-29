package com.example.demo.Mapper;

import com.example.demo.DTO.CourseDTO;
import com.example.demo.Domain.CourseDomain;
import com.example.demo.Domain.StudentDomain;

import java.util.stream.Collectors;

public class CourseMapper {
    public static CourseDTO toDTO(CourseDomain domain) {
        if (domain == null) return null;

        CourseDTO dto = new CourseDTO();
        dto.setId(domain.getId());
        dto.setName(domain.getName());

        if (domain.getStudents() != null) {
            dto.setStudentNames(domain.getStudents().stream()
                    .map(StudentDomain::getName)
                    .collect(Collectors.toList()));
        }

        return dto;
    }


    public static CourseDomain toDomain(CourseDTO dto) {
        if (dto == null) return null;

        CourseDomain domain = new CourseDomain();
        domain.setId(dto.getId());
        domain.setName(dto.getName());

        return domain;
    }
}
