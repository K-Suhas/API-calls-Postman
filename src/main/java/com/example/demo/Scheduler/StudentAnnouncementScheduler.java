package com.example.demo.Scheduler;

import com.example.demo.Service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class StudentAnnouncementScheduler {
    private static final Logger log = LoggerFactory.getLogger(StudentAnnouncementScheduler.class);

    private final EmailService emailService;
    private final TemplateEngine templateEngine;

    public StudentAnnouncementScheduler(EmailService emailService, TemplateEngine templateEngine) {
        this.emailService = emailService;
        this.templateEngine = templateEngine;
    }

    // ✅ Schedule announcement every day at 8 AM IST
    @Scheduled(cron = "0 */5 * * * ?", zone = "Asia/Kolkata")
    public void scheduleDailyAnnouncement() {
        log.info("Scheduler triggered for daily student announcement mail");
        sendAnnouncementToAllStudents();
    }

    @Async
    public void sendAnnouncementToAllStudents() {
        try {
            String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));

            // ✅ Build Thymeleaf context
            Context context = new Context();
            context.setVariable("dateTime", dateTime);

            // ✅ Render announcement.html template
            String htmlContent = templateEngine.process("announcement", context);

            // ✅ Send mail to all students
            emailService.sendEmailToAll("All Students Assemble - " + dateTime, htmlContent);

            log.info("Announcement mail sent to all students at {}", dateTime);
        } catch (Exception e) {
            log.error("Failed to send announcement mail", e);
        }
    }
}
