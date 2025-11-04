package com.example.demo.Repository;

import com.example.demo.Domain.CourseDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<CourseDomain, Long> {

    // Search by course name (case-insensitive) with pagination
    @Query("SELECT c FROM CourseDomain c WHERE c.id = :id OR LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<CourseDomain> searchByIdOrName(@Param("id") Long id, @Param("query") String query, Pageable pageable);

    @Query("SELECT c FROM CourseDomain c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<CourseDomain> searchByName(@Param("query") String query, Pageable pageable);

    List<CourseDomain> findByNameIgnoreCase(String name);
    List<CourseDomain> findByNameInIgnoreCase(List<String> names);



}
