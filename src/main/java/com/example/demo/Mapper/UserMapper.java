package com.example.demo.Mapper;

import com.example.demo.Domain.UserDomain;
import com.example.demo.DTO.UserDTO;
import com.example.demo.Enum.Role;

public class UserMapper {
    public static UserDTO toDTO(UserDomain user) {
        return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    public static UserDomain toDomain(UserDTO dto) {
        return new UserDomain()
                .setId(dto.getId())
                .setName(dto.getName())
                .setEmail(dto.getEmail())
                .setRole(dto.getRole() != null ? dto.getRole() : Role.STUDENT);
    }
}

