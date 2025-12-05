package com.example.demo.Scheduler;

import com.example.demo.Service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class StudentAnnouncementScheduler {

    private static final Logger log = LoggerFactory.getLogger(StudentAnnouncementScheduler.class);

    private final EmailService emailService;

    public StudentAnnouncementScheduler(EmailService emailService) {
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 */5 * * * ?", zone = "Asia/Kolkata")
    public void scheduleDailyAnnouncement() {
        log.info("Scheduler triggered for daily student announcement mail");
        emailService.sendAnnouncementToAllStudents(); // ✅ goes through proxy
    }
}

