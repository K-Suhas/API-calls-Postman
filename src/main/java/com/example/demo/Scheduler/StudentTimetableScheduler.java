package com.example.demo.Scheduler;

import com.example.demo.Service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class StudentTimetableScheduler {

    private static final Logger log = LoggerFactory.getLogger(StudentTimetableScheduler.class);

    private final EmailService emailService;

    public StudentTimetableScheduler(EmailService emailService) {
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 0 7 * * ?", zone = "Asia/Kolkata")
    public void scheduleDailyTimetable() {
        log.info("Scheduler triggered for daily student timetable mail");
        String dayName = LocalDate.now().getDayOfWeek().name();
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));

        // ✅ delegate to async method in EmailService
        emailService.sendTimetableEmailToAll(dayName, dateTime);
    }
}

