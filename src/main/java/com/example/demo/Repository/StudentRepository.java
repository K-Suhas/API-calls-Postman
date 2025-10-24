package com.example.demo.Repository;

import com.example.demo.Domain.StudentDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface StudentRepository extends JpaRepository<StudentDomain, Long> {

    // Search by name or department (case-insensitive)
    List<StudentDomain> findByNameContainingIgnoreCaseOrDeptContainingIgnoreCase(String name, String dept);

    // Search by ID or name or department
    List<StudentDomain> findByIdOrNameContainingIgnoreCaseOrDeptContainingIgnoreCase(Long id, String name, String dept);
    List<StudentDomain> findByDob(Date dob);


}
