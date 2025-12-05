package com.example.demo.Service;

import com.example.demo.DTO.EmailDTO;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface EmailService {
    EmailDTO sendEmail(String toEmail, String subject, String body);
    List<EmailDTO> getAllNotifications();
    List<EmailDTO> sendEmailToAll(String subject, String body);

    EmailDTO sendAdminEmail(String toEmail, String subject, String body, Resource attachment);

    void sendTimetableEmailToAll(String dayName, String dateTime);

    @Async
    void sendAnnouncementToAllStudents();

    // In EmailServiceImpl
    @Async
    void sendDailyReport(Resource csv, String subject, String body, String adminEmail);
}
