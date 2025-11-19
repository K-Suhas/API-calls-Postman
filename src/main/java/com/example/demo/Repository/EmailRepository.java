package com.example.demo.Repository;

import com.example.demo.Domain.EmailDomain;
import com.example.demo.Enum.EmailStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<EmailDomain,Long> {
    boolean existsByToEmailAndSubjectAndBodyAndStatus(String toEmail, String subject, String body, EmailStatus status);

}
