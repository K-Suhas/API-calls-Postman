package com.example.demo.Resource;

import com.example.demo.DTO.EmailDTO;
import com.example.demo.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/email")
@CrossOrigin(origins = "http://localhost:4200")
public class EmailResource {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<EmailDTO> sendEmail(@RequestParam String toEmail,
                                              @RequestParam String subject,
                                              @RequestParam String body) {
        EmailDTO result = emailService.sendEmail(toEmail, subject, body);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/status")
    public ResponseEntity<List<EmailDTO>> getAllStatuses() {
        return ResponseEntity.ok(emailService.getAllNotifications());
    }
    @PostMapping("/sendAll")
    public ResponseEntity<List<EmailDTO>> sendEmailToAll(@RequestParam String subject,
                                                         @RequestParam String body) {
        List<EmailDTO> results = emailService.sendEmailToAll(subject, body);
        return ResponseEntity.ok(results);
    }



}
