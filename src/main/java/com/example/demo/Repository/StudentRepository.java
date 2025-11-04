package com.example.demo.Repository;

import com.example.demo.Domain.StudentDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<StudentDomain, Long> {

    @Query("SELECT s FROM StudentDomain s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(s.dept) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<StudentDomain> searchByNameOrDept(@Param("query") String query, Pageable pageable);

    @Query("SELECT s FROM StudentDomain s WHERE s.id = :id OR LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(s.dept) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<StudentDomain> searchByIdOrNameOrDept(@Param("id") Long id, @Param("query") String query, Pageable pageable);

    @Query(value = "SELECT * FROM Student WHERE DATE(dob) = :dob", nativeQuery = true)
    Page<StudentDomain> searchByDob(@Param("dob") LocalDate dob, Pageable pageable);

    List<StudentDomain> findByNameAndDobAndDept(String name, LocalDate dob, String dept);
    @Query("SELECT s FROM StudentDomain s WHERE s.name = :name AND s.dob = :dob AND s.dept = :dept")
    Optional<StudentDomain> findExistingStudent(@Param("name") String name, @Param("dob") LocalDate dob, @Param("dept") String dept);


}
