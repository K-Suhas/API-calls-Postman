package com.example.demo.Service;

import com.example.demo.DTO.UserDTO;
import com.example.demo.Enum.Role;

import java.util.Optional;

public interface UserService {
    UserDTO loginOrRegisterGoogleUser(String email, String name);
    Optional<UserDTO> findByEmail(String email);
    Role getUserRole(String email);

}

