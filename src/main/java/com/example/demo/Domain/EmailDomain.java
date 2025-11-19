package com.example.demo.Domain;

import com.example.demo.Enum.EmailStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(
        name = "email",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"to_email", "subject", "body"})
        }
)
public class EmailDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "to_email", nullable = false)
    private String toEmail;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;   // ✅ new field

    @Enumerated(EnumType.STRING)
    private EmailStatus status;

    private LocalDateTime sentTime;
}
