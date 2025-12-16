package com.example.demo.Mapper;

import com.example.demo.DTO.SubjectDTO;
import com.example.demo.Domain.SubjectDomain;

public class SubjectMapper {
    private SubjectMapper() {}

    public static SubjectDTO toDTO(SubjectDomain domain) {
        if (domain == null) return null;
        SubjectDTO dto = new SubjectDTO()
                .setId(domain.getId())
                .setName(domain.getName())
                .setSemester(domain.getSemester());
        if (domain.getDepartment() != null) {
            dto.setDepartmentId(domain.getDepartment().getId());
            dto.setDepartmentName(domain.getDepartment().getName());
        }
        return dto;
    }
}