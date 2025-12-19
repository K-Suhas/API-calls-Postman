package com.example.demo.Repository;

import com.example.demo.Domain.StudentSubjectChoiceDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentSubjectChoiceRepository extends JpaRepository<StudentSubjectChoiceDomain, Long> {
    Optional<StudentSubjectChoiceDomain> findByStudentIdAndSemester(Long studentId, Integer semester);
    boolean existsByStudentIdAndSemester(Long studentId, Integer semester);
}

