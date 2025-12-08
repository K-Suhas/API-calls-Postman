package com.example.demo.Service.Serviceimpl;

import com.example.demo.DTO.EmailDTO;
import com.example.demo.Domain.EmailDomain;
import com.example.demo.Domain.StudentDomain;
import com.example.demo.Enum.EmailStatus;
import com.example.demo.ExceptionHandler.DuplicateEmailException;
import com.example.demo.ExceptionHandler.EmailFailedException;
import com.example.demo.ExceptionHandler.EmailNotFoundException;
import com.example.demo.ExceptionHandler.MailGatewayException;
import com.example.demo.Mail.GmailOAuth2Sender;
import com.example.demo.Repository.EmailRepository;
import com.example.demo.Repository.StudentRepository;
import com.example.demo.Repository.TeacherRepository;
import com.example.demo.Service.EmailService;
import com.example.demo.Service.GoogleTokenService;
import com.example.demo.Service.NotificationService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {
    private final EmailRepository repository;
    private final StudentRepository studentRepository;  // ✅ Inject StudentRepository
    private final GoogleTokenService googleTokenService;
    private final GmailOAuth2Sender gmailSender;
    private final NotificationService notificationService;
    private final TemplateEngine templateEngine;
    private final TeacherRepository teacherRepository;

    public EmailServiceImpl(EmailRepository repository,
                            StudentRepository studentRepository,
                            TeacherRepository teacherRepository,   // ✅ new
                            GoogleTokenService googleTokenService,
                            GmailOAuth2Sender gmailSender,
                            NotificationService notificationService,
                            TemplateEngine templateEngine) {
        this.repository = repository;
        this.gmailSender = gmailSender;
        this.googleTokenService = googleTokenService;
        this.notificationService = notificationService;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;   // ✅ new
        this.templateEngine = templateEngine;
    }


    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);


    @Override
    public EmailDTO sendEmail(String toEmail, String subject, String body) {
        boolean isStudent = studentRepository.findByEmail(toEmail).isPresent();
        boolean isTeacher = teacherRepository.findByEmail(toEmail).isPresent();

        if (!isStudent && !isTeacher) {
            throw new EmailNotFoundException("No student/teacher found with email: " + toEmail);
        }

        boolean alreadySent = repository.existsByToEmailAndSubjectAndBodyAndStatus(
                toEmail, subject, body, EmailStatus.SENT);

        if (alreadySent) {
            throw new DuplicateEmailException("Email already sent to " + toEmail + " with same subject and body");
        }

        EmailDomain notification = new EmailDomain()
                .setToEmail(toEmail)
                .setSubject(subject)
                .setBody(body)
                .setStatus(EmailStatus.PENDING);

        notification = repository.save(notification);

        try {
            String accessToken = googleTokenService.getAccessToken();
            gmailSender.send(toEmail, subject, body, accessToken);
            notificationService.create("Mail Sent", "Mail sent successfully to " + toEmail);

            notification.setStatus(EmailStatus.SENT);
            notification.setSentTime(LocalDateTime.now());
        } catch (Exception e) {
            notificationService.create("Mail Failed", "Failed to send mail to " + toEmail + ": " + e.getMessage());
            notification.setStatus(EmailStatus.FAILED);
            notification.setSentTime(LocalDateTime.now());
            repository.save(notification);
            throw new MailGatewayException("Failed to send via SMTP: " + e.getMessage());
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
        return notifications.stream().map(this::mapToDTO).toList();
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

    @Override
    public List<EmailDTO> sendEmailToAll(String subject, String body) {
        List<StudentDomain> students = studentRepository.findAll();
        if (students.isEmpty()) {
            throw new EmailFailedException("No students found to send email");
        }

        // Strict duplicate guard (pre-check)
        for (StudentDomain student : students) {
            boolean alreadySent = repository.existsByToEmailAndSubjectAndBodyAndStatus(
                    student.getEmail(), subject, body, EmailStatus.SENT);
            if (alreadySent) {
                throw new DuplicateEmailException(
                        "Duplicate detected before send: already sent to " + student.getEmail()
                );
            }
        }

        // Fail-fast (first failure aborts)
        return students.stream()
                .map(s -> sendEmail(s.getEmail(), subject, body)) // exceptions propagate
                .toList();
    }
    @Override
    public EmailDTO sendAdminEmail(String toEmail, String subject, String body, Resource attachment) {
        // No StudentRepository validation here
        EmailDomain notification = new EmailDomain()
                .setToEmail(toEmail)
                .setSubject(subject)
                .setBody(body)
                .setStatus(EmailStatus.PENDING);

        notification = repository.save(notification);

        try {
            String accessToken = googleTokenService.getAccessToken();

            // ✅ Pass filename explicitly as 5th argument
            gmailSender.sendWithAttachment(
                    toEmail,
                    subject,
                    body,
                    attachment,
                    "students_report.csv",   // filename for the attachment
                    accessToken
            );

            notificationService.create("Mail Sent", "Admin report mailed successfully to " + toEmail);

            notification.setStatus(EmailStatus.SENT);
            notification.setSentTime(LocalDateTime.now());
        } catch (Exception e) {
            notificationService.create("Mail Failed", "Failed to send admin mail to " + toEmail + ": " + e.getMessage());
            notification.setStatus(EmailStatus.FAILED);
            notification.setSentTime(LocalDateTime.now());
            repository.save(notification);
            throw new MailGatewayException("Failed to send admin mail: " + e.getMessage());
        }

        repository.save(notification);
        return mapToDTO(notification);
    }
    @Override
    public void sendTimetableEmailToAll(String dayName, String dateTime) {
        Context context = new Context();
        context.setVariable("dayName", dayName);
        context.setVariable("dateTime", dateTime);

        String htmlContent = templateEngine.process("timetable", context);

        // ✅ Use your existing sendEmailToAll but pass HTML
        studentRepository.findAll().forEach(student -> {
            try {
                String accessToken = googleTokenService.getAccessToken();
                gmailSender.send(student.getEmail(), dayName + " Timetable - " + dateTime, htmlContent, accessToken);
            } catch (Exception e) {
                log.error("Failed to send timetable mail to {}", student.getEmail(), e);
            }
        });
    }
    @Async
    @Override
    public void sendAnnouncementToAllStudents() {
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
        Context context = new Context();
        context.setVariable("dateTime", dateTime);

        String htmlContent = templateEngine.process("announcement", context);

        sendEmailToAll("All Students Assemble - " + dateTime, htmlContent);

        log.info("Announcement mail sent to all students at {}", dateTime);
    }
    // In EmailServiceImpl
    @Async
    @Override
    public void sendDailyReport(Resource csv, String subject, String body, String adminEmail) {
        sendAdminEmail(adminEmail, subject, body, csv);
        log.info("Daily student CSV report emailed to admin {}", adminEmail);
    }



}

