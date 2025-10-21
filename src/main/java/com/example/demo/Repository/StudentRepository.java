package com.example.demo.Repository;

import com.example.demo.DTO.StudentDTO;
import com.example.demo.Domain.StudentDomain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<StudentDomain,Long> {
}
