package com.example.demo.Repository;

import com.example.demo.Domain.StudentDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface StudentRepository extends JpaRepository<StudentDomain, Long> {

    // Search by name or department (case-insensitive)
    @Query("SELECT s FROM StudentDomain s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(s.dept) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<StudentDomain> searchByNameOrDept(@Param("query") String query);

    // Search by ID, name, or department
    @Query("SELECT s FROM StudentDomain s WHERE s.id = :id OR LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(s.dept) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<StudentDomain> searchByIdOrNameOrDept(@Param("id") Long id, @Param("query") String query);

    // Search by date of birth
    @Query(value = "SELECT * FROM Student WHERE DATE(dob) = :dob", nativeQuery = true)
    List<StudentDomain> searchByDob(@Param("dob") LocalDate dob);


}
