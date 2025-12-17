package com.example.demo.Mapper;

import com.example.demo.Domain.UserDomain;
import com.example.demo.DTO.UserDTO;
import com.example.demo.Enum.Role;

public class UserMapper {
    private UserMapper() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    public static UserDTO toDTO(UserDomain user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        // departmentId and departmentName will be set in the service
        return dto;
    }

    public static UserDomain toDomain(UserDTO dto) {
        return new UserDomain()
                .setId(dto.getId())
                .setName(dto.getName())
                .setEmail(dto.getEmail())
                .setRole(dto.getRole() != null ? dto.getRole() : Role.STUDENT);
    }
}
