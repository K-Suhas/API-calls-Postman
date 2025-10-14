package com.example.demo.Service;

import com.example.demo.Domain.User;

import java.util.List;

public interface UserService {
    String createUser(User user);
    User getUserById(Long id);
    List<User> getAllUsers();
    String updateUser(Long id, User user);
    String deleteUser(Long id);
}
