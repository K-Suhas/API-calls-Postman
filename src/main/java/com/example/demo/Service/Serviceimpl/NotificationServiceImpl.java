package com.example.demo.Service.Serviceimpl;

import com.example.demo.DTO.NotificationDTO;
import com.example.demo.Domain.NotificationDomain;
import com.example.demo.Mapper.NotificationMapper;
import com.example.demo.Repository.NotificationRepository;
import com.example.demo.Service.NotificationService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository repo;
    private final NotificationMapper mapper;
    public NotificationServiceImpl(NotificationRepository repo,NotificationMapper mapper)
    {
        this.repo=repo;
        this.mapper=mapper;
    }

    @Override
    public NotificationDTO create(String title, String message) {
        NotificationDomain n = new NotificationDomain();
        n.setMtitle(title);
        n.setMessage(message);
        n.setTime(LocalDateTime.now());
        n.setRead(false);
        return mapper.toDTO(repo.save(n));
    }

    @Override
    public List<NotificationDTO> getAll() {
        return repo.findAll(Sort.by(Sort.Direction.DESC, "time"))
                .stream().map(mapper::toDTO).toList();
    }

    @Override
    public void markAsRead(Long id) {
        repo.findById(id).ifPresent(n -> {
            n.setRead(true);
            repo.save(n);
        });
    }
}

