package com.example.demo.Resource;

import com.example.demo.DTO.UserDTO;
import com.example.demo.Enum.Role;
import com.example.demo.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class UserResource {

    private final UserService userService;
    public UserResource(UserService userService)
    {
        this.userService=userService;
    }

    @PostMapping("/google")
    public ResponseEntity<UserDTO> loginWithGoogle(@RequestBody Map<String, String> payload) {
        String idTokenString = payload.get("idToken");
        try {
            UserDTO user = userService.authenticateWithGoogle(idTokenString);
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception _) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/role")
    public ResponseEntity<Role> getRole(@RequestParam String email) {
        return ResponseEntity.ok(userService.getUserRole(email));
    }
    @PostMapping("/add-admin")
    public ResponseEntity<UserDTO> addAdmin(@RequestBody UserDTO userDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.addAdmin(userDTO));
    }

}
