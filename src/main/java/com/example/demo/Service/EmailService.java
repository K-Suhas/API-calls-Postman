package com.example.demo.Service;

import com.example.demo.DTO.EmailDTO;
import org.springframework.core.io.Resource;

import java.util.List;

public interface EmailService {
    EmailDTO sendEmail(String toEmail, String subject, String body);
    List<EmailDTO> getAllNotifications();
    List<EmailDTO> sendEmailToAll(String subject, String body);

    EmailDTO sendAdminEmail(String toEmail, String subject, String body, Resource attachment);

    void sendTimetableEmailToAll(String dayName, String dateTime);
}
