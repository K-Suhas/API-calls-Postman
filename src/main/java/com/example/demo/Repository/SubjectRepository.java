// src/main/java/com/example/demo/Repository/SubjectRepository.java
package com.example.demo.Repository;

import com.example.demo.Domain.SubjectDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<SubjectDomain, Long> {
    List<SubjectDomain> findByDepartment_IdAndSemester(Long departmentId, int semester);
    Optional<SubjectDomain> findByNameIgnoreCaseAndDepartment_IdAndSemester(String name, Long departmentId, int semester);
}
