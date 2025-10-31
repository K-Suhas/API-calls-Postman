package com.example.demo.Service.Serviceimpl;

import com.example.demo.DTO.UserDTO;
import com.example.demo.Domain.UserDomain;
import com.example.demo.Mapper.UserMapper;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDTO loginOrRegisterGoogleUser(String email, String name) {
        Optional<UserDomain> existing = userRepository.findByEmail(email);

        UserDomain user = existing.orElseGet(() -> {
            UserDomain newUser = new UserDomain();
            newUser.setEmail(email);
            newUser.setName(name);
            return userRepository.save(newUser);
        });

        return UserMapper.toDTO(user);
    }

    @Override
    public Optional<UserDTO> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserMapper::toDTO);
    }
}

