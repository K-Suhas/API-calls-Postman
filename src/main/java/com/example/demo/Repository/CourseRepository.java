package com.example.demo.Repository;

import com.example.demo.Domain.CourseDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<CourseDomain, Long> {

    @Query("SELECT c FROM CourseDomain c WHERE (:id IS NOT NULL AND c.id = :id) OR (:id IS NULL AND LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<CourseDomain> searchByIdOrName(@Param("id") Long id, @Param("query") String query, Pageable pageable);

    @Query("SELECT c FROM CourseDomain c WHERE LOWER(c.name) = LOWER(:query)")
    Page<CourseDomain> searchByName(@Param("query") String query, Pageable pageable);

    @Query("""
           SELECT c FROM CourseDomain c 
           WHERE c.department.id = :deptId
             AND (
               (:id IS NOT NULL AND c.id = :id)
               OR (:id IS NULL AND LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')))
             )
           """)
    Page<CourseDomain> searchByIdOrNameInDepartment(@Param("deptId") Long deptId,
                                                    @Param("id") Long id,
                                                    @Param("query") String query,
                                                    Pageable pageable);

    List<CourseDomain> findByNameIgnoreCase(String name);
    List<CourseDomain> findByNameInIgnoreCase(List<String> names);

    List<CourseDomain> findByDepartment_Id(Long departmentId);
}
