package com.example.demo.Service;

import com.example.demo.DTO.NotificationDTO;

import java.util.List;

public interface NotificationService {
    NotificationDTO create(String title, String message);
    List<NotificationDTO> getAll();
    void markAsRead(Long id);
}

