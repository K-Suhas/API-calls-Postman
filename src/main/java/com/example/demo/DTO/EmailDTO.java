package com.example.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmailDTO {
    private Long id;
    private String toEmail;
    private String subject;
    private String body;      // ✅ new field
    private String status;
    private LocalDateTime sentTime;
}
