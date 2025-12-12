// src/main/java/com/example/demo/Repository/TeacherRepository.java
package com.example.demo.Repository;

import com.example.demo.Domain.TeacherDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<TeacherDomain, Long> {

    Optional<TeacherDomain> findByEmailIgnoreCase(String email);

    @Query("""
       SELECT t FROM TeacherDomain t 
       WHERE (:id IS NOT NULL AND t.id = :id)
          OR (:id IS NULL AND (
                LOWER(t.name) = LOWER(:query)
             OR LOWER(t.department.name) = LOWER(:query)
             OR LOWER(t.email) = LOWER(:query)
          ))
       """)
    Page<TeacherDomain> searchByIdOrNameOrDeptOrEmail(@Param("id") Long id,
                                                      @Param("query") String query,
                                                      Pageable pageable);

    List<TeacherDomain> findByDepartment_Id(Long departmentId);
}
