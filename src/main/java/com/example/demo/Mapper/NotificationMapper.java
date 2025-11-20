package com.example.demo.Mapper;

import com.example.demo.DTO.NotificationDTO;
import com.example.demo.Domain.NotificationDomain;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {
    public NotificationDTO toDTO(NotificationDomain domain) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(domain.getId());
        dto.setMtitle(domain.getMtitle());
        dto.setMessage(domain.getMessage());
        dto.setTime(domain.getTime());
        dto.setRead(domain.isRead());
        return dto;
    }

    public NotificationDomain toEntity(NotificationDTO dto) {
        NotificationDomain domain = new NotificationDomain();
        domain.setId(dto.getId());
        domain.setMtitle(dto.getMtitle());
        domain.setMessage(dto.getMessage());
        domain.setTime(dto.getTime());
        domain.setRead(dto.isRead());
        return domain;
    }
}

