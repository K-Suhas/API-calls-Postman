package com.example.demo.Mapper;

import com.example.demo.Domain.UserDomain;
import com.example.demo.DTO.UserDTO;

public class UserMapper {
    public static UserDTO toDTO(UserDomain user) {
        return new UserDTO(user.getId(), user.getName(), user.getEmail());
    }

    public static UserDomain toDomain(UserDTO dto) {
        UserDomain user = new UserDomain();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        return user;
    }
}
