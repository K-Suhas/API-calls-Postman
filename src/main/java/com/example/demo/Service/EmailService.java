package com.example.demo.Service;

import com.example.demo.DTO.EmailDTO;

import java.util.List;

public interface EmailService {
    EmailDTO sendEmail(String toEmail, String subject, String body);
    List<EmailDTO> getAllNotifications();
}
