// src/main/java/com/example/demo/scheduler/StudentReportScheduler.java
package com.example.demo.Scheduler;

import com.example.demo.Domain.UserDomain;
import com.example.demo.Enum.Role;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.ReportService;
import com.example.demo.Service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class StudentReportScheduler {
    private static final Logger log = LoggerFactory.getLogger(StudentReportScheduler.class);

    private final ReportService reportService;
    private final EmailService emailService;
    private final UserRepository userRepository;

    public StudentReportScheduler(ReportService reportService,
                                  EmailService emailService,
                                  UserRepository userRepository) {
        this.reportService = reportService;
        this.emailService = emailService;
        this.userRepository = userRepository;
    }

    @Scheduled(cron = "${report.scheduler.cron}", zone = "Asia/Kolkata")
    public void scheduleDailyReport() {
        log.info("Scheduler triggered for daily student CSV report");
        generateAndSendReport();
    }

    @Async
    public void generateAndSendReport() {
        try {
            Resource csv = reportService.generateCsvReport(null);
            String subject = "Daily Students Report - " + LocalDate.now();
            String body = "Attached is the daily students report.";

            // ✅ Fetch admin email from User table
            String adminEmail = userRepository.findByRole(Role.ADMIN)
                    .map(UserDomain::getEmail)
                    .orElseThrow(() -> new RuntimeException("No admin user found"));

            // ✅ Use a new method in EmailService that doesn’t require student validation
            emailService.sendAdminEmail(adminEmail, subject, body, csv);

            log.info("Daily student CSV report emailed to admin {}", adminEmail);
        } catch (Exception e) {
            log.error("Failed to generate/send student CSV report", e);
        }
    }
}

