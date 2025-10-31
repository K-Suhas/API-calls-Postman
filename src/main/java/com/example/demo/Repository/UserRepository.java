package com.example.demo.Repository;

import com.example.demo.Domain.UserDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserDomain, Long> {
    Optional<UserDomain> findByEmail(String email);
}

