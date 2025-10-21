package com.example.demo.Service;

import com.example.demo.DTO.UserDTO;

import java.util.List;

public interface UserService {
    String createUser(UserDTO user);
    UserDTO getUserById(Long id);
    List<UserDTO> getAllUsers();
    String updateUser(Long id, UserDTO user);
    String deleteUser(Long id);
}
