// src/main/java/com/example/demo/Mapper/StudentMapper.java
package com.example.demo.Mapper;

import com.example.demo.DTO.StudentDTO;
import com.example.demo.Domain.CourseDomain;
import com.example.demo.Domain.DepartmentDomain;
import com.example.demo.Domain.StudentDomain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

public class StudentMapper {

    private StudentMapper() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    // ✅ Domain → DTO
    public static StudentDTO toDTO(StudentDomain domain) {
        if (domain == null) return null;

        StudentDTO dto = new StudentDTO();
        dto.setId(domain.getId());
        dto.setName(domain.getName());
        dto.setEmail(domain.getEmail());

        // LocalDate → String
        if (domain.getDob() != null) {
            dto.setDob(domain.getDob().format(FORMATTER));
        }

        if (domain.getDepartment() != null) {
            dto.setDepartmentId(domain.getDepartment().getId());
            dto.setDepartmentName(domain.getDepartment().getName());
        }

        if (domain.getCourses() != null) {
            dto.setCourseNames(domain.getCourses().stream()
                    .map(CourseDomain::getName)
                    .toList());
        }

        return dto;
    }

    // ✅ DTO → Domain
    public static StudentDomain toDomain(StudentDTO dto, DepartmentDomain department, List<CourseDomain> courses) {
        if (dto == null) return null;

        StudentDomain domain = new StudentDomain();
        domain.setId(dto.getId());
        domain.setName(dto.getName());
        domain.setEmail(dto.getEmail());

        // String → LocalDate
        if (dto.getDob() != null && !dto.getDob().isBlank()) {
            domain.setDob(LocalDate.parse(dto.getDob(), FORMATTER));
        }

        domain.setDepartment(department);

        if (courses != null) {
            domain.setCourses(Set.copyOf(courses));
        }

        return domain;
    }

    // Simple version (department & courses set later in service)
    public static StudentDomain toDomain(StudentDTO dto) {
        return toDomain(dto, null, null);
    }
}
