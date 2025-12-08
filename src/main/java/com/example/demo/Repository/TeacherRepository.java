package com.example.demo.Repository;

import com.example.demo.Domain.TeacherDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<TeacherDomain, Long> {

    Optional<TeacherDomain> findByEmail(String email);

    @Query("SELECT t FROM TeacherDomain t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(t.dept) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(t.email) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<TeacherDomain> searchByNameDeptOrEmail(@Param("query") String query, Pageable pageable);
}
