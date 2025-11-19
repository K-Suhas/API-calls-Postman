package com.example.demo.Service.Serviceimpl;

import com.example.demo.DTO.UserDTO;
import com.example.demo.Domain.UserDomain;
import com.example.demo.Enum.Role;
import com.example.demo.Mapper.UserMapper;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Value;
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Value("${google.clientId}")
    private String clientId;

    @Override
    public UserDTO authenticateWithGoogle(String idTokenString) {
        System.out.println(idTokenString);
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                JacksonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(clientId))
                .build();

        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                String name = (String) payload.get("name");
                return loginOrRegisterGoogleUser(email, name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public UserDTO loginOrRegisterGoogleUser(String email, String name) {
        Optional<UserDomain> existing = userRepository.findByEmail(email);
        UserDomain user = existing.orElseGet(() -> {
            UserDomain newUser = new UserDomain();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setRole(Role.STUDENT);
            return userRepository.save(newUser);
        });
        return UserMapper.toDTO(user);
    }

    @Override
    public Role getUserRole(String email) {
        return userRepository.findByEmail(email)
                .map(UserDomain::getRole)
                .orElse(Role.STUDENT);
    }

    // ✅ Added missing method
    @Override
    public Optional<UserDTO> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserMapper::toDTO);
    }
}



