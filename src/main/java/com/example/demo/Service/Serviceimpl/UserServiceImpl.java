package com.example.demo.Service.Serviceimpl;

import com.example.demo.DTO.UserDTO;
import com.example.demo.Domain.UserDomain;
import com.example.demo.Enum.Role;
import com.example.demo.ExceptionHandler.DuplicateEmailException;
import com.example.demo.Mapper.UserMapper;
import com.example.demo.Repository.StudentRepository;
import com.example.demo.Repository.TeacherRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.UserService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    public UserServiceImpl(UserRepository userRepository, TeacherRepository teacherRepository,StudentRepository studentRepository) {
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
    }

    @Value("${google.clientId}")
    private String clientId;


    @Override
    public UserDTO authenticateWithGoogle(String idTokenString) {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance())   // ✅ use GsonFactory instead of JacksonFactory
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

            if (teacherRepository.findByEmailIgnoreCase(email).isPresent()) {
                newUser.setRole(Role.TEACHER);
            } else {
                newUser.setRole(Role.STUDENT);
            }

            return userRepository.save(newUser);
        });

        UserDTO dto = UserMapper.toDTO(user);

        if (dto.getRole() == Role.STUDENT) {
            studentRepository.findByEmail(email).ifPresent(s -> {
                dto.setDepartmentId(s.getDepartment().getId());
                dto.setDepartmentName(s.getDepartment().getName());
                // ✅ Add the real student id
                dto.setId(s.getId());
            });
        } else if (dto.getRole() == Role.TEACHER) {
            teacherRepository.findByEmailIgnoreCase(email).ifPresent(t -> {
                dto.setDepartmentId(t.getDepartment().getId());
                dto.setDepartmentName(t.getDepartment().getName());
                // ✅ Add the real teacher id if needed
                dto.setId(t.getId());
            });
        }

        return dto;
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
    @Override
    public UserDTO addAdmin(UserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new DuplicateEmailException("Email already exists");
        }

        UserDomain user = new UserDomain();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setRole(Role.valueOf("ADMIN"));

        return UserMapper.toDTO(userRepository.save(user));
    }
    @Override
    public Optional<Long> getDepartmentIdForTeacher(String email) {
        return teacherRepository.findByEmailIgnoreCase(email)
                .map(t -> t.getDepartment().getId());
    }

    @Override
    public Optional<Long> getDepartmentIdForStudent(String email) {
        return studentRepository.findByEmail(email)
                .map(s -> s.getDepartment().getId());
    }


}



