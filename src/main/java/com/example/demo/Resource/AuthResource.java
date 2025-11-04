package com.example.demo.Resource;

import com.example.demo.DTO.UserDTO;
import com.example.demo.Service.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthResource {

    @Autowired
    private UserService userService;

    @Value("${google.clientId}")
    private String clientId;

    @PostMapping("/google")
    public ResponseEntity<UserDTO> loginWithGoogle(@RequestBody Map<String, String> payload) throws Exception {
        String token = payload.get("token");

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList( clientId))
                .build();

        GoogleIdToken idToken = verifier.verify(token);
        if (idToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        GoogleIdToken.Payload googlePayload = idToken.getPayload();
        String email = googlePayload.getEmail();
        String name = (String) googlePayload.get("name");

        UserDTO user = userService.loginOrRegisterGoogleUser(email, name);
        return ResponseEntity.ok(user);
    }
}
