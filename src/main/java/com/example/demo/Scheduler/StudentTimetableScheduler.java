package com.example.demo.Scheduler;

import com.example.demo.Service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class StudentTimetableScheduler {
    private static final Logger log = LoggerFactory.getLogger(StudentTimetableScheduler.class);

    private final EmailService emailService;
    private final TemplateEngine templateEngine;

    public StudentTimetableScheduler(EmailService emailService, TemplateEngine templateEngine) {
        this.emailService = emailService;
        this.templateEngine = templateEngine;
    }

    // ✅ Run every day at 7 AM IST "0 0 7 * * ?"
    @Scheduled(cron = "0 0 7 * * ?", zone = "Asia/Kolkata")
    public void scheduleDailyTimetable() {
        log.info("Scheduler triggered for daily student timetable mail");
        sendTimetableToAllStudents();
    }

    @Async
    public void sendTimetableToAllStudents() {
        try {
            String dayName = LocalDate.now().getDayOfWeek().name();
            String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));

            // ✅ Build Thymeleaf context
            Context context = new Context();
            context.setVariable("dayName", capitalize(dayName));
            context.setVariable("dateTime", dateTime);

            // ✅ Render timetable.html template
            String htmlContent = templateEngine.process("timetable", context);

            // ✅ Use EmailService to send HTML mail to all students
            emailService.sendEmailToAll(capitalize(dayName) + " Timetable - " + dateTime, htmlContent);

            log.info("Timetable mail sent to all students for {}", dayName);
        } catch (Exception e) {
            log.error("Failed to send timetable mail", e);
        }
    }

    private String capitalize(String day) {
        return day.substring(0,1).toUpperCase() + day.substring(1).toLowerCase();
    }
}
