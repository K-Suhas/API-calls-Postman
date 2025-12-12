// src/main/java/com/example/demo/Mapper/CourseMapper.java
package com.example.demo.Mapper;

import com.example.demo.DTO.CourseDTO;
import com.example.demo.Domain.CourseDomain;
import com.example.demo.Domain.StudentDomain;

public class CourseMapper {

    private CourseMapper() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    public static CourseDTO toDTO(CourseDomain domain) {
        if (domain == null) return null;

        CourseDTO dto = new CourseDTO();
        dto.setId(domain.getId());
        dto.setName(domain.getName());

        if (domain.getDepartment() != null) {
            dto.setDepartmentId(domain.getDepartment().getId());
            dto.setDepartmentName(domain.getDepartment().getName());
        }

        if (domain.getStudents() != null) {
            dto.setStudentNames(domain.getStudents().stream()
                    .map(StudentDomain::getName)
                    .toList());
        }

        return dto;
    }

    public static CourseDomain toDomain(CourseDTO dto) {
        if (dto == null) return null;

        CourseDomain domain = new CourseDomain();
        domain.setId(dto.getId());
        domain.setName(dto.getName());
        // department is set in service
        return domain;
    }
}
