package com.example.demo.Service.Serviceimpl;

import com.example.demo.DTO.EmailDTO;
import com.example.demo.Domain.EmailDomain;
import com.example.demo.Enum.EmailStatus;
import com.example.demo.ExceptionHandler.EmailFailedException;
import com.example.demo.ExceptionHandler.EmailNotFoundException;
import com.example.demo.Mail.GmailOAuth2Sender;
import com.example.demo.Repository.EmailRepository;
import com.example.demo.Repository.StudentRepository;
import com.example.demo.Service.EmailService;
import com.example.demo.Service.GoogleTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private EmailRepository repository;

    @Autowired
    private StudentRepository studentRepository;  // ✅ Inject StudentRepository

    @Autowired
    private GoogleTokenService googleTokenService;

    @Autowired
    private GmailOAuth2Sender gmailSender;

    @Override
    public EmailDTO sendEmail(String toEmail, String subject, String body) {
        // ✅ Check if student exists
        studentRepository.findByEmail(toEmail)
                .orElseThrow(() -> new EmailFailedException(
                        "No student found with email: " + toEmail
                ));

        // ✅ Check if already sent
        boolean alreadySent = repository.existsByToEmailAndSubjectAndBodyAndStatus(
                toEmail, subject, body, EmailStatus.SENT);

        if (alreadySent) {
            throw new EmailFailedException(
                    "Email already sent to " + toEmail + " with same subject and body"
            );
        }

        EmailDomain notification = new EmailDomain();
        notification.setToEmail(toEmail);
        notification.setSubject(subject);
        notification.setBody(body);
        notification.setStatus(EmailStatus.PENDING);
        notification = repository.save(notification);

        try {
            String accessToken = googleTokenService.getAccessToken();
            gmailSender.send(toEmail, subject, body, accessToken);

            notification.setStatus(EmailStatus.SENT);
            notification.setSentTime(LocalDateTime.now());
        } catch (Exception e) {
            notification.setStatus(EmailStatus.FAILED);
            notification.setSentTime(LocalDateTime.now());
            throw new EmailFailedException("Failed to send email: " + e.getMessage());
        }

        repository.save(notification);
        return mapToDTO(notification);
    }



    @Override
    public List<EmailDTO> getAllNotifications() {
        List<EmailDomain> notifications = repository.findAll();
        if (notifications.isEmpty()) {
            throw new EmailNotFoundException("No email notifications found");
        }
        return notifications.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private EmailDTO mapToDTO(EmailDomain entity) {
        return new EmailDTO(
                entity.getId(),
                entity.getToEmail(),
                entity.getSubject(),
                entity.getBody(),
                entity.getStatus().name(),
                entity.getSentTime()
        );
    }
}

