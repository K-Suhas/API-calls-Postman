// src/main/java/com/example/demo/Repository/DepartmentRepository.java
package com.example.demo.Repository;

import com.example.demo.Domain.DepartmentDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<DepartmentDomain, Long> {
    Optional<DepartmentDomain> findByNameIgnoreCase(String name);
}
