package com.example.demo.Resource;

import com.example.demo.DTO.NotificationDTO;
import com.example.demo.Service.NotificationService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = "http://localhost:4200")
public class NotificationResource {

    private final NotificationService service;
    public NotificationResource(NotificationService service)
    {
        this.service=service;

    }

    @GetMapping
    public List<NotificationDTO> getAll() {
        return service.getAll();
    }

    @PostMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id) {
        service.markAsRead(id);
    }
}

