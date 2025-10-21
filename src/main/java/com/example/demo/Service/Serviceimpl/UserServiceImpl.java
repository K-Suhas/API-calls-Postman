package com.example.demo.Service.Serviceimpl;

import com.example.demo.DTO.UserDTO;
import com.example.demo.Service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private List<UserDTO> users = new ArrayList<>();

    @Override
    public String createUser(UserDTO user) {
        users.add(user);
        return "User created: " + user.getName();
    }

    @Override
    public UserDTO getUserById(Long id) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return users;
    }

    @Override
    public String updateUser(Long id, UserDTO updatedUser) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(id)) {
                users.set(i, updatedUser);
                return "User updated: " + updatedUser.getName();
            }
        }
        return "User not found with ID: " + id;
    }

    @Override
    public String deleteUser(Long id) {
        boolean removed = users.removeIf(u -> u.getId().equals(id));
        return removed ? "User deleted with ID: " + id : "User not found with ID: " + id;
    }
}

