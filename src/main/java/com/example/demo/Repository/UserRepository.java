package com.example.demo.Repository;

import com.example.demo.DTO.UserDTO;

import com.example.demo.Domain.UserDomain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserDomain, Long> {
}
