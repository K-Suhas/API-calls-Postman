package com.example.demo.DTO;

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
public class NotificationDTO {
    private Long id;
    private String mtitle;
    private String message;
    private LocalDateTime time;
    private boolean read;
}

